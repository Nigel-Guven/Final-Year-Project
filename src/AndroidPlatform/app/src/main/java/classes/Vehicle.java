package classes;


import java.io.Serializable;

/**
 *
 * Type: Object Class
 * Defines a Vehicle Object
 *
 **/
@SuppressWarnings({"unused"})
public class Vehicle implements Serializable
{
    /**
     *
     * Class Variables
     *
     **/
    private String car_type, car_registration, car_year, car_color, car_engine_cc, car_fuel_type, car_body;

    /**
     *
     * Constructors
     *
     **/
    public Vehicle(){}

    public Vehicle(String mcar_type, String mcar_registration, String mcar_year, String mcar_color, String mcar_engine_cc, String mcar_fuel_type, String mcar_body)
    {
        this.car_type = mcar_type;
        this.car_registration = mcar_registration;
        this.car_year = mcar_year;
        this.car_color=mcar_color;
        this.car_engine_cc = mcar_engine_cc;
        this.car_fuel_type = mcar_fuel_type;
        this.car_body = mcar_body;
    }

    /**
     *
     * Getters and Setters
     *
     **/
    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getCar_registration() {
        return car_registration;
    }

    public void setCar_registration(String car_registration) { this.car_registration = car_registration; }

    public String getCar_year() {
        return car_year;
    }

    public void setCar_year(String car_year) {
        this.car_year = car_year;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }

    public String getCar_engine_cc() {
        return car_engine_cc;
    }

    public void setCar_engine_cc(String car_engine_cc) {
        this.car_engine_cc = car_engine_cc;
    }

    public String getCar_fuel_type() {
        return car_fuel_type;
    }

    public void setCar_fuel_type(String car_fuel_type) {
        this.car_fuel_type = car_fuel_type;
    }

    public String getCar_body() {
        return car_body;
    }

    public void setCar_body(String car_body) {
        this.car_body = car_body;
    }
}
