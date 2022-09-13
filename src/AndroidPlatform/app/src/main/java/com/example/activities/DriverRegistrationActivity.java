package com.example.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import classes.Vehicle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Looper;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings("ALL")
public class DriverRegistrationActivity extends AppCompatActivity
{

    Button nextBtn, retypeBtn;
    EditText mCarReg;
    Document doc;
    String url;
    private static final String TAG = "DriverRegActivity";
    Vehicle mvehicle = new Vehicle("","","","","","","");
    TextView mtext1,mtext2,mtext3,mtext4,mtext5,mtext6;
    Bundle extras;
    private Timer timer = new Timer();
    private final long DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        doc = null;
        url = "https://www.motorcheck.ie/free-car-check/?vrm=";
        nextBtn = findViewById(R.id.button10);
        retypeBtn = findViewById(R.id.button12);
        mCarReg = findViewById(R.id.editText3);

        mtext1 = findViewById(R.id.textView24);
        mtext2 = findViewById(R.id.textView33);
        mtext3 = findViewById(R.id.textView34);
        mtext4 = findViewById(R.id.textView35);
        mtext5 = findViewById(R.id.textView36);
        mtext6 = findViewById(R.id.textView37);

        mCarReg.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(timer != null) timer.cancel();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.length() >= 3)
                {

                    timer = new Timer();
                    timer.schedule(new TimerTask()
                    {
                        @SuppressWarnings("SingleStatementInBlock")
                        @Override
                        public void run()
                        {
                            try
                            {
                                final String mcar_registration = mCarReg.getText().toString().trim().replace("-", "");
                                if (TextUtils.isEmpty(mcar_registration)) {
                                    mCarReg.setError("Valid car registration is required.");
                                }
                                else if(mcar_registration.matches("^.*[^a-zA-Z0-9 ].*$"))
                                {
                                    mCarReg.setError("Valid car registration is required.");
                                }

                                doc = Jsoup.connect(url + mcar_registration).userAgent("Mozilla").get();
                                Log.d("+++ DEBUG +++ ",doc.location() + "//" + doc.nodeName());
                                String html_soup = doc.select("div[class=col-md-6 align-items-center]").toString();
                                html_soup = Jsoup.clean(html_soup, Whitelist.none());
                                Log.d(TAG, "+++ DEBUG +++ " + html_soup);

                                if(html_soup.equals(""))
                                {
                                    Looper.prepare();

                                    Toast.makeText(DriverRegistrationActivity.this, "Failed to retrieve car registration details.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    html_soup = html_soup.replaceAll(",","\n");
                                    html_soup = html_soup.replaceAll("Body:", "\n");
                                    html_soup = html_soup.replaceAll("Engine CC:", "\n");
                                    html_soup = html_soup.replaceAll("Fuel:", "\n");
                                    html_soup = html_soup.replaceAll("Colour:", "\n");
                                    Log.d(TAG, "+++ DEBUG +++ " + html_soup);
                                    String[] vehicleData = html_soup.split("\n", 7);
                                    String [] tmpArr = vehicleData[5].split("W",10);
                                    mvehicle = new Vehicle(vehicleData[0].trim(),mcar_registration.trim(),vehicleData[1].trim(),tmpArr[0].trim(),vehicleData[3].trim(),vehicleData[4].trim(),vehicleData[2].trim());
                                    Log.d(TAG, "+++ DEBUG CAR TYPE = " + vehicleData[0]);
                                    Log.d(TAG, "+++ DEBUG MCARREG = " + mcar_registration);
                                    Log.d(TAG, "+++ DEBUG CAR YEAR = " + vehicleData[1]);
                                    Log.d(TAG, "+++ DEBUG CAR COLOR = " + vehicleData[5]);
                                    Log.d(TAG, "+++ DEBUG CAR ENGINE = " + vehicleData[3]);
                                    Log.d(TAG, "+++ DEBUG CAR FUEL = " + vehicleData[4]);
                                    Log.d(TAG, "+++ DEBUG CAR BODY = " + vehicleData[2]);

                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            mCarReg.setVisibility(View.INVISIBLE);
                                            retypeBtn.setVisibility(View.VISIBLE);
                                            mtext1.append(mvehicle.getCar_type());
                                            mtext2.append(mvehicle.getCar_year());
                                            mtext3.append(mvehicle.getCar_color());
                                            mtext4.append(mvehicle.getCar_engine_cc());
                                            mtext5.append(mvehicle.getCar_fuel_type());
                                            mtext6.append(mvehicle.getCar_body());

                                        }
                                    });
                                }
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(DriverRegistrationActivity.this, "This requires an internet connection to process.", Toast.LENGTH_LONG).show();

                            }
                            catch (NullPointerException f)
                            {
                                f.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(DriverRegistrationActivity.this, "NullPointer: This app requires an active Internet connection. Please enable Wifi in Settings. ", Toast.LENGTH_LONG).show();
                            }

                        }

                    }, DELAY);
                }
            }
        });

        retypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mCarReg.setText("");
                String type = getString(R.string.car_type);
                String year = getString(R.string.car_year);
                String color = getString(R.string.car_color);
                String engcc = getString(R.string.car_engcc);
                String fuel = getString(R.string.car_fuel) ;
                String body = getString(R.string.car_body);
                mtext1.setText(type);
                mtext2.setText(year);
                mtext3.setText(color);
                mtext4.setText(engcc);
                mtext5.setText(fuel);
                mtext6.setText(body);
                mCarReg.setVisibility(View.VISIBLE);
                retypeBtn.setVisibility(View.INVISIBLE);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String mcar_registration = mCarReg.getText().toString().trim().replace("-", "").toUpperCase();
                if(TextUtils.isEmpty(mcar_registration))
                {
                    mCarReg.setError("A vehicle registration is required.");
                }
                if(mvehicle.getCar_type().equals(""))
                {
                    mCarReg.setError("A vehicle registration is required.");
                }
                else
                {
                    extras = getIntent().getExtras();
                    Intent intentToSignUp = new Intent(DriverRegistrationActivity.this, SignUpActivity.class);
                    int value = extras.getInt("DRIVER_KEY");
                    String name_val = extras.getString("USER_NAME");
                    String surname_val = extras.getString("USER_SURNAME");
                    String gender_val = extras.getString("USER_GENDER");
                    String date_val = extras.getString("USER_DOB");
                    String address_val = extras.getString("USER_ADDRESS");
                    String mobile_val = extras.getString("USER_MOBILE");
                    Bitmap profile_val = extras.getParcelable("USER_PICTURE");

                    intentToSignUp.putExtra("USER_NAME", name_val);
                    intentToSignUp.putExtra("USER_SURNAME", surname_val);
                    intentToSignUp.putExtra("USER_GENDER", gender_val);
                    intentToSignUp.putExtra("USER_DOB", date_val);
                    intentToSignUp.putExtra("USER_ADDRESS", address_val);
                    intentToSignUp.putExtra("USER_MOBILE", mobile_val);
                    intentToSignUp.putExtra("USER_PICTURE", profile_val);
                    intentToSignUp.putExtra("DRIVER_CAR", mvehicle);
                    intentToSignUp.putExtra("DRIVER_KEY", value);
                    startActivity(intentToSignUp);
                }
            }
        });
    }
}
