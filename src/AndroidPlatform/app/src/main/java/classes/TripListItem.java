package classes;

import android.util.Log;

/**
 *
 * Type: Object Class
 * Defines an item which stores only certain pieces of Route class for storing in a List.
 *
 **/
@SuppressWarnings({"unused"})
public class TripListItem
{
    /**
     *
     * Class Variables
     *
     **/
    private double routeDistance;
    private int routeTime;

    /**
     *
     * Constructors
     *
     **/
    public TripListItem(){}

    public TripListItem(double routeDistance, int routeTime)
    {
        this.routeDistance = routeDistance;
        this.routeTime = routeTime;
    }

    /**
     *
     * Getters and Setters
     *
     **/
    public int getRouteTime() { return routeTime; }

    public void setRouteTime(int routeTime) { this.routeTime = routeTime; }

    public double getRouteDistance() { return routeDistance; }

    public void setRouteDistance(double routeDistance) { this.routeDistance = routeDistance; }

    /**
     *
     * Type: Function
     * Convert hours and minutes into minutes
     *
     **/
    public static int formatTime(String routeDuration)
    {
        String [] hourMin = routeDuration.split(" ");
        if(hourMin.length==2)
        {
            return Integer.parseInt(hourMin[0]);
        }
        else if(hourMin.length==4)
        {
            int hour = Integer.parseInt(hourMin[0]);
            int min = Integer.parseInt(hourMin[2]);
            return (hour*60) + min;
        }
        else return 0;
    }

    /**
     *
     * Type: Function
     * Convert minutes into hours and minutes.
     *
     **/
    public static String timeToString(int minutes)
    {
        if(minutes==1)
        {
            return minutes + " minute.";
        }
        if((minutes<60) && (minutes > 1))
        {
            return minutes + " minutes.";
        }
        else if(minutes == 60)
        {
            return 1 + " hour.";
        }
        else if(minutes == 61)
        {
            int hours = minutes/60;
            int mins = minutes%60;

            return hours + " hour, " + mins + " minute.";
        }
        else if(minutes > 60 && minutes < 120)
        {
            int hours = minutes/60;
            int mins = minutes%60;

            return hours + " hour, " + mins + " minutes.";
        }
        else if(minutes >=120)
        {
            int hours = minutes/60;
            int mins = minutes%60;

            return hours + " hours, " + mins + " minutes.";
        }
        else
            return "0 minutes.";
    }

    /**
     *
     * Type: Function
     * Convert distance based on whether it is a float or integer value
     *
     **/
    public static String distanceToString(double routeDistance)
    {
        if(routeDistance == 0.0)
        {
            return "0 km's.";
        }
        else if(routeDistance==1.0)
        {
            return "1 km.";
        }
        else if(routeDistance%1==0)
        {
            int converted = (int) Math.round(routeDistance);
            return converted + " km's.";
        }
        else return routeDistance + " km's.";
    }

    /**
     *
     * Type: Function
     * Convert string to double: Distance
     *
     **/
    public static double formatDistance(String routeDistance)
    {
        Log.d("UserStats",routeDistance);
        String [] distance = routeDistance.split(" ");
        return Double.parseDouble(distance[0]);
    }
}
