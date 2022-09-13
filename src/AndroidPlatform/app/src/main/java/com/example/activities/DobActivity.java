package com.example.activities;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DobActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView dateText, date_title_text;
    Button nextButton, mBirthdayButton;
    private static final String TAG = "DobActivity";
    String dateToCollect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dob);
        dateText = findViewById(R.id.textView5);
        date_title_text = findViewById(R.id.textView110);
        nextButton = findViewById(R.id.button7);
        mBirthdayButton = findViewById(R.id.show_dialog);

        mBirthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                String birth_date = dateText.getText().toString().trim();
                if(TextUtils.isEmpty(birth_date))
                {
                    Toast.makeText(DobActivity.this, "A Date is required.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, -18);
                    cal.add(Calendar.DATE, -1);
                    Date date_eighteen = cal.getTime();

                    Date date_birth = null;
                    try {
                        date_birth = new SimpleDateFormat("dd/MM/yyyy").parse(birth_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    assert date_birth != null;
                    if(date_birth.compareTo(date_eighteen)<0)
                    {
                        Log.d(TAG,"+++ DEBUG +++ " + birth_date);

                        Bundle extras = getIntent().getExtras();
                        if (extras != null) {
                            int value = extras.getInt("DRIVER_KEY");
                            String name_val = extras.getString("USER_NAME");
                            String surname_val =extras.getString("USER_SURNAME");
                            String gender_val =extras.getString("USER_GENDER");
                            Log.d(TAG,"+++ DEBUG +++ " + value +"-" + name_val + surname_val + "-" + gender_val);

                            Intent intentToPA = new Intent(DobActivity.this,PhoneAddressActivity.class);
                            intentToPA.putExtra("USER_NAME", name_val);
                            intentToPA.putExtra("USER_SURNAME", surname_val);
                            intentToPA.putExtra("USER_GENDER", gender_val);
                            intentToPA.putExtra("USER_DOB", dateToCollect);
                            intentToPA.putExtra("DRIVER_KEY", value);
                            startActivity(intentToPA);
                        }

                    }
                    else if(date_birth.compareTo(date_eighteen)>0)
                    {
                        Log.d(TAG,"+++ DEBUG +++ " + date_eighteen);
                        Toast.makeText(DobActivity.this, "You are not old enough to use this application. Reverting to login screen.",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                }
            }
        });

    }
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month=month+1;
        dateToCollect = dayOfMonth + "/" + month + "/" + year;
        date_title_text.setVisibility(View.VISIBLE);
        dateText.setText(dateToCollect);
        dateText.setVisibility(View.VISIBLE);
    }
}
