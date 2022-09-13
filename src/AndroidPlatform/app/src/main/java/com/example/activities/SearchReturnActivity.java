package com.example.activities;

import adapters.SearchResultsAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import classes.RelevantItem;
import classes.Route;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static classes.User.updateUserStatus;

public class SearchReturnActivity extends AppCompatActivity implements SearchResultsAdapter.OnPostClickListener, AdapterView.OnItemSelectedListener
{
    private Boolean flag = false;
    private FirebaseAuth mAuth;
    private TextView tViewQueryInformation, tViewBadQuery;
    RecyclerView recyclerView;
    ArrayList<RelevantItem> listOfRelevantRoutes = new ArrayList<>();
    Spinner spinnerSorter;
    SearchResultsAdapter resultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_return);

        mAuth = FirebaseAuth.getInstance();
        tViewBadQuery = findViewById(R.id.textView127);
        Button btnToDash = findViewById(R.id.button16);
        recyclerView = findViewById(R.id.recyclerView_search);
        spinnerSorter = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(this, R.array.sorting_posts, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSorter.setAdapter(mAdapter);
        spinnerSorter.setOnItemSelectedListener(this);

        tViewQueryInformation = findViewById(R.id.textView126);
        listOfRelevantRoutes = getIntent().getParcelableArrayListExtra("RELEVANTRESULT");

        if(listOfRelevantRoutes!=null)
        {
            setUpSearchResults(listOfRelevantRoutes);
        }
        else
        {
            tViewBadQuery.setVisibility(View.VISIBLE);
            tViewQueryInformation.setText(R.string.no_results_text);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        btnToDash.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            }
        });
        BottomNavigationView mBottomNavView = findViewById(R.id.bottom_navigation_search);
        mBottomNavView.setSelectedItemId(R.id.dashboard);
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
                        flag = true;
                        return true;
                    case R.id.dashboard:
                        flag=true;
                        startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
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

    private void setUpSearchResults(ArrayList<RelevantItem> routes)
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsAdapter = new  SearchResultsAdapter(SearchReturnActivity.this,routes, this);
        if(routes.size() == 0)
        {
            tViewBadQuery.setVisibility(View.VISIBLE);
            tViewQueryInformation.setText(R.string.no_results_text);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else if(resultsAdapter.getItemCount() > 1)
        {
            String information = getString(R.string.query_prefix_text) + " " + resultsAdapter.getItemCount() + " " +getString(R.string.query_info_suffix_plural);
            tViewQueryInformation.setText(information);
            Collections.reverse(routes);
            recyclerView.setAdapter(resultsAdapter);
        }
        else
        {
            String information = getString(R.string.query_prefix_text) + " " + resultsAdapter.getItemCount() + " " +getString(R.string.query_info_suffix);
            tViewQueryInformation.setText(information);
            recyclerView.setAdapter(resultsAdapter);
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(SearchReturnActivity.this, LoginActivity.class);
            startActivity(returnToLogin);
        }
        else {
            Log.d("Dashboard","START");
            updateUserStatus("online");
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && !flag)
        {
            Log.d("Dashboard","DESTROY");
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            Log.d("Dashboard","DESTROY");
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

    @Override
    public void onPostClick(int position)
    {
        Intent intentToPostDetails = new Intent(SearchReturnActivity.this, PostDetailsActivity.class);
        Route tmp = listOfRelevantRoutes.get(position).getRoute();
        intentToPostDetails.putExtra("routeItem", tmp);
        startActivity(intentToPostDetails);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
    {
        if(position == 0)
        {
            Collections.sort(listOfRelevantRoutes, new RelevantItem.RouteComparatorRelevance());
            resultsAdapter.notifyDataSetChanged();
        }
        if(position==1)
        {
            Collections.sort(listOfRelevantRoutes, new RelevantItem.RouteComparatorDate());
            resultsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }
}
