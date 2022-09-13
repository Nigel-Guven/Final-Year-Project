package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
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

import com.example.activities.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import static classes.Functions.downloadImage;

/**
 *
 * Reference: https://www.youtube.com/channel/UCIHBIPape0dWHKANkivrcJw/about
 * Coding Cafe Whatsapp tutorials aided with Messaging System
 *
 * RequestFragment holds list of user contact requests. Current user can accept or decline a request based on the information received.
 *
 */
public class RequestsFragment extends Fragment
{
    private RecyclerView requestsRecyclerView;
    private DatabaseReference chatRequestRef, contactsRef, userRef;
    private String currentUserID;
    private StorageReference profile_imagesRef;
    private String route_source ="";
    private String route_dest = "";
    private String route_info = "";
    private String route_format = "";

    public RequestsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View requestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);
        requestsRecyclerView = requestsFragmentView.findViewById(R.id.requests_list);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestsRecyclerView.setHasFixedSize(true);
        requestsRecyclerView.setItemViewCacheSize(20);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return requestsFragmentView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>().setQuery(chatRequestRef.child(currentUserID), Contact.class).build();
        FirebaseRecyclerAdapter<Contact, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, RequestViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contact model)
            {

                final String list_user_id = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot requestDataSnapshot)
                    {
                        if(requestDataSnapshot.exists())
                        {
                            String type = Objects.requireNonNull(requestDataSnapshot.getValue()).toString();
                            Log.d("TAG", type);
                            if(type.equals("received"))
                            {
                                assert list_user_id != null;
                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener()
                                {
                                    @SuppressWarnings("ConstantConditions")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userDataSnapshot)
                                    {
                                        final String requestName = Objects.requireNonNull(userDataSnapshot.child("first_name").getValue()).toString() + " " + userDataSnapshot.child("surname").getValue().toString();
                                        final String mobileNo = "Mobile No: " + userDataSnapshot.child("mobile_no").getValue().toString();
                                        final String driveFlag = userDataSnapshot.child("driver").getValue().toString();
                                        final String profileImage = userDataSnapshot.child("profile_photo_url").getValue().toString();
                                        Log.d("TAG", requestName);
                                        chatRequestRef.child(list_user_id).child(currentUserID).addValueEventListener(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot chatDataSnapshot)
                                            {
                                                if (chatDataSnapshot.exists())
                                                {
                                                    route_source = chatDataSnapshot.child("user_src").getValue().toString();
                                                    route_dest = chatDataSnapshot.child("user_dst").getValue().toString();
                                                    route_info = chatDataSnapshot.child("user_route_info").getValue().toString();
                                                    route_format = chatDataSnapshot.child("user_route_format").getValue().toString();
                                                    Log.d("TAG", requestName + " " + mobileNo + " " + driveFlag + " " + profileImage);
                                                    holder.userName.setText(requestName);
                                                    holder.userInfo.setText(R.string.connect_request);
                                                    holder.userInfo.setTypeface(null, Typeface.ITALIC);
                                                    String user_type_text = "Origin: " + route_source + "\n\nDestination: " + route_dest + "\n\n" + route_format;
                                                    if (driveFlag.equals("true"))
                                                    {
                                                        holder.userType.setText(user_type_text);
                                                    }
                                                    else if (driveFlag.equals("false"))
                                                    {
                                                        holder.userType.setText(user_type_text);
                                                    }
                                                    profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + list_user_id + ".jpg");
                                                    downloadImage(profile_imagesRef, holder.userImage);

                                                    holder.itemView.setOnClickListener(new View.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(View view)
                                                        {
                                                            CharSequence[] items = new CharSequence[]{"Accept", "Decline"};
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                            builder.setTitle("Chat Request from: " + requestName);

                                                            builder.setItems(items, new DialogInterface.OnClickListener()
                                                            {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i)
                                                                {
                                                                    if (i == 0)
                                                                    {
                                                                        contactsRef.child(currentUserID).child(list_user_id).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>()
                                                                        {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    contactsRef.child(list_user_id).child(currentUserID).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                    {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                        {
                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                chatRequestRef.child(currentUserID).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                    {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            chatRequestRef.child(list_user_id).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                            {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                                {
                                                                                                                    if (task.isSuccessful())
                                                                                                                    {
                                                                                                                        Toast.makeText(getContext(), "New Contact Saved.", Toast.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                    if (i == 1) {
                                                                        chatRequestRef.child(currentUserID).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                        {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    chatRequestRef.child(list_user_id).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                    {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                        {
                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                Toast.makeText(getContext(), "Contact Request Declined.", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                            builder.show();
                                                        }
                                                    });
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                            {
                                                Log.d("TAG", databaseError.toString());
                                            }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {
                                        Log.d("TAG", databaseError.toString());
                                    }
                                });
                            }
                            else if (type.equals("sent"))
                            {
                                assert list_user_id != null;
                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener()
                                {
                                    @SuppressWarnings("ConstantConditions")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        final String requestName = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("surname").getValue().toString();
                                        final String mobileNo = "Mobile No: " + dataSnapshot.child("mobile_no").getValue().toString();
                                        final String driveFlag = dataSnapshot.child("driver").getValue().toString();
                                        final String profileImage = dataSnapshot.child("profile_photo_url").getValue().toString();

                                        Log.d("TAG", requestName + " " + mobileNo + " " + driveFlag + " " + profileImage);
                                        holder.userName.setText(requestName);
                                        String request_rec_text = "Request Pending";
                                        holder.userInfo.setText(request_rec_text);
                                        holder.userInfo.setTypeface(null, Typeface.ITALIC);
                                        if(driveFlag.equals("true"))
                                        {
                                            holder.userType.setText(R.string.user_is_driver);
                                        }
                                        else if(driveFlag.equals("false"))
                                        {
                                            holder.userType.setText((R.string.user_is_passenger));
                                        }
                                        profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + list_user_id + ".jpg");
                                        downloadImage(profile_imagesRef, holder.userImage);

                                        holder.itemView.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                CharSequence [] items = new CharSequence[] { "Cancel Request" };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Contact Request: " + requestName);

                                                builder.setItems(items, new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i)
                                                    {
                                                        if(i == 0)
                                                        {
                                                            chatRequestRef.child(currentUserID).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                            {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        chatRequestRef.child(list_user_id).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                        {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(getContext(), "Contact Request Cancelled.",Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {
                                        Log.d("TAG", databaseError.toString());
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
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                return new RequestViewHolder(view);
            }
        };
        requestsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userInfo, userType;
        CircleImageView userImage;

        RequestViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.username_text);
            userInfo = itemView.findViewById(R.id.user_status_text);
            userImage = itemView.findViewById(R.id.user_profile_image);
            userType = itemView.findViewById(R.id.user_type_text);
        }
    }
}
