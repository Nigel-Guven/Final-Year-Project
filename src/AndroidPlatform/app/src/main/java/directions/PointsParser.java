package directions;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 *
 * Credit Code City https://github.com/Vysh01/android-maps-directions
 * Type: Supporting class for Directions
 * Parse Individual Points in the returned Directions API JSON data
 *
 **/
public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
{
    /**
     *
     * Class Variables
     *
     **/
    private TaskLoadedCallback taskCallback;
    private String directionMode = "driving";

    /**
     *
     * Constructors
     *
     **/
    PointsParser(Context mContext, String directionMode)
    {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    /**
     *
     * Fetch DirectionsAPI JSON data
     * Add JSON points to a list of points
     *
     **/
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
    {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);

            DataParser parser;
            parser = new DataParser();

            // Starts parsing data
            routes = parser.parse(jObject);

        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    /**
     *
     * ASync post execute
     * Once data is successfully retrieved, Points are converted into double values
     *
     **/
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        try
        {
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                if (directionMode.equalsIgnoreCase("walking")) {
                    lineOptions.width(10);
                    lineOptions.color(Color.MAGENTA);
                } else {
                    lineOptions.width(20);
                    lineOptions.color(Color.BLUE);
                }
                Log.d("mylog", "onPostExecute lineoptions decoded");
            }
        }
        catch(NullPointerException np)
        {
            System.out.println("np");
        }



        /* Drawing polyline in the Google Map for the i-th route */
        if (lineOptions != null)
        {
            taskCallback.onTaskDone(lineOptions);
        } else {
            Log.d("mylog", "without Polylines drawn");
        }
    }
}
