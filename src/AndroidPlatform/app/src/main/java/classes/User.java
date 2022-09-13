package classes;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * Type: Object Class
 * Defines a user object
 *
 **/
@SuppressWarnings({"WeakerAccess", "unused"})
public class User
{
    /**
     *
     * Class Variables
     *
     **/
    public String address, date_of_birth, email, first_name, gender, mobile_no, profile_photo_url, surname;
    public Boolean driver;
    static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    static FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    /**
     *
     * Constructors
     *
     **/
    User(){}

    public User(String address, String date_of_birth, Boolean driver,String email, String first_name, String gender, String mobile_no, String profile_photo_url, String surname)
    {
        this.address = address;
        this.date_of_birth = date_of_birth;
        this.email = email;
        this.first_name = first_name;
        this.gender = gender;
        this.mobile_no = mobile_no;
        this.profile_photo_url = profile_photo_url;
        this.surname = surname;
        this.driver = driver;
    }

    /**
     *
     * Getters and Setters
     *
     **/
    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) { this.profile_photo_url = profile_photo_url; }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean getDriver() {
        return driver;
    }

    public void setDriver(Boolean driver) {
        this.driver = driver;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * Type: Class Function
     * Update User online status
     *
     **/
    public static void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentTime = new SimpleDateFormat("H:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();

        onlineStateMap .put("time", saveCurrentTime);
        onlineStateMap .put("date", saveCurrentDate);
        onlineStateMap .put("state", state);

        String currentUserID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();

        databaseReference.child("Users").child(currentUserID).child("user_status").updateChildren(onlineStateMap);
    }
}
