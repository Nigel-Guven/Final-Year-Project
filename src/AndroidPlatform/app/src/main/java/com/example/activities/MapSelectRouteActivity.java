package com.example.activities;

import androidx.fragment.app.FragmentActivity;
import directions.FetchURL;
import directions.TaskLoadedCallback;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import static classes.User.updateUserStatus;

public class MapSelectRouteActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback
{
    private GoogleMap mMap;
    Button satView, goButton;
    TextView tutorialText;
    private final String TAG = "MapsRouteActivity";
    RequestQueue requestQueue;
    Polyline currentRoute;
    FirebaseAuth mFirebaseAuth;
    Marker firstMarker = null, secondMarker = null;
    private Boolean flag = false;
    BitmapDescriptor srcIcon, dstIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select_route);

        srcIcon = BitmapDescriptorFactory.fromResource(R.drawable.source_marker);
        dstIcon = BitmapDescriptorFactory.fromResource(R.drawable.destination_marker);
        requestQueue = Volley.newRequestQueue(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        satView = findViewById(R.id.btSatellite);
        goButton = findViewById(R.id.button13);
        tutorialText = findViewById(R.id.textView60);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        satView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    String defText = "DEFAULT";
                    satView.setText(defText);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    String satText = "SATELLITE";
                    satView.setText(satText);
                }

            }
        });

        goButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + firstMarker.getPosition().latitude + "," + firstMarker.getPosition().longitude + "&key=" + getString(R.string.google_maps_key);
                JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(final JSONObject response1)
                    {
                        String url2 = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + secondMarker.getPosition().latitude + "," + secondMarker.getPosition().longitude + "&key=" + getString(R.string.google_maps_key);
                        JsonObjectRequest request = new JsonObjectRequest(url2, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response2)
                            {
                                try
                                {
                                    String geo_address1 = response1.getJSONArray("results").getJSONObject(0).getString("address_components");
                                    String geo_address2 = response2.getJSONArray("results").getJSONObject(0).getString("address_components");
                                    Log.d(TAG, geo_address1);
                                    Log.d(TAG, geo_address2);

                                    if(geo_address1.contains("Ireland") || geo_address1.contains("Northern Ireland"))
                                    {
                                        if (geo_address2.contains("Ireland") || geo_address2.contains("Northern Ireland"))
                                        {
                                            String route_url = getUrl(firstMarker.getPosition(), secondMarker.getPosition());
                                            flag=true;
                                            new FetchURL(MapSelectRouteActivity.this).execute(route_url, "driving");
                                            Intent intentToDateTime = new Intent(MapSelectRouteActivity.this, DateTimeActivity.class);
                                            intentToDateTime.putExtra("SRC", firstMarker.getPosition());
                                            intentToDateTime.putExtra("DST", secondMarker.getPosition());
                                            Log.d(TAG, firstMarker.getPosition() + "||" + secondMarker.getPosition());
                                            startActivity(intentToDateTime);
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(MapSelectRouteActivity.this, "This application is only available in IReland and Northern Ireland. Please adjust your route.",Toast.LENGTH_SHORT ).show();
                                    }
                                }
                                catch (JSONException e)
                                {
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
                        });
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

        });
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng point)
            {
                if(firstMarker == null) {
                    MarkerOptions options = new MarkerOptions().draggable(true).position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.source_flag));
                    firstMarker = mMap.addMarker(options);
                    tutorialText.setText(R.string.finishing_text);
                    Log.d(TAG, "GPS Coord: " + firstMarker.getPosition());
                    return;
                }
                if (secondMarker == null)
                {
                    MarkerOptions options = new MarkerOptions().draggable(true).position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_flag));
                    secondMarker = mMap.addMarker(options);
                    goButton.setVisibility(View.VISIBLE);
                    tutorialText.setText(R.string.go_text);
                    Log.d(TAG, "GPS Coord: " + secondMarker.getPosition());
                }
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {
            @Override
            public void onMarkerDragStart(Marker marker) { }
            @Override
            public void onMarkerDrag(Marker marker) { }
            @Override
            public void onMarkerDragEnd(Marker marker)
            {
                marker.setPosition(marker.getPosition());
                Log.d(TAG, marker.getPosition() + "");
            }
        });
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

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent returnToLogin = new Intent(MapSelectRouteActivity.this, LoginActivity.class);
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
        flag=true;
        Intent intent_to_home = new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(intent_to_home);
    }
}