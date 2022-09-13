package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import static classes.Functions.capitalize;


public class RegisterNameActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    private static final String TAG = "NameActivity";

    EditText mFirstName, mSurname;
    String gender;
    Button mNextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_name);

        mFirstName = findViewById(R.id.editText7);
        mSurname = findViewById(R.id.editText8);
        mNextButton = findViewById(R.id.button5);

        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);
        spinner.setOnItemSelectedListener(this);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String first_name = mFirstName.getText().toString().trim();
                String surname = mSurname.getText().toString().trim();

                if(TextUtils.isEmpty(first_name))
                {
                    mFirstName.setError("Name is required");
                    return;
                }
                if(TextUtils.isEmpty(surname))
                {
                    mSurname.setError("Surname is required");
                    return;
                }

                Bundle extras = getIntent().getExtras();
                if (extras != null)
                {
                    first_name = capitalize(first_name);
                    surname = capitalize(surname);
                    int value = extras.getInt("DRIVER_KEY");
                    Log.d(TAG,"+++ DEBUG +++ " + value );
                    Intent intentToDob = new Intent(RegisterNameActivity.this,DobActivity.class);
                    intentToDob.putExtra("USER_NAME", first_name);
                    intentToDob.putExtra("USER_SURNAME", surname);
                    intentToDob.putExtra("USER_GENDER", gender);
                    intentToDob.putExtra("DRIVER_KEY", value);
                    startActivity(intentToDob);
                }
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
    {
        gender = adapterView.getItemAtPosition(position).toString();
        Log.d(TAG,"+++ DEBUG TEST +++" + gender);

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }
}
