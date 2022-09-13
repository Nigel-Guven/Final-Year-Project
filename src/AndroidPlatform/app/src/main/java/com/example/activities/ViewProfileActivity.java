package com.example.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import classes.Vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

import static classes.Functions.downloadImage;
import static classes.Functions.getAddress;
import static classes.User.updateUserStatus;

public class ViewProfileActivity extends AppCompatActivity
{
    private ImageView profile_image;
    private TextView tViewName, tViewAddress, tViewEmail, tViewMobile, tViewVehicle;
    private Button buttonToMessage;
    private final String TAG = "OTHER USER ACTIVITY";
    private StorageReference profile_imagesRef;
    private DatabaseReference chatRequestRef, notificationRef;
    private String userID, user_name, user_surname, user_address, user_email, user_mobile, activeUserID, currentState;
    private String route_info_item, route_src, route_dst;
    Boolean isDriver, currentIsDriver;
    private Boolean flag = false;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        profile_image = findViewById(R.id.imageViewUser);
        tViewName = findViewById(R.id.textViewName);
        tViewAddress = findViewById(R.id.textViewAddr);
        tViewEmail = findViewById(R.id.textViewMail);
        tViewMobile = findViewById(R.id.textViewMob);
        tViewVehicle = findViewById(R.id.textViewVehicle);
        buttonToMessage = findViewById(R.id.imageButton);
        currentState = "NEW";

        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child("Requests");
        final DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        Intent fromPostDetails = getIntent();
        userID = Objects.requireNonNull(fromPostDetails.getExtras()).getString("USERID");
        route_info_item = fromPostDetails.getExtras().getString("USERINFO");
        route_src = fromPostDetails.getExtras().getString("USERSRC");
        route_dst = fromPostDetails.getExtras().getString("USERDST");
        profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + userID + ".jpg");
        activeUserID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();

        chatRequestRef.child(activeUserID).child(userID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    buttonToMessage.setText(R.string.cancel_request_text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d("TAG", databaseError.toString());
            }
        });


        mStore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                user_name = documentSnapshot.getString("first_name");
                user_surname = documentSnapshot.getString("surname");
                user_address = documentSnapshot.getString("address");
                user_email = documentSnapshot.getString("email");
                user_mobile = documentSnapshot.getString("mobile_no");
                isDriver = documentSnapshot.getBoolean("driver");

                String USERNAME = user_name + " " + user_surname;
                tViewName.setText(USERNAME);
                tViewAddress.setText(getAddress((user_address)));
                tViewEmail.setText(user_email);
                tViewMobile.setText(user_mobile);
                downloadImage(profile_imagesRef, profile_image);
                Log.d(TAG, "CONTENTS:" + user_name + user_surname + user_mobile + user_email + user_address);

                assert isDriver != null;
                if(isDriver)
                {
                    Vehicle user_vehicle = documentSnapshot.get("vehicle",Vehicle.class);
                    assert user_vehicle != null;
                    tViewVehicle.setText(user_vehicle.getCar_type());
                }

                contactRef.child(userID).child(activeUserID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            buttonToMessage.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            mStore.collection("users").document(activeUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                            {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot)
                                {
                                    currentIsDriver = Objects.requireNonNull(documentSnapshot.getBoolean("driver"));
                                    if(!currentIsDriver&&!isDriver)
                                    {
                                        buttonToMessage.setVisibility(View.INVISIBLE);
                                        tViewVehicle.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        manageChatRequests();
                                    }
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(ViewProfileActivity.this, LoginActivity.class);
            startActivity(returnToLogin);
        }
        else {
            updateUserStatus("online");
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null && !flag)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null)
        {
            Log.d("ONLOGDESTROY", "THIS");
            updateUserStatus("offline");
        }
    }

    @Override
    public void onBackPressed()
    {
        flag = true;
        Intent intent_to_dash = new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(intent_to_dash);
    }

    public void manageChatRequests()
    {
        chatRequestRef.child(activeUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(userID))
                {
                    String request_type = Objects.requireNonNull(dataSnapshot.child(userID).child("request_type").getValue()).toString();
                    if (request_type.equals("sent"))
                    {
                        currentState = "REQUESTSENT";
                    }
                    else if(request_type.equals("received"))
                    {
                        currentState = "request_received";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d(TAG, databaseError.toString());
            }
        });

        if(!activeUserID.equals(userID))
        {
            buttonToMessage.setVisibility(View.VISIBLE);
            buttonToMessage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    buttonToMessage.setEnabled(false);
                    if(currentState.equals("NEW"))
                    {
                        sendContactRequest();
                    }
                    else if(currentState.equals("REQUESTSENT"))
                    {
                        cancelChatRequest();
                    }
                }
            });
        }
        else
        {
            buttonToMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void cancelChatRequest()
    {
        chatRequestRef.child(activeUserID).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    chatRequestRef.child(userID).child(activeUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                buttonToMessage.setEnabled(true);
                                currentState = "NEW";
                                buttonToMessage.setText(R.string.message_text);
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendContactRequest()
    {
        chatRequestRef.child(activeUserID).child(userID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    String formatted = formatRouteInfo(route_info_item, isDriver, currentIsDriver);
                    chatRequestRef.child(activeUserID).child(userID).child("user_src").setValue(route_src);
                    chatRequestRef.child(activeUserID).child(userID).child("user_dst").setValue(route_dst);
                    chatRequestRef.child(activeUserID).child(userID).child("user_route_format").setValue(formatted);
                    chatRequestRef.child(activeUserID).child(userID).child("user_route_info").setValue(route_info_item);
                    chatRequestRef.child(userID).child(activeUserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                chatNotificationMap.put("author", activeUserID);
                                chatNotificationMap.put("message_type", "request");

                                notificationRef.child(userID).push().setValue(chatNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            buttonToMessage.setEnabled(true);
                                            currentState = "REQUESTSENT";
                                            buttonToMessage.setText(R.string.cancel_request_text);
                                        }
                                        else
                                            Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

    private String formatRouteInfo(String route_information, Boolean profileUserDriver, Boolean currentUserDriver)
    {
        Log.d("TAGROUTE", route_information + " " + profileUserDriver + " " + currentUserDriver);

        if(currentUserDriver && !profileUserDriver)
        {
            return "This person has offered to pick you up.";
        }
        if(!currentUserDriver && profileUserDriver)
        {
            return route_information.replaceAll(" you ", " this person ");
        }
        if(currentUserDriver)
        {
            return route_information.replaceAll(" you ", " this person ");
        }
        else
            return "This person is also a passenger";
    }
}
