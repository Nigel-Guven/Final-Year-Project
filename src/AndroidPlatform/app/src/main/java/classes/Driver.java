package classes;

/**
 *
 * Type: Object Class
 * Driver class defines a driver on the application. Extends User and adds a vehicle object
 *
 **/
@SuppressWarnings({"unused"})
public class Driver extends User
{
    /**
     *
     * Class Variables
     *
     **/
    private Vehicle vehicle;

    /**
     *
     * Constructors
     *
     **/
    public Driver() {}

    public Driver(String address, String date_of_birth, Boolean driver, String email, String first_name, String gender, String mobile_no, String profile_photo_url, String surname, Vehicle vehicle)
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
        this.vehicle = vehicle;
    }

    /**
     *
     * Getters and Setters
     *
     **/
    public Vehicle getVehicle()
    {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle)
    {
        this.vehicle = vehicle;
    }
}
