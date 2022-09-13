package classes;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

/**
 *
 * Type: Object Class
 * Contact class is a model class used for FirebaseRecyclerUI inside of ContactsFragment
 *
 **/
@SuppressWarnings({"unused"})
public class Contact
{
    /**
     *
     * Class Variables
     *
     **/
    private String driver, email, first_name, mobile_no, profile_photo_url, surname, uid;
    private Vehicle user_vehicle;
    private static String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    /**
     *
     * Constructors
     *
     **/
    public Contact(){}

    public Contact(String driver, String email, String first_name, String mobile_no, String profile_photo_url, String surname, String uid, Vehicle user_vehicle)
    {
        this.driver = driver;
        this.email = email;
        this.first_name = first_name;
        this.mobile_no = mobile_no;
        this.profile_photo_url = profile_photo_url;
        this.surname = surname;
        this.uid = uid;
        this.user_vehicle = user_vehicle;
    }

    /**
     *
     * Getters and Setters
     *
     **/
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public Vehicle getUser_vehicle() {
        return user_vehicle;
    }

    public void setUser_vehicle(Vehicle user_vehicle) {
        this.user_vehicle = user_vehicle;
    }

    public static void deleteContact(final String list_of_userID, final DatabaseReference reference)
    {
        reference.child(UID).child(list_of_userID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                reference.child(list_of_userID).child(UID).removeValue();
            }
        });
    }
}
