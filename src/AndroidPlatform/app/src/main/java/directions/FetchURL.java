package directions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * Credit Code City https://github.com/Vysh01/android-maps-directions
 * Type: Supporting class for Directions
 * Parse JSON object from Google Directions API
 * Retrieve context of DirectionsAPI call
 *
 **/
public class FetchURL extends AsyncTask<String, Void, String>
{

    /**
     *
     * Class Variables
     *
     **/
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private String directionMode = "driving";

    /**
     *
     * Constructors
     *
     **/
    public FetchURL(Context mContext) {
        this.mContext = mContext;
    }

    /**
     *
     * Fetch DirectionsAPI JSON data
     * Return JSON data
     *
     **/
    @Override
    protected String doInBackground(String... strings)
    {
        // For storing data from web service
        String data = "";
        directionMode = strings[1];
        try
        {
            // Fetching the data from web service
            data = downloadUrl(strings[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    /**
     *
     * ASync post execute
     * Once data is successfully retrieved, create a parser to read individual points in the JSON
     *
     **/
    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser(mContext, directionMode);
        // Invokes the thread for parsing the JSON data
        parserTask.execute(s);
    }

    /**
     *
     * Download URL
     *
     **/
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            @SuppressWarnings("UnusedAssignment") String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}