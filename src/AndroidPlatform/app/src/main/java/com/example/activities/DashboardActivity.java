package com.example.activities;

import adapters.DashboardPostAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import classes.LevenshteinDistance;
import classes.RelevantItem;
import classes.Route;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import static classes.Functions.getUniqueUsers;
import static classes.Route.deleteData;
import static classes.User.updateUserStatus;


public class DashboardActivity extends AppCompatActivity
{
    HashSet<RelevantItem> setOfRelevantItems = new HashSet<>();
    HashSet<String> setOfUniqueItems = new HashSet<>();
    private TextView no_posts, active_users, active_posts;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Boolean flag = false;
    private String userID;
    private FirebaseAuth mAuth;
    private CollectionReference routeCollectionReference = db.collection("routes");
    private DashboardPostAdapter dashboardPostAdapter;
    private double levenshtein_threshold = 0.6;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        BottomNavigationView mBottomNavView = findViewById(R.id.bottom_navigation);
        FloatingActionButton postButton = findViewById(R.id.floatingActionButton);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final SearchView searchView = findViewById(R.id.searchView);
        active_users = findViewById(R.id.textView84);
        active_posts = findViewById(R.id.textView85);
        no_posts = findViewById(R.id.empty);
        deleteData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(final String query)
            {
                Log.d("DASHTAG", query);
                db.collection("routes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                if(!document.getId().contains(userID))
                                {
                                    Route route  = document.toObject(Route.class);
                                    String [] tmpArr = route.printRoute();

                                    for(String iterate : tmpArr)
                                    {
                                        if(LevenshteinDistance.returnDistance(iterate, query) > levenshtein_threshold)
                                        {
                                            Log.d("DASHTAG", "Levenshtein: " + iterate + " = " + LevenshteinDistance.returnDistance(iterate,query) + "\n");
                                            RelevantItem tmpItem = new RelevantItem(document.getId(), route,LevenshteinDistance.returnDistance(iterate,query));

                                            if(!setOfUniqueItems.contains(tmpItem.getDocumentId()))
                                            {
                                                setOfRelevantItems.add(tmpItem);
                                            }
                                            setOfUniqueItems.add(tmpItem.getDocumentId());
                                        }
                                    }
                                }
                            }
                            ArrayList<RelevantItem> routeList = new ArrayList<>(setOfRelevantItems);

                            if(!routeList.isEmpty())
                            {
                                searchView.setQuery("", false);
                                searchView.setIconified(true);
                                Intent intentToResults = new Intent(DashboardActivity.this,SearchReturnActivity.class);
                                intentToResults.putParcelableArrayListExtra("RELEVANTRESULT", routeList);
                                startActivity(intentToResults);
                            }
                            else {
                                Toast.makeText(DashboardActivity.this, R.string.bad_query_text, Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d("TAG", e.toString());
                        }
                    });
                    return false;
            }
            @Override
            public boolean onQueryTextChange(String s) { return false; }
        });



        createRecyclerView();

        dashboardPostAdapter.getSnapshots().addChangeEventListener(new ChangeEventListener()
        {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) { }
            @Override
            public void onDataChanged()
            {
                String posts = "" + dashboardPostAdapter.getItemCount();
                active_posts.setText(posts);

                if(dashboardPostAdapter.getItemCount() == 0)
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    searchView.setVisibility(View.INVISIBLE);
                    no_posts.setVisibility(View.VISIBLE);
                }
                String users = getUniqueUsers(dashboardPostAdapter.getSnapshots()) + "";
                active_users.setText(users);
            }
            @Override
            public void onError(@NonNull FirebaseFirestoreException e) { Log.d("TAG", Objects.requireNonNull(e.getMessage())); }
        });


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
                        flag=true;
                        return true;
                    case R.id.dashboard:
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
        postButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                flag=true;
                Intent intent = new Intent(DashboardActivity.this, MapSelectRouteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createRecyclerView()
    {
        Query query = routeCollectionReference.orderBy("date_for_comp", Query.Direction.ASCENDING).orderBy("time",Query.Direction.ASCENDING).limit(30);
        FirestoreRecyclerOptions<Route> options = new FirestoreRecyclerOptions.Builder<Route>().setQuery(query,Route.class).build();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dashboardPostAdapter = new DashboardPostAdapter(options);
        recyclerView.setAdapter(dashboardPostAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dashboardPostAdapter.setOnItemClickListener(new DashboardPostAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position)
            {
                flag=true;
                Route clicked_route  =  documentSnapshot.toObject(Route.class);

                Intent intentToPostDetails = new Intent(DashboardActivity.this, PostDetailsActivity.class);
                intentToPostDetails.putExtra("routeItem", clicked_route);
                startActivity(intentToPostDetails);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        dashboardPostAdapter.startListening();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(DashboardActivity.this, LoginActivity.class);
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
        dashboardPostAdapter.stopListening();
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
        dashboardPostAdapter.stopListening();
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
        Intent intent_to_home = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent_to_home);
    }

}


