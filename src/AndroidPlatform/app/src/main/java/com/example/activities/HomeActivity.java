package com.example.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import classes.Vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import static classes.Functions.downloadImage;
import static classes.User.updateUserStatus;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private final String TAG = "PROF ACTIVITY";
    ImageView profile_image, settings_image;
    TextView tViewName, tViewAddress, tViewEmail, tViewMobile, tViewCar;

    StorageReference profile_imagesRef;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore mStore;

    DrawerLayout drawerLayout;
    NavigationView mNavigationView;
    Toolbar toolbar;
    DatabaseReference userRef, databaseReference;
    String userID, user_name, user_surname, user_address, user_email, user_mobile;
    Boolean isDriver;
    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profile_image = findViewById(R.id.imageView3);
        settings_image = findViewById(R.id.imageView4);
        tViewName = findViewById(R.id.textView32);
        tViewAddress = findViewById(R.id.textView39);
        tViewEmail = findViewById(R.id.textView41);
        tViewMobile = findViewById(R.id.textView43);
        tViewCar = findViewById(R.id.textViewCar);
        drawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mNavigationView.setNavigationItemSelectedListener(this);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");



        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + userID + ".jpg");

        mNavigationView.bringToFront();
        drawerLayout.requestLayout();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nev_bar, R.string.close_nav_bar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        settings_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
                mNavigationView.bringToFront();

            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                        flag=true;
                        break;
                    case R.id.logout:
                        startActivity(new Intent(getApplicationContext(),LogOutActivity.class));
                        flag=true;
                        break;
                    case R.id.rate:
                        startActivity(new Intent(getApplicationContext(),RateUsActivity.class));
                        flag=true;
                        break;
                }
                return false;
            }
        });



        mStore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user_name = documentSnapshot.getString("first_name");
                user_surname = documentSnapshot.getString("surname");
                user_address = documentSnapshot.getString("address");
                user_email = documentSnapshot.getString("email");
                user_mobile = documentSnapshot.getString("mobile_no");
                isDriver = documentSnapshot.getBoolean("driver");

                //noinspection ConstantConditions
                if(isDriver)
                {
                    Vehicle user_vehicle = documentSnapshot.get("vehicle",Vehicle.class);
                    assert user_vehicle != null;
                    tViewCar.setText(user_vehicle.getCar_type());
                }
                else
                {
                    tViewCar.setVisibility(View.GONE);
                }
                String USERNAME = user_name + " " + user_surname;
                tViewName.setText(USERNAME);
                tViewAddress.setText(user_address);
                tViewEmail.setText(user_email);
                tViewMobile.setText(user_mobile);
                downloadImage(profile_imagesRef, profile_image);

                Log.d(TAG, "CONTENTS:" + user_name + user_surname + user_mobile + user_email + user_address);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });

        BottomNavigationView mBottomNavView = findViewById(R.id.bottom_navigation);
        mBottomNavView.setSelectedItemId(R.id.home);
        mBottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.home:
                        flag=true;
                        return true;
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                        overridePendingTransition(0, 0);
                        flag=true;
                        return true;
                    case R.id.messaging:
                        startActivity(new Intent(getApplicationContext(),ForumActivity.class));
                        overridePendingTransition(0, 0);
                        flag=true;
                        return true;
                    case R.id.user_history:
                        startActivity(new Intent(getApplicationContext(),UserStatisticsActivity.class));
                        overridePendingTransition(0, 0);
                        flag=true;
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(returnToLogin);
        }
        else {
            Log.d(TAG,"START");
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
            Log.d(TAG,"STOP");
            updateUserStatus("offline");
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            flag=true;
            Intent intent_to_dash = new Intent(getApplicationContext(),DashboardActivity.class);
            startActivity(intent_to_dash);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { return false; }
}



