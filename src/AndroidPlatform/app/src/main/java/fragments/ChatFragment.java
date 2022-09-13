package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import classes.Contact;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activities.PairChatActivity;
import com.example.activities.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import static classes.Contact.deleteContact;
import static classes.Functions.downloadImage;

/**
 *
 * Reference: https://www.youtube.com/channel/UCIHBIPape0dWHKANkivrcJw/about
 * Coding Cafe Whatsapp tutorials aided with Messaging System
 *
 * ChatFragment holds list of private chat groups for 1-1 messaging
 *
 */
public class ChatFragment extends Fragment
{
    private RecyclerView chatList;
    private DatabaseReference chatDatabaseReference, userRef, contactRef;
    private StorageReference profile_imagesRef;
    private String user_name;
    private String currentUserID;

    public ChatFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View privateChatView = inflater.inflate(R.layout.fragment_chat, container, false);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        chatDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatList = privateChatView.findViewById(R.id.chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>().setQuery(chatDatabaseReference, Contact.class).build();
        FirebaseRecyclerAdapter<Contact, ChatViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ChatViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contact model)
            {
                final String list_of_userID = getRef(position).getKey();

                assert list_of_userID != null;
                userRef.child(list_of_userID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild("profile_photo_url"))
                        {
                            //USERNAME
                            user_name = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("surname").getValue().toString();
                            //USERINFO
                            String mobileNo = dataSnapshot.child("mobile_no").getValue().toString();
                            String email = dataSnapshot.child("email").getValue().toString();
                            String information = "Email: " + email + " | Mobile: " + mobileNo;
                            //USERTYPE
                            String profileImage = dataSnapshot.child("profile_photo_url").getValue().toString();

                            holder.userName.setText(user_name);
                            holder.userInfo.setText(information);
                            holder.userType.setVisibility(View.INVISIBLE);
                            if(dataSnapshot.child("user_status").hasChild("state"))
                            {
                                String state = dataSnapshot.child("user_status").child("state").getValue().toString();
                                String date = dataSnapshot.child("user_status").child("date").getValue().toString();
                                String time = dataSnapshot.child("user_status").child("time").getValue().toString();

                                switch (state) {
                                    case "online":
                                        holder.userInfo.setText(R.string.active_now);
                                        break;
                                    case "offline":
                                        String last_seen_time = getString(R.string.last_seen) + " " + date + " " + time;
                                        holder.userInfo.setText(last_seen_time);
                                        break;
                                    case "logged_out":
                                        holder.userInfo.setText(R.string.offline_user);
                                        break;
                                }
                            }
                            else
                            {
                                holder.userInfo.setText(R.string.offline_user);
                            }

                            profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + profileImage);
                            downloadImage(profile_imagesRef, holder.profile_image);

                            holder.itemView.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    Intent intentToPrivateChat = new Intent(getContext(), PairChatActivity.class);
                                    intentToPrivateChat.putExtra("chat_user_id", list_of_userID);
                                    intentToPrivateChat.putExtra("chat_user_name", user_name);
                                    startActivity(intentToPrivateChat);
                                }
                            });

                            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
                            {
                                @Override
                                public boolean onLongClick(View view)
                                {
                                    CharSequence [] options = new CharSequence[] { "Delete this Contact", "Cancel"};

                                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                    builder.setTitle("Delete User?");
                                    builder.setItems(options, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int position)
                                        {
                                            if(position == 0)
                                            {
                                                Log.d("DEBUG:DELETE", currentUserID + " | " + list_of_userID);
                                                deleteContact(list_of_userID, contactRef);
                                                Toast.makeText(getContext(), "Contact has been deleted.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    builder.show();
                                    return true;
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Log.d("DELETE", databaseError.toString());
                    }
                });

            }
            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                return new ChatViewHolder(view);
            }
        };

        chatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profile_image;
        TextView userName, userInfo, userType;

        ChatViewHolder(@NonNull View itemView)
        {
            super(itemView);
            profile_image = itemView.findViewById(R.id.user_profile_image);
            userName= itemView.findViewById(R.id.username_text);
            userInfo = itemView.findViewById(R.id.user_status_text);
            userType = itemView.findViewById(R.id.user_type_text);
        }
    }
}
