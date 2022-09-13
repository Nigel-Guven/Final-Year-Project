package classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import androidx.annotation.NonNull;

/**
 *
 * Type: Object Class
 * Defines a Route object.
 *
 **/
@SuppressWarnings({"unused"})
public class Route implements Parcelable , Comparable<Route>
{
    /**
     *
     * Class Variables
     *
     **/
    private String route_start;
    private String route_end;
    private String route_duration;
    private String route_distance;
    private Double start_coordinate_lat;
    private Double start_coordinate_lng;
    private Double end_coordinate_lat;
    private Double end_coordinate_lng;
    private String date;
    private String time;
    private String first_name;
    private String surname;
    private String email;
    private String mobile;
    private Boolean is_offer;
    private Integer date_for_comp;
    private String user_identifier;
    private static String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    /**
     *
     * Constructors
     *
     **/
    public Route() {}

    public Route(String routeStart, String routeEnd, String routeDuration, String routeDistance, Double startCoordinateLat, Double startCoordinateLng,Double endCoordinateLat, Double endCoordinateLng, String date, String time, String first_name, String surname, String email, String mobile, Boolean isOffer, Integer mdateForComp,String userIdentifier)
    {
        this.route_start = routeStart;
        this.route_end = routeEnd;
        this.route_duration = routeDuration;
        this.route_distance = routeDistance;
        this.start_coordinate_lat = startCoordinateLat;
        this.start_coordinate_lng = startCoordinateLng;
        this.end_coordinate_lat = endCoordinateLat;
        this.end_coordinate_lng = endCoordinateLng;
        this.date = date;
        this.time = time;
        this.first_name = first_name;
        this.surname = surname;
        this.email = email;
        this.mobile = mobile;
        this.is_offer = isOffer;
        this.date_for_comp = mdateForComp;
        this.user_identifier = userIdentifier;
    }

    /**
     *
     * Parcelable Implementation
     *
     **/
    protected Route(Parcel in) {
        route_start = in.readString();
        route_end = in.readString();
        route_duration = in.readString();
        route_distance = in.readString();
        if (in.readByte() == 0) {
            start_coordinate_lat = null;
        } else {
            start_coordinate_lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            start_coordinate_lng = null;
        } else {
            start_coordinate_lng = in.readDouble();
        }
        if (in.readByte() == 0) {
            end_coordinate_lat = null;
        } else {
            end_coordinate_lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            end_coordinate_lng = null;
        } else {
            end_coordinate_lng = in.readDouble();
        }
        date = in.readString();
        time = in.readString();
        first_name = in.readString();
        surname = in.readString();
        email = in.readString();
        mobile = in.readString();
        byte tmpIs_offer = in.readByte();
        is_offer = tmpIs_offer == 0 ? null : tmpIs_offer == 1;
        if (in.readByte() == 0) {
            date_for_comp = null;
        } else {
            date_for_comp = in.readInt();
        }
        user_identifier = in.readString();
    }

    /**
     *
     * Parcelable Function
     *
     **/
    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    /**
     *
     * Getters and Setters
     *
     **/
    public String getRoute_start() {
        return route_start;
    }

    public void setRoute_start(String route_start) {
        this.route_start = route_start;
    }

    public String getRoute_end() {
        return route_end;
    }

    public void setRoute_end(String route_end) {
        this.route_end = route_end;
    }

    public String getRoute_duration() {
        return route_duration;
    }

    public void setRoute_duration(String route_duration) {
        this.route_duration = route_duration;
    }

    public String getRoute_distance() {
        return route_distance;
    }

    public void setRoute_distance(String route_distance) {
        this.route_distance = route_distance;
    }

    public Double getStart_coordinate_lat() {
        return start_coordinate_lat;
    }

    public void setStart_coordinate_lat(Double start_coordinate_lat) {
        this.start_coordinate_lat = start_coordinate_lat;
    }

    public Double getStart_coordinate_lng() {
        return start_coordinate_lng;
    }

    public void setStart_coordinate_lng(Double start_coordinate_lng) {
        this.start_coordinate_lng = start_coordinate_lng;
    }

    public Double getEnd_coordinate_lat() {
        return end_coordinate_lat;
    }

    public void setEnd_coordinate_lat(Double end_coordinate_lat) {
        this.end_coordinate_lat = end_coordinate_lat;
    }

    public Double getEnd_coordinate_lng() {
        return end_coordinate_lng;
    }

    public void setEnd_coordinate_lng(Double end_coordinate_lng) {
        this.end_coordinate_lng = end_coordinate_lng;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getIs_offer() {
        return is_offer;
    }

    public void setIs_offer(Boolean is_offer) {
        this.is_offer = is_offer;
    }

    public Integer getDate_for_comp() {
        return date_for_comp;
    }

    public void setDate_for_comp(Integer date_for_comp) {
        this.date_for_comp = date_for_comp;
    }

    public String getUser_identifier() {
        return user_identifier;
    }

    public void setUser_identifier(String user_identifier) {
        this.user_identifier = user_identifier;
    }

    /**
     *
     * Parcelable Function
     *
     **/
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *
     * Parcelable Function
     *
     **/
    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(route_start);
        parcel.writeString(route_end);
        parcel.writeString(route_duration);
        parcel.writeString(route_distance);
        if (start_coordinate_lat == null)
        {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(start_coordinate_lat);
        }
        if (start_coordinate_lng == null)
        {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(start_coordinate_lng);
        }
        if (end_coordinate_lat == null)
        {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(end_coordinate_lat);
        }
        if (end_coordinate_lng == null)
        {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(end_coordinate_lng);
        }
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(first_name);
        parcel.writeString(surname);
        parcel.writeString(email);
        parcel.writeString(mobile);
        parcel.writeByte((byte) (is_offer == null ? 0 : is_offer ? 1 : 2));
        if (date_for_comp == null)
        {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(date_for_comp);
        }
        parcel.writeString(user_identifier);
    }

    /**
     *
     * Implemented Comparator for sorting Arraylist of Routes
     *
     **/
    @Override
    public int compareTo(@NonNull Route route)
    {
        if(getDate_for_comp()==null || route.getDate_for_comp() == null)
        {
            return 0;
        }
        return  getDate_for_comp().compareTo(route.getDate_for_comp());
    }

    /**
     *
     * Returns String array of variables. Used by Levenshtein Distance algorithm
     *
     **/
    public String [] printRoute()
    {
        String tmp = getRoute_start() + " " + getRoute_end() + " " + getFirst_name() + " " + getSurname() + " " + getDate();
        return tmp.split(" ");
    }

    /**
     *
     * Delete tmpRoute created by the DataParser.java ASync Task
     *
     **/
    public static void deleteData()
    {
        FirebaseFirestore.getInstance().collection("tmpRoute").document(UID).delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d("TAG", "DocumentSnapshot successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d("TAG", "Failed to connect to database", e);
            }
        });
    }
}
