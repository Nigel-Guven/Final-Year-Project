package com.example.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import classes.LevenshteinDistance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.widget.Toast.makeText;
import static classes.Functions.containsDigit;

public class DriverLicenseActivity extends AppCompatActivity
{

    Button nextButton, button_to_Camera;

    Bitmap mbitmap; // RECYCLABLE

    private static final String TAG = "DL_Activity";
    private static final int CAMERA_REQUEST_CODE = 1001;
    int isClicked = 0;

    String validLicence = "CEADUNAS TIOMANA DRIVING LICENCE";
    String invalidLicence = "CEAD FOGHLAMORA LEARNER PERMIT";

    ArrayList<String> listOfBlocks = new ArrayList<>(); // RECYCLABLE
    int confidenceValid, confidenceInvalid = 0;         // RECYCLABLE

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_license);

        button_to_Camera = findViewById(R.id.button11);
        nextButton = findViewById(R.id.button6);

        button_to_Camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                isClicked = 1;
                Intent intentToCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentToCamera, CAMERA_REQUEST_CODE);


            }
        });

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                for(int i = 0;i<listOfBlocks.size();i++)
                {
                    if(listOfBlocks.get(i)!=null)
                    {
                        confidenceValid += LevenshteinDistance.longestSubstr(listOfBlocks.get(i), validLicence);
                        confidenceInvalid += LevenshteinDistance.longestSubstr(listOfBlocks.get(i), invalidLicence);
                        Log.d(TAG, "DEBUG: " + confidenceValid + "- - -" + confidenceInvalid);
                    }
                }

                if(isClicked==1 && confidenceValid > confidenceInvalid)
                {
                    makeText(DriverLicenseActivity.this, "Licence has been successfully validated.",Toast.LENGTH_SHORT).show();
                    Bundle extras = getIntent().getExtras();
                    if (extras != null)
                    {
                        Intent intentToRegistration = new Intent(DriverLicenseActivity.this, DriverRegistrationActivity.class);
                        int value = extras.getInt("DRIVER_KEY");
                        String name_val = extras.getString("USER_NAME");
                        String surname_val = extras.getString("USER_SURNAME");
                        String gender_val = extras.getString("USER_GENDER");
                        String date_val = extras.getString("USER_DOB");
                        String address_val = extras.getString("USER_ADDRESS");
                        String mobile_val = extras.getString("USER_MOBILE");
                        Bitmap profile_val = extras.getParcelable("USER_PICTURE");

                        intentToRegistration.putExtra("USER_NAME", name_val);
                        intentToRegistration.putExtra("USER_SURNAME", surname_val);
                        intentToRegistration.putExtra("USER_GENDER", gender_val);
                        intentToRegistration.putExtra("USER_DOB", date_val);
                        intentToRegistration.putExtra("USER_ADDRESS", address_val);
                        intentToRegistration.putExtra("USER_MOBILE", mobile_val);
                        intentToRegistration.putExtra("USER_PICTURE", profile_val);
                        intentToRegistration.putExtra("DRIVER_KEY", value);
                        startActivity(intentToRegistration);
                    }
                }
                else if(isClicked==0)
                {
                    makeText(DriverLicenseActivity.this, "Please take a picture of your license before proceeding.",Toast.LENGTH_SHORT).show();
                }
                else {
                    makeText(DriverLicenseActivity.this, "Invalid License. Please try again or sign up as a passenger.", Toast.LENGTH_SHORT).show();
                    isClicked = 0;
                    String TRY = "Try Again?";
                    button_to_Camera.setText(TRY);

                    mbitmap.recycle();
                    listOfBlocks.clear();
                    confidenceValid = 0;
                    confidenceInvalid = 0;
                }
            }
        });
    }

    private void detectTextFromImage()
    {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(mbitmap);
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer();
        firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>()
        {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText)
            {
                readTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                makeText(DriverLicenseActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readTextFromImage(FirebaseVisionText firebaseVisionText)
    {
        String image_ocr = "";
        List<FirebaseVisionText.TextBlock> mblocklist = firebaseVisionText.getTextBlocks();
        if(mblocklist.size()==0)
        {
            makeText(DriverLicenseActivity.this, "A valid license has not been found in the image." , Toast.LENGTH_SHORT).show();
        }
        else
        {

            for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks())
            {
                image_ocr += block.getText();
                if(!containsDigit(image_ocr))
                {
                    listOfBlocks.add(image_ocr);
                    Log.d(TAG, "DEBUG: " + image_ocr);
                }
            }
        }
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                mbitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                detectTextFromImage();
            }
        }
    }
}
