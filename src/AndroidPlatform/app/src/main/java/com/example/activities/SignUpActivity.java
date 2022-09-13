package com.example.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import classes.Driver;
import classes.User;
import classes.Vehicle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;

import static classes.Functions.isValidEmail;

public class SignUpActivity extends AppCompatActivity {

    EditText mEmail, mPassword, mPasswordConfirm;
    Button mRegisterBtn;
    ProgressBar mProgressBar, mProgressCircle;

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore mFirestore;
    FirebaseStorage mFirestorage;
    StorageReference profile_imagesRef, mreference;
    DatabaseReference rootRef;

    String profile_image_url;
    String name_val, surname_val, mobile_val;
    private static final String TAG = "SIGNUPACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmail = findViewById(R.id.editText15);
        mPassword = findViewById(R.id.editText5);
        mPasswordConfirm = findViewById(R.id.editText6);
        mRegisterBtn = findViewById(R.id.button9);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestorage = FirebaseStorage.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        mProgressBar = findViewById(R.id.progressBar6);
        mProgressCircle = findViewById(R.id.progressBar5);

        if(mFirebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));

        }

        mPasswordConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setProgress(100);
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email is Required.");
                    return;
                }
                if(!isValidEmail(email))
                {
                    mEmail.setError("Email is invalid.");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password is Required.");
                    return;
                }
                if (password.length() < 8)
                {
                    mPassword.setError("Password must contain more than 6 characters.");
                    return;
                }
                else
                {
                    char [] arrayPass = password.toCharArray();
                    boolean bad_password_num = true;
                    boolean bad_password_cap = true;

                    for(Character i : arrayPass)
                    {
                        if(Character.isUpperCase(i))
                        {
                            bad_password_cap = false;
                        }
                        else if(Character.isDigit(i))
                        {
                            bad_password_num = false;
                        }
                    }
                    if(bad_password_cap || bad_password_num)
                    {
                        mPassword.setError("Password must contain at least one number and one capital letter.");
                        return;
                    }
                }


                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressCircle.setVisibility(View.VISIBLE);

                mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        final String userID = mFirebaseAuth.getCurrentUser().getUid();
                        if(task.isSuccessful())
                        {
                            @SuppressWarnings("deprecation")
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            rootRef.child("Users").child(userID).child("device_token").setValue(deviceToken);

                            Toast.makeText(SignUpActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                            Bundle extras = getIntent().getExtras();
                            if (extras != null)
                            {
                                int value = extras.getInt("DRIVER_KEY");
                                name_val = extras.getString("USER_NAME");
                                surname_val = extras.getString("USER_SURNAME");
                                String gender_val = extras.getString("USER_GENDER");
                                String date_val = extras.getString("USER_DOB");
                                String address_val = extras.getString("USER_ADDRESS");
                                mobile_val = extras.getString("USER_MOBILE");
                                Bitmap profile_val = extras.getParcelable("USER_PICTURE");
                                uploadImage(profile_val,userID);
                                Log.d(TAG,"+++ DEBUG +++ "+ value +"-" +
                                        name_val +" "+
                                        surname_val + "-" +
                                        gender_val +"-"+
                                        date_val +"//"+
                                        address_val+ "//" +
                                        mobile_val + "//" + profile_val);

                                if(value == 1)
                                {
                                    final Vehicle car_val = (Vehicle) extras.getSerializable("DRIVER_CAR");

                                    Log.d(TAG,"+++ DEBUG +++ " +
                                            Objects.requireNonNull(car_val).getCar_type() + "//"+
                                            car_val.getCar_registration() +"//"+"//"+
                                            car_val.getCar_color() +"//"+"//"+
                                            car_val.getCar_year() +"//"+"//"+
                                            car_val.getCar_fuel_type() +"//"+"//"+
                                            car_val.getCar_engine_cc() +"//"+"//"+
                                            car_val.getCar_body() +"//");

                                    DocumentReference mdocReference = mFirestore.collection("users").document(userID);
                                    Driver thisDriver = new Driver(address_val, date_val, true, email, name_val, gender_val, mobile_val, userID +".jpg", surname_val, car_val);
                                    HashMap<String,String> profileMap = new HashMap<>();
                                    profileMap.put("uid", userID);
                                    profileMap.put("driver", "true");
                                    profileMap.put("first_name", name_val);
                                    profileMap.put("surname", surname_val);
                                    profileMap.put("mobile_no", mobile_val);
                                    profileMap.put("email", email);
                                    profileMap.put("profile_photo_url",userID + ".jpg");
                                    profileMap.put("device_token",deviceToken);
                                    rootRef.child("Users").child(userID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                HashMap<String,String> vehicleMap = new HashMap<>();
                                                vehicleMap.put("car_name",car_val.getCar_type());
                                                vehicleMap.put("car_registration",car_val.getCar_registration());
                                                vehicleMap.put("car_color",car_val.getCar_color());
                                                vehicleMap.put("car_year",car_val.getCar_year());
                                                vehicleMap.put("car_fuel_type",car_val.getCar_fuel_type());
                                                vehicleMap.put("car_engine_cc",car_val.getCar_engine_cc());
                                                vehicleMap.put("car_body",car_val.getCar_body());
                                                rootRef.child("Users").child(userID).child("Vehicle").setValue(vehicleMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {
                                                            Log.d(TAG, task.toString());
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener()
                                                {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                    {
                                                        Log.d(TAG, e.toString());
                                                    }
                                                });
                                                Log.d(TAG, "Driver information uploaded to RTDB.");
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Failed to upload information: " + Objects.requireNonNull(task.getException()).toString());
                                            }
                                        }
                                    });
                                    mdocReference.set(thisDriver, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>()
                                    {
                                        @Override
                                        public void onSuccess(Void aVoid)
                                        {
                                            Log.d("TAG", "USER PROFILE IS SUCCESSFUL");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Log.d("TAG", "USER PROFILE FAILED" + e.toString());
                                        }
                                    });

                                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                }
                                else
                                {

                                    DocumentReference mdocReference = mFirestore.collection("users").document(userID);
                                    User thisUser = new User(address_val, date_val, false, email, name_val, gender_val, mobile_val, userID +".jpg", surname_val);


                                    HashMap<String,String> profileMap = new HashMap<>();
                                    profileMap.put("uid",userID);
                                    profileMap.put("driver","false");
                                    profileMap.put("first_name",name_val);
                                    profileMap.put("surname",surname_val);
                                    profileMap.put("mobile_no",mobile_val);
                                    profileMap.put("email",email);
                                    profileMap.put("profile_photo_url",userID + ".jpg");
                                    profileMap.put("device_token",deviceToken);
                                    rootRef.child("Users").child(userID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Log.d(TAG, "User information uploaded to RTDB.");
                                            }
                                            else
                                            {
                                                Log.d(TAG, "Failed to upload information: " + Objects.requireNonNull(task.getException()).toString());
                                            }
                                        }
                                    });
                                    mdocReference.set(thisUser, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid)
                                        {
                                            Log.d("TAG", "USER PROFILE IS SUCCESSFUL" + userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Log.d("TAG", "USER PROFILE FAILED" + e.toString());
                                        }
                                    });

                                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this,"Problem occurred during registration. Please check your credentials.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    private void uploadImage(Bitmap tmpmap, String uID)
    {
        if(tmpmap!=null)
        {
            mreference = profile_imagesRef.child(uID + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            tmpmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            profile_image_url = mreference.getDownloadUrl().toString();
            Log.d(TAG , profile_image_url);
            UploadTask uploadTask = mreference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception exception)
                {
                    Log.d(TAG,"Failed to upload picture to Firebase");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Log.d(TAG,"Uploaded picture to Firebase Success.");
                }
            });
        }
    }
}
