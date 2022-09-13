package classes;

/**
 *
 * Type: Object Class
 * Defines a custom Date object utilised by DateTimeActivity and DAteOfBirth registration
 *
 **/
@SuppressWarnings({"unused"})
public class SimpleDate
{
    /**
     *
     * Class Variables
     *
     **/
    private int day,month,year;

    /**
     *
     * Constructors
     *
     **/
    public SimpleDate(){}

    public SimpleDate(int mday, int mmonth, int myear)
    {
        this.day = mday;
        this.month = mmonth;
        this.year = myear;
    }

    /**
     *
     * Type: Function
     * Print date to LogCat
     *
     **/
    public void printSimpleDate(SimpleDate sdate)
    {
        System.out.println(sdate.day + sdate.month + sdate.year);
    }

    public int getDay() { return day; }

    public void setDay(int day) { this.day = day; }

    public int getMonth() { return month; }

    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }

    public void setYear(int year) { this.year = year; }
}
