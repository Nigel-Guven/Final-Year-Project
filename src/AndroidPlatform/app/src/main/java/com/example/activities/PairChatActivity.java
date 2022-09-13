package com.example.activities;

import adapters.MessageAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import classes.ApplicationChannels;
import classes.Message;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static classes.Functions.downloadImage;

@SuppressWarnings("deprecation")
public class PairChatActivity extends AppCompatActivity
{
    private String messageReceiverID, saveCurrentTime, saveCurrentDate, messageSenderID;
    private String checkState = null, fileUrl = "";
    private StorageTask uploadTask;
    private final int DIR_REQUEST_CODE = 428;
    private ImageButton sendMessageButton, sendMediaButton;
    private EditText messageInputText;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    StorageReference profile_imagesRef;
    FirebaseAuth mFirebaseAuth;
    private DatabaseReference rootRef, notificationRef, databaseReference;
    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView userMessageList;
    private android.app.ProgressDialog loadingBar;
    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_chat);

        mFirebaseAuth = FirebaseAuth.getInstance();

        messageSenderID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child("Messages");
        userLastSeen = findViewById(R.id.textViewLastSeen);
        messageReceiverID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("chat_user_id")).toString();
        String messageReceiverName = Objects.requireNonNull(getIntent().getExtras().get("chat_user_name")).toString();
        Log.d("TAG", ApplicationChannels.CHANNEL_1_ID);
        initializeState();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userName.setText(messageReceiverName);
        profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + messageReceiverID + ".jpg");
        downloadImage(profile_imagesRef, userImage);

        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
                userMessageList.smoothScrollToPosition(Objects.requireNonNull(userMessageList.getAdapter()).getItemCount());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(PairChatActivity.this, "Failed to send message: " + databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage();
            }
        });

        sendMediaButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CharSequence [] options = new CharSequence[]{ "Images", "Documents" };
                AlertDialog.Builder builder = new AlertDialog.Builder(PairChatActivity.this);
                builder.setTitle("Select a File");

                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if(i == 0)
                        {
                            checkState = "image";

                            Intent intentToLocalDirectory = new Intent();
                            flag=true;
                            intentToLocalDirectory.setAction(Intent.ACTION_GET_CONTENT);
                            intentToLocalDirectory.setType("image/*");
                            startActivityForResult(Intent.createChooser(intentToLocalDirectory, "Select an Image"), DIR_REQUEST_CODE);
                        }
                        if(i == 1)
                        {
                            checkState = "document";
                            Intent intentToLocalDirectory = new Intent();
                            flag=true;
                            intentToLocalDirectory.setAction(Intent.ACTION_GET_CONTENT);
                            intentToLocalDirectory.setType("application/pdf");
                            startActivityForResult(Intent.createChooser(intentToLocalDirectory, "Select a PDF Document"), DIR_REQUEST_CODE);
                        }
                    }
                });
                builder.show();
            }
        });
        displayLastActive();
    }
    @SuppressWarnings("ConstantConditions")
    private void displayLastActive()
    {
        rootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.d("TAG", dataSnapshot.toString());
                if(dataSnapshot.child("user_status").hasChild("state"))
                {
                    String state = dataSnapshot.child("user_status").child("state").getValue().toString();
                    String date = dataSnapshot.child("user_status").child("date").getValue().toString();
                    String time = dataSnapshot.child("user_status").child("time").getValue().toString();

                    switch (state)
                    {
                        case "online":
                            userLastSeen.setText(R.string.active_now);
                            break;
                        case "offline":
                            String last_seen_time = getString(R.string.last_seen) + date + " " + time;
                            userLastSeen.setText(last_seen_time);
                            break;
                        case "logged_out":
                            userLastSeen.setText(R.string.offline_user);
                            break;
                    }
                }
                else
                {
                    userLastSeen.setText(R.string.offline_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    @SuppressWarnings({"deprecation", "unchecked", "ConstantConditions"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DIR_REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            loadingBar.setTitle("Uploading Media");
            loadingBar.setMessage("Please wait while your file is being transferred...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            Uri fileUri = data.getData();


            if(!checkState.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat_documents");

                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();
                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + ".pdf");
                uploadTask = filePath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation()
                {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            fileUrl = downloadUri.toString();

                            Map<String, String> messageDocumentBody = new HashMap<>();
                            messageDocumentBody.put("message_contents", fileUrl);
                            messageDocumentBody.put("message_type", "document");
                            messageDocumentBody.put("author", messageSenderID);
                            messageDocumentBody.put("message_receiver", messageReceiverID);
                            messageDocumentBody.put("messageID", messagePushID);
                            messageDocumentBody.put("date", saveCurrentDate);
                            messageDocumentBody.put("time", saveCurrentTime);

                            Map<String, Object> messageBodyDetails = new HashMap<>();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageDocumentBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageDocumentBody);


                            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener()
                            {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        HashMap<String, String> messageNotificationMap = new HashMap<>();
                                        messageNotificationMap.put("author", messageSenderID);
                                        messageNotificationMap.put("message_type",fileUrl.trim());

                                        notificationRef.child(messageReceiverID).push().setValue(messageNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    Log.d("TAG","Notification sent successfully.");
                                                }
                                                else
                                                    Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                                            }
                                        });
                                        Log.d("TAG","Message sent successfully.");
                                    }
                                    messageInputText.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    loadingBar.dismiss();
                                    Toast.makeText(PairChatActivity.this, "Message failed to send", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        loadingBar.dismiss();
                        Log.d("TAG",e.toString(), uploadTask.getException());
                    }
                });

            }

            else if(checkState.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat_images");

                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();
                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + ".jpg");
                uploadTask = filePath.putFile(fileUri);

                //noinspection unchecked
                uploadTask.continueWithTask(new Continuation()
                {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw Objects.requireNonNull(task.getException());
                        }

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            fileUrl = downloadUri.toString();

                            Map<String, String> messageImageBody = new HashMap<>();
                            messageImageBody.put("message_contents", fileUrl);
                            messageImageBody.put("message_type", "image");
                            messageImageBody.put("author", messageSenderID);
                            messageImageBody.put("message_receiver", messageReceiverID);
                            messageImageBody.put("messageID", messagePushID);
                            messageImageBody.put("date", saveCurrentDate);
                            messageImageBody.put("time", saveCurrentTime);

                            Map<String, Object> messageBodyDetails = new HashMap<>();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageImageBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageImageBody);

                            //noinspection unchecked
                            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener()
                            {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        HashMap<String, String> messageNotificationMap = new HashMap<>();
                                        messageNotificationMap.put("author", messageSenderID);
                                        messageNotificationMap.put("message_type",fileUrl.trim());

                                        notificationRef.child(messageReceiverID).push().setValue(messageNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    Log.d("TAG","Notification sent successfully.");
                                                }
                                                else
                                                    Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                                            }
                                        });
                                        Log.d("TAG","Message sent successfully.");
                                    }
                                    messageInputText.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    loadingBar.dismiss();
                                    Toast.makeText(PairChatActivity.this, "Message failed to send", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        loadingBar.dismiss();
                        Log.d("TAG",e.toString(), uploadTask.getException());
                    }
                });
            }
            else
            {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeState()
    {
        userImage = findViewById(R.id.profile_image_chat);
        userName = findViewById(R.id.textView92);
        userLastSeen = findViewById(R.id.textViewLastSeen);
        sendMessageButton = findViewById(R.id.send_message_button2);
        sendMediaButton = findViewById(R.id.upload_media_button);
        messageInputText = findViewById(R.id.input_private_message);
        loadingBar = new android.app.ProgressDialog(PairChatActivity.this);
        messageAdapter = new MessageAdapter(messageList);
        userMessageList = findViewById(R.id.chat_message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(PairChatActivity.this, LoginActivity.class);
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
            updateUserStatus("offline");
        }
    }

    @Override
    public void onBackPressed()
    {
        flag=true;
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(PairChatActivity.this, LoginActivity.class);
            startActivity(returnToLogin);
        }
        Intent intent_to_forum = new Intent(getApplicationContext(),ForumActivity.class);
        startActivity(intent_to_forum);
    }

    @SuppressWarnings("unchecked")
    private void sendMessage()
    {
        final String messageText = messageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "Message field is empty.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();
            String messagePushID = userMessageKeyRef.getKey();

            Map<String, String> messageTextBody = new HashMap<>();
            messageTextBody.put("message_contents", messageText.trim());
            messageTextBody.put("message_type", "text");
            messageTextBody.put("author", messageSenderID);
            messageTextBody.put("message_receiver", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("time", saveCurrentTime);

            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            //noinspection unchecked
            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        HashMap<String, String> messageNotificationMap = new HashMap<>();
                        messageNotificationMap.put("author", messageSenderID);
                        messageNotificationMap.put("message_type", messageText.trim());

                        notificationRef.child(messageReceiverID).push().setValue(messageNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Log.d("TAG","Notification sent successfully.");
                                }
                                else
                                    Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                            }
                        });
                        Log.d("TAG","Message sent successfully.");
                    }
                    messageInputText.setText("");
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(PairChatActivity.this, "Message failed to send", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUserStatus(String state)
    {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentTime = new SimpleDateFormat("H:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();

        onlineStateMap .put("time", saveCurrentTime);
        onlineStateMap .put("date", saveCurrentDate);
        onlineStateMap .put("state", state);

        String currentUserID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();

        databaseReference.child("Users").child(currentUserID).child("user_status").updateChildren(onlineStateMap);
    }
}
