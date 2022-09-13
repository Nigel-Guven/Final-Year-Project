package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.Objects;

import static classes.Device.flipImage;

public class ProfilePhotoActivity extends AppCompatActivity {

    ImageView profile_image;
    Button btnCamera, btnNext;
    int check_flag = 0;
    Bitmap mbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);

        btnCamera = findViewById(R.id.button3);
        profile_image = findViewById(R.id.imageView2);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, 0);
            }
        });



        btnNext = findViewById(R.id.button8);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(check_flag==1)
                {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null)
                    {

                        int value = extras.getInt("DRIVER_KEY");
                        String name_val = extras.getString("USER_NAME");
                        String surname_val = extras.getString("USER_SURNAME");
                        String gender_val = extras.getString("USER_GENDER");
                        String date_val = extras.getString("USER_DOB");
                        String address_val = extras.getString("USER_ADDRESS");
                        String mobile_val = extras.getString("USER_MOBILE");

                        if(value == 1)
                        {
                            Intent intentToDriverLicense = new Intent(ProfilePhotoActivity.this, DriverLicenseActivity.class);
                            intentToDriverLicense.putExtra("USER_NAME", name_val);
                            intentToDriverLicense.putExtra("USER_SURNAME", surname_val);
                            intentToDriverLicense.putExtra("USER_GENDER", gender_val);
                            intentToDriverLicense.putExtra("USER_DOB", date_val);
                            intentToDriverLicense.putExtra("USER_ADDRESS", address_val);
                            intentToDriverLicense.putExtra("USER_MOBILE", mobile_val);
                            intentToDriverLicense.putExtra("USER_PICTURE", mbitmap);
                            intentToDriverLicense.putExtra("DRIVER_KEY", value);
                            startActivity(intentToDriverLicense);
                        }
                        else
                        {
                            Intent intentToEmailPass = new Intent(ProfilePhotoActivity.this, SignUpActivity.class);
                            intentToEmailPass.putExtra("USER_NAME", name_val);
                            intentToEmailPass.putExtra("USER_SURNAME", surname_val);
                            intentToEmailPass.putExtra("USER_GENDER", gender_val);
                            intentToEmailPass.putExtra("USER_DOB", date_val);
                            intentToEmailPass.putExtra("USER_ADDRESS", address_val);
                            intentToEmailPass.putExtra("USER_MOBILE", mobile_val);
                            intentToEmailPass.putExtra("USER_PICTURE", mbitmap);
                            intentToEmailPass.putExtra("DRIVER_KEY", value);
                            startActivity(intentToEmailPass);
                        }
                    }
                }

                else
                {
                    Toast.makeText(ProfilePhotoActivity.this, "Please set a Profile picture before proceeding.",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mbitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        mbitmap = flipImage(mbitmap);

        profile_image.setImageBitmap(mbitmap);
        profile_image.setVisibility(View.VISIBLE);
        String option_try = "Take another?";
        check_flag = 1;
        btnCamera.setText(option_try);
    }
}
