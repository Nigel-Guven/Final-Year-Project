package com.example.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import classes.Route;
import directions.FetchURL;
import directions.TaskLoadedCallback;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static classes.TripListItem.formatDistance;
import static classes.TripListItem.formatTime;
import static classes.User.updateUserStatus;

public class PostDetailsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback
{
    private GoogleMap mMap;
    private final String TAG = "PostDetailsActivity";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textViewInfo, textOrigin, textDest;
    private String routeId, routeDistance, routeDuration;
    private LatLng coordinate, SRC, DST;
    private Polyline currentRoute;
    private double mylatitude, mylongitude;
    private Boolean isOffer;
    private RequestQueue requestQueue;
    private FirebaseAuth mFirebaseAuth;
    private Boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child("Views");

        Intent intentFromDash = getIntent();
        Route routeItem = intentFromDash.getParcelableExtra("routeItem");

        textOrigin = findViewById(R.id.textView91);
        textDest = findViewById(R.id.textView99);
        textViewInfo = findViewById(R.id.textView107);
        TextView textDate = findViewById(R.id.textView94);
        TextView textTime = findViewById(R.id.textView96);
        TextView textDuration = findViewById(R.id.textView97);
        TextView textDistance = findViewById(R.id.textView98);
        TextView textEmail = findViewById(R.id.textView103);
        TextView textMobile = findViewById(R.id.textView104);
        TextView textPostType = findViewById(R.id.textView106);
        TextView textName = findViewById(R.id.textView102);

        final Button buttonToProfile = findViewById(R.id.button18);
        final Button buttonToMessaging = findViewById(R.id.button19);
        final Button buttonToAccept = findViewById(R.id.btAccept);

        requestQueue = Volley.newRequestQueue(this);
        String userID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid().trim();
        if(routeItem!=null)
        {
            routeId = routeItem.getUser_identifier().trim();

            if(!routeId.equals(userID))
            {
                final String DOCPATH = routeId + "_" + routeItem.getDate().replaceAll("/", "") + routeItem.getRoute_distance().replaceAll(" ", "").replace(".", "");
                HashMap<String, String> viewNotificationMap = new HashMap<>();
                viewNotificationMap.put("author", userID);
                viewNotificationMap.put("route", DOCPATH);
                viewNotificationMap.put("message_type", "post_view");
                notificationRef.child(routeId).push().setValue(viewNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Log.d(TAG, "Notification sent to owner of post.");
                    }
                });
            }

            textOrigin.setText(routeItem.getRoute_start());
            textDest.setText(routeItem.getRoute_end());
            textDate.setText(routeItem.getDate());
            textTime.setText(routeItem.getTime());
            textDuration.setText(routeItem.getRoute_duration());
            textDistance.setText(routeItem.getRoute_distance());
            isOffer = routeItem.getIs_offer();


            if(routeId.equals(userID))
            {
                Log.d(TAG, routeItem.getUser_identifier());
                Log.d(TAG, userID);
                Log.d(TAG, "RouteID" + routeItem.getUser_identifier());
                Log.d(TAG,"USERID" + userID);
                buttonToProfile.setVisibility(View.GONE);
                buttonToMessaging.setVisibility(View.GONE);
                buttonToAccept.setVisibility(View.GONE);
                textPostType.setText(R.string.owner_text);
                textViewInfo.setVisibility(View.GONE);
            }
            else if(routeItem.getIs_offer())
            {
                textPostType.setText(R.string.driver_text);
            }
            else
            {
                textPostType.setText(R.string.passenger_text);
            }

            routeDistance = routeItem.getRoute_distance();
            routeDuration = routeItem.getRoute_duration();
            String name = routeItem.getFirst_name()+" "+routeItem.getSurname();
            textName.setText(name);
            textEmail.setText(routeItem.getEmail());
            textMobile.setText(routeItem.getMobile());

            SRC = new LatLng(routeItem.getStart_coordinate_lat(), routeItem.getStart_coordinate_lng());
            DST = new LatLng(routeItem.getEnd_coordinate_lat(),routeItem.getEnd_coordinate_lng());

            buttonToMessaging.setEnabled(false);
            buttonToMessaging.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    buttonToMessaging.setEnabled(true);
                }
            }, 2000);

            buttonToAccept.setEnabled(false);
            buttonToAccept.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    buttonToAccept.setEnabled(true);
                }
            }, 2000);

            buttonToProfile.setEnabled(false);
            buttonToProfile.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    buttonToProfile.setEnabled(true);
                }
            }, 2000);

            String route_urlActual = getUrl(SRC,DST);
            new FetchURL(PostDetailsActivity.this).execute(route_urlActual,"driving");

            buttonToMessaging.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    flag = true;
                    Intent intentToProfile = new Intent(PostDetailsActivity.this, ViewProfileActivity.class);
                    intentToProfile.putExtra("USERID", routeId);
                    intentToProfile.putExtra("USERINFO", textViewInfo.getText().toString());
                    intentToProfile.putExtra("USERSRC", textOrigin.getText().toString());
                    intentToProfile.putExtra("USERDST", textDest.getText().toString());
                    Log.d(TAG,textViewInfo.getText() + "");
                    startActivity(intentToProfile);
                }
            });

            buttonToAccept.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    flag = true;
                    Intent intentToProfile = new Intent(PostDetailsActivity.this, ViewProfileActivity.class);
                    intentToProfile.putExtra("USERID", routeId);
                    intentToProfile.putExtra("USERINFO", textViewInfo.getText().toString());
                    intentToProfile.putExtra("USERSRC", textOrigin.getText().toString());
                    intentToProfile.putExtra("USERDST", textDest.getText().toString());
                    Log.d(TAG,textViewInfo.getText() + "");
                    startActivity(intentToProfile);
                }
            });

            buttonToProfile.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    flag = true;
                    Intent intentToProfile = new Intent(PostDetailsActivity.this, ViewProfileActivity.class);
                    intentToProfile.putExtra("USERID", routeId);
                    intentToProfile.putExtra("USERINFO", textViewInfo.getText().toString());
                    intentToProfile.putExtra("USERSRC", textOrigin.getText().toString());
                    intentToProfile.putExtra("USERDST", textDest.getText().toString());
                    Log.d(TAG,textViewInfo.getText() + "");
                    startActivity(intentToProfile);
                }
            });
        }
    }
    private String getUrl(LatLng origin, LatLng dest)
    {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + "driving";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String outputType = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + outputType + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        LatLng centre = new LatLng(53.429530, -7.929585);
        Log.d("Centre On Ireland", centre.latitude + " " + centre.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centre,6));
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(PostDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
        else
        {
            getCurrentLocation();
            Log.d("FUSED_API", " MM " + mylatitude + " MM");
        }

    }
    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode  == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                    mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted. Please Enable Location Information in Settings", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(PostDetailsActivity.this, LoginActivity.class);
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
            Log.d("ONLOGDESTROY", "THIS");
            updateUserStatus("offline");
        }
    }

    @Override
    public void onBackPressed()
    {
        flag = true;
        finish();
        Intent intent_to_dash = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent_to_dash);
    }

    @Override
    public void onTaskDone (Object...values)
    {
        if (currentRoute != null)
        {
            currentRoute.remove();
        }
        currentRoute = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void getCurrentLocation()
    {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(PostDetailsActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback()
                {
                    @Override
                    public void onLocationResult(LocationResult locationResult)
                    {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(PostDetailsActivity.this).removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0)
                        {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            mylatitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            mylongitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            Log.d("FUSED_LOCATION_API", mylatitude + " " + mylongitude);
                            if (mylatitude != 0.0)
                            {
                                MarkerOptions options = new MarkerOptions().position(SRC).icon(BitmapDescriptorFactory.fromResource(R.drawable.source_flag));
                                MarkerOptions options2 = new MarkerOptions().position(DST).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_flag));
                                mMap.addMarker(options);
                                mMap.addMarker(options2);
                                coordinate = new LatLng(mylatitude, mylongitude);
                                MarkerOptions options3 = new MarkerOptions().position(coordinate);
                                mMap.addMarker(options3);
                                Log.d("FUSED_API", "" + mylatitude);
                                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 7);
                                mMap.animateCamera(yourLocation);
                            }
                            String urlToUser1 = "https://maps.googleapis.com/maps/api/directions/json?origin=" + SRC.latitude + ","+ SRC.longitude + "&destination=" + mylatitude + "," + mylongitude + "&key=" + getString(R.string.google_maps_key);
                            Log.d("FUSEDAPI", urlToUser1);
                            JsonObjectRequest request = new JsonObjectRequest(urlToUser1, new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(final JSONObject response1)
                                {
                                    String urlToUser2 = "https://maps.googleapis.com/maps/api/directions/json?origin=" + DST.latitude + ","+ DST.longitude + "&destination=" + mylatitude + "," + mylongitude + "&key=" + getString(R.string.google_maps_key);
                                    Log.d("FUSEDAPI", urlToUser2);
                                    JsonObjectRequest request = new JsonObjectRequest(urlToUser2, new Response.Listener<JSONObject>(){
                                        @Override
                                        public void onResponse(JSONObject response2) {
                                            try
                                            {
                                                JSONArray jRoutes1 = response1.getJSONArray("routes");
                                                JSONArray jLegs1 = ((JSONObject) jRoutes1.get(0)).getJSONArray("legs");
                                                JSONObject srcToUsr_distanceJSON1 = jLegs1.getJSONObject(0).getJSONObject("distance");
                                                JSONObject srcToUsr_durationJSON1 = jLegs1.getJSONObject(0).getJSONObject("duration");
                                                double srcToUsr_distance1 = formatDistance(srcToUsr_distanceJSON1.getString("text"));
                                                int srcToUsr_duration1 = formatTime(srcToUsr_durationJSON1.getString("text"));
                                                Log.d("FUSEDAPI", srcToUsr_distance1 + " || " + srcToUsr_duration1);

                                                JSONArray jRoutes2 = response2.getJSONArray("routes");
                                                JSONArray jLegs2 = ((JSONObject) jRoutes2.get(0)).getJSONArray("legs");
                                                JSONObject srcToUsr_distanceJSON2 = jLegs2.getJSONObject(0).getJSONObject("distance");
                                                JSONObject srcToUsr_durationJSON2 = jLegs2.getJSONObject(0).getJSONObject("duration");
                                                double srcToUsr_distance2 = formatDistance(srcToUsr_distanceJSON2.getString("text"));
                                                int srcToUsr_duration2 = formatTime(srcToUsr_durationJSON2.getString("text"));

                                                Log.d("FUSEDAPI", srcToUsr_distance2 + " || " + srcToUsr_duration2);
                                                Log.d("FUSEDAPI", routeDistance + " || " + routeDuration);

                                                double srcToDst_distance = formatDistance(routeDistance);
                                                int srcToDst_duration = formatTime(routeDuration);

                                                double new_distance = (srcToUsr_distance1 + srcToUsr_distance2) - srcToDst_distance;
                                                int new_duration = (srcToUsr_duration1 + srcToUsr_duration2) - srcToDst_duration;
                                                Log.d("TAG_A", routeDistance+"");
                                                if(isOffer)
                                                {
                                                    String info_line = "Picking you up will add " + Math.round(new_distance) + " km(s) to the journey and " + new_duration + " minutes in extra time.";
                                                    textViewInfo.setText(info_line);
                                                }
                                                else {
                                                    String info_line = "Picking this person up will add " + Math.round(new_distance) + " km(s) to the journey and " + new_duration + " minutes in extra time.";
                                                    textViewInfo.setText(info_line);
                                                }
                                            } catch (JSONException e) {
                                                textViewInfo.setText(R.string.json_path_error);
                                                e.printStackTrace();
                                            }
                                        }
                                        }, new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error2)
                                            {
                                                Log.d(TAG, error2.toString());
                                            }
                                        }
                                    );
                                    requestQueue.add(request);
                                }
                            }, new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error1)
                                {
                                    Log.d(TAG, error1.toString());
                                }
                            });
                            requestQueue.add(request);
                        }
                    }
                }, Looper.getMainLooper());
    }
}
