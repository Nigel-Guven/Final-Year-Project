package com.example.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import static classes.User.updateUserStatus;

public class GroupActivity extends AppCompatActivity
{
    private final String TAG = "GROUPCHAT";
    private Boolean flag = false;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private LinearLayout linearLayout;
    private String currentUserID, currentUsername;
    private int count = 0;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference groupNameRef, groupRef;
    private HashSet<String> hashSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        ImageButton sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        mScrollView = findViewById(R.id.scrollViewGroup);
        linearLayout = findViewById(R.id.linear_layout_group_messages);
        TextView textViewGroupName = findViewById(R.id.textView83);
        String currentGroupName = Objects.requireNonNull(getIntent().getExtras()).getString("GROUPNAME");
        textViewGroupName.setText(currentGroupName);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        assert currentGroupName != null;
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("messages");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        getUserInformation();

        sendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveMessageInfoToRTDB();
                userMessageInput.setText("");
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void saveMessageInfoToRTDB()
    {
        String message = userMessageInput.getText().toString();
        String messageKey = groupNameRef.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(GroupActivity.this, "The text field is empty!", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MM, yyyy");
            String currentDate = currentDateFormat.format(calendar.getTime());

            Calendar calendarTimeStamp = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm");
            String currentTime = currentTimeFormat.format(calendarTimeStamp.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            assert messageKey != null;
            DatabaseReference groupMessageKeyRef = groupNameRef.child(messageKey);
            HashMap<String,Object> messageInformationMap = new HashMap<>();
            messageInformationMap.put("name", currentUsername);
            messageInformationMap.put("message", message);
            messageInformationMap.put("date", currentDate);
            messageInformationMap.put("time", currentTime);

            groupMessageKeyRef.updateChildren(messageInformationMap);
        }
    }

    private void getUserInformation()
    {
        firebaseFirestore.collection("users").document(currentUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if(documentSnapshot.exists())
                {
                    currentUsername = documentSnapshot.getString("first_name") + " " + documentSnapshot.getString("surname");
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG,e.toString());
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(GroupActivity.this, LoginActivity.class);
            startActivity(returnToLogin);
        }
        else {
            updateUserStatus("online");
        }

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    printMessages(dataSnapshot);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    printMessages(dataSnapshot);
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && !flag)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    public void onBackPressed()
    {
        flag=true;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(GroupActivity.this, LoginActivity.class);
            startActivity(returnToLogin);
        }
        else {
            updateUserStatus("online");
        }
        Intent intent_to_forum = new Intent(getApplicationContext(),ForumActivity.class);
        startActivity(intent_to_forum);
    }

    private void printMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext())
        {

            String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot) iterator.next()).getValue();
            addLinearLayout(chatName + " :", chatMessage, chatDate + " " + chatTime);
            count++;
            hashSet.add(chatName);
            groupRef.child("post_count").setValue(count);
            groupRef.child("user_count").setValue(hashSet.size());
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
    @SuppressWarnings("deprecation")
    private void addLinearLayout(String tmp1, String tmp2, String tmp3)
    {
        LinearLayout ll = new LinearLayout(this);
        TextView displayName = new TextView(this);
        TextView displayMessages = new TextView(this);
        TextView displayDateTime = new TextView(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_params.setMargins(24, 16, 24, 8);
        ll.setLayoutParams(layout_params);
        ll.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.group_message_background, null));
        ll.setPadding(8, 8,8, 8);
        displayName.setText(tmp1);

        displayMessages.setText(tmp2);
        displayDateTime.setText(tmp3);

        displayName.setTypeface(null, Typeface.BOLD);
        displayMessages.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        displayMessages.setTextColor(getResources().getColor(R.color.black));
        displayDateTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        displayDateTime.setTypeface(null, Typeface.ITALIC);
        displayName.setPadding(16, 10, 24, 12);
        displayMessages.setPadding(16, 10, 24, 12);
        displayDateTime.setPadding(16, 10, 24, 12);

        ll.addView(displayName);
        ll.addView(displayMessages);
        ll.addView(displayDateTime);
        linearLayout.addView(ll);
    }
}
