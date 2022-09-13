package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Contacts Fragment holds list of  attached friends. Green icon displays if contact person is online
 *
 */
public class ContactsFragment extends Fragment
{
    private RecyclerView myContactList;
    private DatabaseReference contactsRef, userRef, databaseRef;
    private StorageReference profile_imagesRef;
    private String currentUserID;
    public ContactsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);
        myContactList = contactsView.findViewById(R.id.recyclerviewcontacts);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return contactsView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contact>().setQuery(contactsRef, Contact.class).build();

        @SuppressWarnings("unchecked")
        FirebaseRecyclerAdapter<Contact, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ContactsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contact model)
            {
                final String userIDSet = getRef(position).getKey();
                assert userIDSet != null;
                userRef.child(userIDSet).addValueEventListener(new ValueEventListener()
                {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            if(dataSnapshot.hasChild("profile_photo_url"))
                            {
                                //USERNAME
                                String user_name = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("surname").getValue().toString();
                                //USERINFO
                                String mobileNo = dataSnapshot.child("mobile_no").getValue().toString();
                                String email = dataSnapshot.child("email").getValue().toString();
                                String information = "Email: " + email + "\nMobile: " + mobileNo;
                                //USERTYPE
                                String driveFlag = dataSnapshot.child("driver").getValue().toString();
                                String profileImage = dataSnapshot.child("profile_photo_url").getValue().toString();

                                holder.userName.setText(user_name);
                                holder.userInfo.setText(information);

                                if(driveFlag.equals("true"))
                                {
                                    holder.userType.setText(R.string.user_is_driver);
                                }
                                else if(driveFlag.equals("false"))
                                {
                                    holder.userType.setText((R.string.user_is_passenger));
                                }

                                if(dataSnapshot.child("user_status").hasChild("state"))
                                {
                                    String state = dataSnapshot.child("user_status").child("state").getValue().toString();

                                    switch (state)
                                    {
                                        case "online":
                                            holder.online_icon.setVisibility(View.VISIBLE);
                                            break;
                                        case "offline":
                                        case "logged_out":
                                            holder.online_icon.setVisibility(View.GONE);
                                            break;
                                    }
                                }
                                else
                                {
                                    holder.online_icon.setVisibility(View.GONE);
                                }
                                profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + profileImage);
                                downloadImage(profile_imagesRef, holder.userImage);

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
                                                    Log.d("DEBUG:DELETE", currentUserID + " | " + userIDSet);
                                                    deleteContact(userIDSet, databaseRef);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Log.d("TAG", databaseError.toString());
                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                return new ContactsViewHolder(view);
            }
        };

        myContactList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userInfo, userType;
        CircleImageView userImage;
        ImageView online_icon;

        ContactsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName = itemView.findViewById(R.id.username_text);
            userInfo = itemView.findViewById(R.id.user_status_text);
            userImage = itemView.findViewById(R.id.user_profile_image);
            userType = itemView.findViewById(R.id.user_type_text);
            online_icon = itemView.findViewById(R.id.online_status);
        }
    }
}
