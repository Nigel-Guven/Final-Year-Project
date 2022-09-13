package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static classes.Functions.capitalize;
import static classes.Functions.format_home_address;

public class PhoneAddressActivity extends AppCompatActivity {

    Button nextButton;
    EditText house_input, town_city_input, county_input, mobile_input;
    private static final String TAG = "PhoneAddressActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_address);

        nextButton = findViewById(R.id.button8);
        house_input = findViewById(R.id.editText10);
        town_city_input = findViewById(R.id.editText11);
        county_input = findViewById(R.id.editText12);
        mobile_input = findViewById(R.id.editText13);

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String house_addr = house_input.getText().toString().trim();
                String town_addr = town_city_input.getText().toString().trim();
                String county_addr = county_input.getText().toString().trim();
                String mobile_number = mobile_input.getText().toString().trim();

                if(TextUtils.isEmpty(house_addr)){
                    house_input.setError("Street Address is required.");
                    return;
                }
                if(TextUtils.isEmpty(town_addr)){
                    town_city_input.setError("Town Address is required.");
                    return;
                }
                if(TextUtils.isEmpty(county_addr)){
                    county_input.setError("County name is required.");
                    return;
                }
                if(TextUtils.isEmpty(mobile_number)){
                    mobile_input.setError("Phone number is required.");
                    return;
                }
                if((mobile_number.length() < 9 || mobile_number.length() > 10))
                {
                    Log.d(TAG,"+++ DEBUG +++ THIS1" );
                    mobile_input.setError("Please enter a valid Irish or UK mobile phone number.");
                    return;
                }
                if((!mobile_number.substring(0,2).equals("08")) && (!mobile_number.substring(0,2).equals("07")))
                {
                    Log.d(TAG,"+++ DEBUG +++ THIS2" );
                    mobile_input.setError("Please enter a valid Irish or UK mobile phone number.");
                    return;
                }
                Bundle extras = getIntent().getExtras();
                if (extras != null)
                {
                    house_addr = format_home_address(house_addr);
                    town_addr = capitalize(town_addr);
                    county_addr = capitalize(county_addr);
                    String full_address = house_addr + ", " + town_addr + ", " + county_addr;
                    int value = extras.getInt("DRIVER_KEY");
                    String name_val = extras.getString("USER_NAME");
                    String surname_val =extras.getString("USER_SURNAME");
                    String gender_val =extras.getString("USER_GENDER");
                    String date_val = extras.getString("USER_DOB");
                    Log.d(TAG,"+++ DEBUG +++ " + value +"-" + name_val +" "+ surname_val + "-" + gender_val +"-"+date_val);
                    Intent intentToProfile = new Intent(PhoneAddressActivity.this,ProfilePhotoActivity.class);
                    intentToProfile.putExtra("USER_NAME", name_val);
                    intentToProfile.putExtra("USER_SURNAME", surname_val);
                    intentToProfile.putExtra("USER_GENDER", gender_val);
                    intentToProfile.putExtra("USER_DOB", date_val);
                    intentToProfile.putExtra("USER_ADDRESS", full_address);
                    intentToProfile.putExtra("USER_MOBILE", mobile_number);
                    intentToProfile.putExtra("DRIVER_KEY", value);
                    startActivity(intentToProfile);
                }
            }
        });
    }
}
