package com.example.activities;

import adapters.UserTripsAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import classes.Route;
import classes.TripListItem;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static classes.User.updateUserStatus;

public class UserStatisticsActivity extends AppCompatActivity
{
    private static final String TAG = "UserStatisticsActivity";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private Boolean flag = false;
    private int min = 99999999;
    private String routeId;
    TextView tViewUpcomingJourneys, tViewTotalDistance, tViewTotalTime,
            tViewDateTime, tViewStartAddr, tViewEndAddr, tViewDistanceDuration,
            tViewOrigin, tViewDest, tViewOnePost;
    ArrayList<TripListItem> listOfUserTrips = new ArrayList<>();
    ArrayList<Route> listOfUserOwnedPosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();
        final String currentUserID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        tViewUpcomingJourneys = findViewById(R.id.textView117);
        tViewTotalDistance = findViewById(R.id.textView118);
        tViewTotalTime = findViewById(R.id.textView119);
        tViewDateTime = findViewById(R.id.textview_date_time);
        tViewStartAddr = findViewById(R.id.textView122);
        tViewEndAddr = findViewById(R.id.textView123);
        tViewDistanceDuration = findViewById(R.id.textView121);
        tViewOrigin = findViewById(R.id.textview_start_addr);
        tViewDest = findViewById(R.id.textview_end_addr);
        tViewOnePost = findViewById(R.id.textView124);

        mFirestore.collection("routes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(Objects.requireNonNull(task.getResult()).isEmpty())
                {
                    tViewUpcomingJourneys.setText(R.string.embarrassing_empty_dash);
                    tViewUpcomingJourneys.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tViewTotalDistance.setVisibility(View.INVISIBLE);
                    tViewTotalTime.setVisibility(View.INVISIBLE);
                }
                else if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                    {

                        if(document.getId().contains(currentUserID))
                        {
                            Route route  = document.toObject(Route.class);

                            int lowest_number = route.getDate_for_comp();
                            if(lowest_number < min)
                            {
                                min = lowest_number;
                                routeId = document.getId();
                                Log.d(TAG,"Earliest: " + routeId);
                            }

                            TripListItem item = new TripListItem();

                            item.setRouteTime(TripListItem.formatTime(route.getRoute_duration()));
                            item.setRouteDistance(TripListItem.formatDistance(route.getRoute_distance()));

                            listOfUserTrips.add(item);
                            listOfUserOwnedPosts.add(route);
                            Log.d("TAG", route.getRoute_start());
                        }
                        Log.d(TAG,document.getId() + " => " + listOfUserTrips.size() + "\n");
                    }

                    displayTripsInformation();

                    if(listOfUserTrips.size() > 0)
                    {
                        getNextRoute(routeId);
                    }

                    if(listOfUserOwnedPosts.size()>0)
                    {
                        if(listOfUserOwnedPosts.size()==1)
                        {
                            listOfUserOwnedPosts.remove(0);
                            tViewOnePost.setVisibility(View.VISIBLE);
                        }
                        else {
                            fillRecyclerView(listOfUserOwnedPosts);
                        }
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });



        BottomNavigationView mBottomNavView = findViewById(R.id.bottom_navigation);
        mBottomNavView.setSelectedItemId(R.id.user_history);
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
                        startActivity(new Intent(getApplicationContext(),ForumActivity.class));
                        overridePendingTransition(0, 0);
                        flag=true;
                        return true;
                    case R.id.user_history:
                        flag=true;
                        return true;
                }
                return false;
            }
        });
    }

    private void fillRecyclerView(ArrayList<Route> listOfUserOwnedPosts)
    {
        Log.d("UNSORTED_TAG_LIST", String.valueOf(listOfUserOwnedPosts));
        Collections.sort(listOfUserOwnedPosts);
        listOfUserOwnedPosts.remove(0);
        Log.d("SORTED_TAG_LIST", String.valueOf(listOfUserOwnedPosts));
        RecyclerView recyclerView = findViewById(R.id.recyclerview_trips);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        UserTripsAdapter tripsAdapter = new UserTripsAdapter(this, listOfUserOwnedPosts);
        recyclerView.setAdapter(tripsAdapter);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(UserStatisticsActivity.this, LoginActivity.class);
            startActivity(returnToLogin);
        }
        else {
            Log.d("ONLOGSTART", "THIS");
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
            Log.d("ONLOGPAUSE", "THIS");
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
            Log.d("ONLOGDESTROY", "THIS");
            updateUserStatus("offline");
        }
    }

    private void displayTripsInformation()
    {
        String prefix = "You have ";
        String list_count = "" + listOfUserTrips.size();
        String suffix;

        if(listOfUserTrips.size()==1)
        {
            suffix = " upcoming journey.";
            formatDisplay(prefix, suffix, list_count);
            displayTotalTimeandDuration(listOfUserTrips);
        }
        else if(listOfUserTrips.size()>=2)
        {
            suffix = " upcoming journeys.";
            formatDisplay(prefix, suffix, list_count);
            displayTotalTimeandDuration(listOfUserTrips);
        }
        else
        {
            tViewUpcomingJourneys.setText(R.string.no_active_posts);
            tViewUpcomingJourneys.setGravity(Gravity.CENTER_HORIZONTAL);
            tViewUpcomingJourneys.setTypeface(null, Typeface.BOLD);
            tViewTotalDistance.setVisibility(View.INVISIBLE);
            tViewTotalTime.setVisibility(View.INVISIBLE);
        }
    }

    private void displayTotalTimeandDuration(ArrayList<TripListItem> tripList)
    {
        int duration_Total = 0;
        double distance_Total = 0.0;

        for(TripListItem tmp : tripList)
        {
            duration_Total += tmp.getRouteTime();
            distance_Total += tmp.getRouteDistance();
        }
        Log.d(TAG, "" + duration_Total + " " + distance_Total);


        String distTextTmp = TripListItem.distanceToString(distance_Total);
        String timeTextTmp = TripListItem.timeToString(duration_Total);

        tViewTotalDistance.setText(R.string.your_distance_text);
        tViewTotalTime.setText(R.string.your_time_text);
        formatDisplay( distTextTmp,tViewTotalDistance);
        formatDisplay(timeTextTmp ,tViewTotalTime);

    }

    private void formatDisplay(String prefixPhrase , String suffixPhrase, String count)
    {
        SpannableString spannable_number = new SpannableString(count);
        spannable_number.setSpan(new StyleSpan(Typeface.ITALIC), 0, count.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_number.setSpan(new StyleSpan(Typeface.BOLD), 0, count.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        tViewUpcomingJourneys.setText(prefixPhrase);
        tViewUpcomingJourneys.append(spannable_number);
        tViewUpcomingJourneys.append(suffixPhrase);
    }

    private void formatDisplay(String phrase, @NonNull TextView textView)
    {
        SpannableString spannable_phrase = new SpannableString(phrase);
        spannable_phrase.setSpan(new StyleSpan(Typeface.ITALIC), 0, phrase.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_phrase.setSpan(new StyleSpan(Typeface.BOLD), 0, phrase.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(spannable_phrase);
    }

    private void formatDisplay(String phrase, String appended, @NonNull TextView textView)
    {
        SpannableString spannable_phrase = new SpannableString(phrase);
        spannable_phrase.setSpan(new StyleSpan(Typeface.BOLD), 0, phrase.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(spannable_phrase);
        textView.append(appended);
    }

    private void getNextRoute(String routeId)
    {
        mFirestore.collection("routes").document(routeId).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    tViewOrigin.setVisibility(View.VISIBLE);
                    tViewDest.setVisibility(View.VISIBLE);
                    String dateTmp = "Date: ";
                    String timeTmp = "  Time: ";
                    formatDisplay(dateTmp, documentSnapshot.getString("date"), tViewDateTime);
                    formatDisplay(timeTmp, documentSnapshot.getString("time"), tViewDateTime);

                    tViewStartAddr.setText(documentSnapshot.getString("route_start"));
                    tViewEndAddr.setText(documentSnapshot.getString("route_end"));

                    String durationTmp = "Duration: ";
                    String distanceTmp = "  Distance: ";
                    formatDisplay(durationTmp, documentSnapshot.getString("route_duration"), tViewDistanceDuration);
                    formatDisplay(distanceTmp, documentSnapshot.getString("route_distance"), tViewDistanceDuration);
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

    @Override
    public void onBackPressed()
    {
        flag=true;
        Intent intent_to_home = new Intent(getApplicationContext(),ForumActivity.class);
        startActivity(intent_to_home);
    }
}
