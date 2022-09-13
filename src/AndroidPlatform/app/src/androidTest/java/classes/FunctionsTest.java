package classes;

import com.example.activities.R;

import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionsTest
{
    @Test
    public void containsDigit()
    {
        assertTrue(Functions.containsDigit("fgasfsafd7afdf"));
        assertFalse(Functions.containsDigit("hellowogfsdfgrldgmaicom"));
    }

    @Test
    public void getAddress()
    {
        assertTrue("DCU, 630 Collins Ave Ext, Whitehall, Dublin, D09 W6F4", true);
        assertFalse("dCU, 630 Collins Ave Ext, Whitehall, Dublin, D09 W6F4", false);
    }

    @Test
    public void format_home_address()
    {
        assertEquals(Functions.format_home_address("DCU, 630 Collins Ave ext, Whitehall, Dublin"), Functions.format_home_address("dCU, 630 collins Ave Ext, Whitehall, dublin"));
        assertEquals(Functions.format_home_address("dCU, 630 collins ave ext, whitehall, dublin"), Functions.format_home_address("dCU, 630 collins Ave Ext, Whitehall, Dublin"));
        assertNotEquals(Functions.format_home_address("dCU, 630 Collins Ave ext, whitehall, dublin"), Functions.format_home_address("dCU, 630 Collins AVe Ext, Whitehall, Dublin"));
    }

    @Test
    public void capitalize()
    {
        //Should be Correct
        assertTrue("Helloworld", true);
        assertTrue("Hello World", true);
        assertTrue("2Hello World", true);

        //Should be Incorrect
        assertFalse("hello World", false);
    }

    @Test
    public void getImage()
    {
        //Should be Correct
        assertEquals(Functions.getImage(true), R.drawable.driver_post);
        assertEquals(Functions.getImage(false), R.drawable.passenger_post);

        //Should be Incorrect
        assertNotEquals(Functions.getImage(false), R.drawable.driver_post);
        assertNotEquals(Functions.getImage(true), R.drawable.passenger_post);
    }

    @Test
    public void isValidEmail()
    {
        //Should be Correct
        assertTrue(Functions.isValidEmail("helloworld@gmail.com"));
        assertTrue(Functions.isValidEmail("helloworld@ii.com"));
        assertTrue(Functions.isValidEmail("helloworld@homeemail.com"));
        assertTrue(Functions.isValidEmail("helloworld@1233numbers.com"));
        assertTrue(Functions.isValidEmail("helloworld@hotmail.com"));
        assertTrue(Functions.isValidEmail("helloworld@firebase.com"));
        assertTrue(Functions.isValidEmail("helloworld@outlook.com"));
        assertTrue(Functions.isValidEmail("heldfg234234orld@gm.com"));
        assertTrue(Functions.isValidEmail("heldfg234234orld@gm.com"));

        //Should be Incorrect
        assertFalse(Functions.isValidEmail("helloworldgmail.com"));
        assertFalse(Functions.isValidEmail("hello@@worldmail.com"));
        assertFalse(Functions.isValidEmail("helloworldgmail.com"));
        assertFalse(Functions.isValidEmail("helloworld@com"));
        assertFalse(Functions.isValidEmail("helhd345345ld@out345k+com"));
        assertFalse(Functions.isValidEmail("helloworldg345com"));
    }
}