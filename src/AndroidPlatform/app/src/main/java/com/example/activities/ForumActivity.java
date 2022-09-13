package com.example.activities;

import adapters.ForumTabsAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static classes.User.updateUserStatus;

public class ForumActivity extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth;
    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        ViewPager mViewPager = findViewById(R.id.main_tabs_pager);
        ForumTabsAdapter forumTabsAdapter = new ForumTabsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(forumTabsAdapter);
        TabLayout mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mFirebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView mBottomNavView = findViewById(R.id.bottom_navigation);
        mBottomNavView.setSelectedItemId(R.id.messaging);
        mBottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0, 0);
                        flag=true;
                        return true;
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                        overridePendingTransition(0, 0);
                        flag=true;
                        return true;
                    case R.id.messaging:
                        flag=true;
                        return true;
                    case R.id.user_history:
                        flag=true;
                        startActivity(new Intent(getApplicationContext(),UserStatisticsActivity.class));
                        overridePendingTransition(0, 0);
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
            Intent returnToLogin = new Intent(ForumActivity.this, LoginActivity.class);
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
    protected void onDestroy()
    {
        super.onDestroy();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    public void onBackPressed()
    {
        flag=true;
        Intent intent_to_home = new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(intent_to_home);
    }

}
