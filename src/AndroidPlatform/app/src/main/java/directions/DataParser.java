package directions;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;



/**
 *
 * Credit Code City https://github.com/Vysh01/android-maps-directions
 * Type: Supporting class for Directions
 * Parse JSON object from Google Directions API
 *
 **/
class DataParser
{
    private String src_address;
    private String dst_address;
    private String route_distance;
    private String route_duration;
    private String userID;

    /**
     *
     * Type: Function for JSON parser
     * Parse returned JSON polyline
     *
     **/
    List<List<HashMap<String, String>>> parse(JSONObject jObject)
    {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try
        {
            jRoutes = jObject.getJSONArray("routes");

            /* Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++)
            {

                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                src_address = jLegs.getJSONObject(0).getString("start_address");
                dst_address = jLegs.getJSONObject(0).getString("end_address");
                route_distance = jLegs.getJSONObject(0).getString("distance");
                route_duration = jLegs.getJSONObject(0).getString("duration");

                String [] tmpArray = route_distance.split(",");
                route_distance= tmpArray[0];
                route_distance = route_distance.replace("\"text\":","");
                route_distance = route_distance.replace("\"", "");
                route_distance = route_distance.replace("{","");

                String [] tmpArray2 = route_duration.split(",");
                route_duration = tmpArray2[0];
                route_duration = route_duration.replace("\"text\":","");
                route_duration = route_duration.replaceAll("\"", "");
                route_duration = route_duration.replace("{","");

                Log.d("DEBUG",  src_address+ "|" + dst_address+ "|" + route_distance + "|" + route_duration);

                List<HashMap<String, String>> path = new ArrayList<>();
                /* Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++)
                {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /* Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++)
                    {
                        String polyline;
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /* Traversing all points */
                        for (int l = 0; l < list.size(); l++)
                        {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

            userID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
            DocumentReference mdocReference = mFirestore.collection("tmpRoute").document(userID);
            Map<String, Object> route = new HashMap<>();
            route.put("source_address",src_address);
            route.put("destination_address",dst_address);
            route.put("distance",route_distance);
            route.put("duration",route_duration);

            mdocReference.set(route, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Log.d("DataParser", "Data Uploaded Successfully to TMPROUTE: " + userID);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Log.d("TAG", "Data Failed to Upload" + e.toString());
                }
            });

        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (Exception f)
        {
            Log.d("TAG", f.toString());
        }
        return routes;
    }


    /**
     *
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     *
     */
    private List<LatLng> decodePoly(String encoded)
    {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}