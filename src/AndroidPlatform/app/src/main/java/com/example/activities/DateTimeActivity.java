package com.example.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import classes.Route;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static classes.Functions.getAddress;
import static classes.User.updateUserStatus;

public class DateTimeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private final String TAG = "DATETIME:";
    private FirebaseFirestore mStore;
    FirebaseAuth mFirebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference notificationRef;
    private LatLng source_coordinates, destination_coordinates;
    private String userID, route_start, route_end, route_distance, route_duration, dateTmp, timeTmp, user_name, user_surname, user_email, user_mobile;
    private Boolean isDriver;
    private Date routeDate, currentDate = null;
    private TextView text_start_address, text_end_address, text_distance, text_duration, text_depart_date, text_depart_time;
    private Integer dateToScramble;
    private int routeFlag = 0;
    private int day, month, year, dayFinal, monthFinal, yearFinal;
    private Boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);

        Button buttonToDateTime = findViewById(R.id.button14);
        Button buttonToCreateRoute = findViewById(R.id.button15);
        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        text_start_address = findViewById(R.id.textView68);
        text_end_address = findViewById(R.id.textView69);
        text_distance = findViewById(R.id.textView73);
        text_duration = findViewById(R.id.textView72);
        text_depart_date = findViewById(R.id.textView70);
        text_depart_time = findViewById(R.id.textView71);
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child("Routes");

        source_coordinates = Objects.requireNonNull(getIntent().getExtras()).getParcelable("SRC");
        destination_coordinates = getIntent().getExtras().getParcelable("DST");
        Log.d(TAG, source_coordinates + " " + destination_coordinates);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();



        buttonToDateTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                downloadTmpRouteData();
                downloadUserData();
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                currentDate = c.getTime();

                Log.d(TAG, "current: " + currentDate);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DateTimeActivity.this, DateTimeActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        buttonToCreateRoute.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, route_start + "|" + route_duration + "|" + route_end);
                if(routeFlag==1 && route_start!=null)
                {
                    Log.d(TAG, route_start + "|" + route_duration + "|" + route_end);
                    uploadRoute();
                    Log.d(TAG, "Route Check Flag = 1...Add route to Firestore");
                    Intent intentToDash = new Intent(DateTimeActivity.this, DashboardActivity.class);
                    flag=true;
                    startActivity(intentToDash);
                }
                else
                {
                    Toast.makeText(DateTimeActivity.this, "Please enter a valid Date for your journey.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day)
    {
        dayFinal = day;
        monthFinal = month + 1;
        yearFinal = year;

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(DateTimeActivity.this, DateTimeActivity.this, hour, minute, true);
        timePickerDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1)
    {
        String dateTimeInstance = dayFinal + "-" +monthFinal + "-" + yearFinal;
        Log.d(TAG, dayFinal + " " + monthFinal + " " + yearFinal + "||" + i + ":" + i1);

        try
        {
            routeDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateTimeInstance);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        assert routeDate != null;
        if(routeDate.compareTo(currentDate) < 0)
        {
            Log.d(TAG,"+++ DEBUG INVALID +++ " + routeDate + " || " + currentDate);
            Toast.makeText(DateTimeActivity.this, "An invalid date has been selected. Please try again.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.d(TAG,"+++ DEBUG +++ " + routeDate + " || " + currentDate);
            String formattedMin;
            String formattedHour;
            String formattedDay;

            if(i1 <10)
            {
                formattedMin = "0" + i1;
            }
            else
            {
                formattedMin = "" + i1;
            }
            if(i <10)
            {
                formattedHour= "0" + i;
            }
            else
            {
                formattedHour = "" + i;
            }
            if(dayFinal<10)
            {
                formattedDay = "0" + dayFinal;
            }
            else {
                formattedDay = "" + dayFinal;
            }

            String formattedMonth;
            if(monthFinal<10)
            {
                formattedMonth = "0" + monthFinal;
            }
            else
            {
                formattedMonth = "" + monthFinal;
            }

            timeTmp = formattedHour + ":" + formattedMin;
            dateTmp = formattedDay + "/" + formattedMonth + "/" + yearFinal;
            String tmpDate =  yearFinal + formattedMonth + formattedDay + "";
            dateToScramble = Integer.parseInt(tmpDate);

            text_depart_time.setText(timeTmp);
            text_depart_date.setText(dateTmp);
            text_duration.setText(route_duration);
            text_distance.setText(route_distance);
            text_start_address.setText(route_start);
            Log.d(TAG, route_start + "|" + route_duration + "|" + route_end);
            text_end_address.setText(route_end);
            routeFlag = 1;
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(DateTimeActivity.this, LoginActivity.class);
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
    protected void onStop()
    {
        super.onStop();
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
        Intent intent_to_dash = new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(intent_to_dash);
    }

    public void uploadRoute()
    {
        Route thisRoute;
        thisRoute = new Route(route_start, route_end, route_duration, route_distance,
                source_coordinates.latitude,source_coordinates.longitude,
                destination_coordinates.latitude,destination_coordinates.longitude,
                dateTmp, timeTmp,
                user_name, user_surname, user_email, user_mobile,
                isDriver, dateToScramble,userID);
        final String DOCPATH = userID + "_" + dateTmp.replaceAll("/", "") + route_distance.replaceAll(" ", "").replace(".", "");
        DocumentReference mDocReference = mStore.collection("routes").document(DOCPATH);
        mDocReference.set(thisRoute, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "Data Uploaded Successfully: " + userID);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "Data Failed to Upload: " + e.toString());
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    String short_addr_end = getAddress(route_end);
                    DatabaseReference routeRef =  FirebaseDatabase.getInstance().getReference();
                    HashMap<String, String> routeMap = new HashMap<>();
                    routeMap.put("author", userID);
                    routeMap.put("route_start", route_start);
                    routeMap.put("route_end", short_addr_end);
                    routeRef.child("Routes").child(DOCPATH).setValue(routeMap).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                HashMap<String, String> messageNotificationMap = new HashMap<>();
                                messageNotificationMap.put("author", userID);
                                messageNotificationMap.put("message_type", "Route");
                                messageNotificationMap.put("route_start", route_start);
                                messageNotificationMap.put("route_end", route_end);

                                notificationRef.child(userID).push().setValue(messageNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Log.d(TAG, task.toString());
                                        }
                                        else
                                            Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                                    }
                                });
                                Log.d(TAG, task.toString());
                            }
                            else
                            {
                                Log.d(TAG, String.valueOf(task.getException()));
                            }
                        }
                    });
                }
            }
        });
    }
    public void downloadTmpRouteData()
    {
        mStore.collection("tmpRoute").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                route_start = documentSnapshot.getString("source_address");
                route_end = documentSnapshot.getString("destination_address");
                route_distance = documentSnapshot.getString("distance");
                route_duration = documentSnapshot.getString("duration");
                Log.d(TAG, route_start + "|" + route_duration + "|" + route_end);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }
    public void downloadUserData()
    {
        mStore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                user_name = documentSnapshot.getString("first_name");
                user_surname = documentSnapshot.getString("surname");
                user_email = documentSnapshot.getString("email");
                user_mobile = documentSnapshot.getString("mobile_no");
                isDriver = documentSnapshot.getBoolean("driver");

                Log.d(TAG, "CONTENTS:" + user_name + user_surname + user_mobile + user_email);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, e.toString());
            }
        });
    }
}
