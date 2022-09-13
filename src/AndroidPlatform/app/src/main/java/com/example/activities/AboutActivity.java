package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static classes.User.updateUserStatus;

public class AboutActivity extends AppCompatActivity
{
    private Boolean flag = false;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final TextView urlToRepo = findViewById(R.id.textView49);

        urlToRepo.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(AboutActivity.this, LoginActivity.class);
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
        flag=true;
        Intent intent_to_home = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent_to_home);
    }
}
