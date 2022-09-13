package com.example.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    TextView textForgotPassword;
    Button mLoginBtn, mRegister;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.editText);
        mPassword = findViewById(R.id.editText2);
        mLoginBtn = findViewById(R.id.button);
        mRegister = findViewById(R.id.button2);
        textForgotPassword = findViewById(R.id.textView82);
        textForgotPassword.setText(HtmlCompat.fromHtml("<u>Forgotten Password? Click Here.</u>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        if(mFirebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
        }

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText enterMail = new EditText(view.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Forgot Password?");
                passwordResetDialog.setMessage("Please enter the email associated with your account to receive a password reset link.");
                passwordResetDialog.setView(enterMail);
                passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener()
                {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = enterMail.getText().toString().trim();
                        if(TextUtils.isEmpty(mail))
                        {
                            enterMail.setText("Required Field is empty.");
                        }
                        else
                            {
                            mFirebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "A link has been sent to your email to reset your password.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "An error has occurred. Please check that you are connected to Wi-fi and the email is valid.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { dialogInterface.cancel(); }
                });
                passwordResetDialog.create().show();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {

                    mPassword.setError("Wrong Password. Please try again.");
                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String currentUserID = mFirebaseAuth.getCurrentUser().getUid();
                            @SuppressWarnings("deprecation")
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            usersRef.child(currentUserID).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(FirebaseAnalytics.Param.QUANTITY,"1");
                                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

                                        Toast.makeText(LoginActivity.this, "Logged In.",Toast.LENGTH_SHORT).show();
                                        Intent toDashActivity = new Intent(LoginActivity.this, DashboardActivity.class);
                                        startActivity(toDashActivity);
                                    }
                                    else
                                        Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,"Please check your credentials or your Internet Access.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(),RegistryChooseActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
