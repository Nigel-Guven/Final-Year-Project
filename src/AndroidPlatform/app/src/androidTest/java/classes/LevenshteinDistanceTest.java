package classes;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class LevenshteinDistanceTest
{
    @Test
    public void returnDistance()
    {
        System.out.println("Results: " + LevenshteinDistance.returnDistance("Hello Wold", "Hello World"));
        System.out.println("Results: " + LevenshteinDistance.returnDistance("", "Hello World"));
        System.out.println("Results: " + LevenshteinDistance.returnDistance("HelloWorld", "Hello World"));
        System.out.println("Results: " + LevenshteinDistance.returnDistance("Hello Wrld", "Hello World"));
        System.out.println("Results: " + LevenshteinDistance.returnDistance("Hello", "Hello World"));
    }

    @Test
    public void longestSubstr()
    {
        Assert.assertSame(2, LevenshteinDistance.longestSubstr("Hi", "hi"));
        Assert.assertNotSame(4, LevenshteinDistance.longestSubstr("Hitt", "hi2"));
        Assert.assertEquals(6, LevenshteinDistance.longestSubstr(" Hello ", "Hello World"));
        Assert.assertEquals(0, LevenshteinDistance.longestSubstr("", "Hello World"));
        Assert.assertEquals(5, LevenshteinDistance.longestSubstr("HelloWorld", "Hello World"));
        Assert.assertEquals(7, LevenshteinDistance.longestSubstr("Hello Wrld", "Hello World"));
        Assert.assertEquals(6, LevenshteinDistance.longestSubstr("Hello ", "Hello World"));
    }

    @Test
    public void scrapeAPI() throws IOException
    {
        //API Timeout Refuse Connection
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("07D27644"), true);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("08D2764"), true);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("07D244"), true);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("07D24"), true);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("07c44"), true);

        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("09FF27"), false);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("09G27"), false);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("09789"), false);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("07"), false);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("27"), false);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI("09v27"), false);
        //Assert.assertEquals(LevenshteinDistance.scrapeAPI(""), false);
    }

    @Test
    public void isValidLicense()
    {
        Assert.assertEquals(true, LevenshteinDistance.isValidLicense("CEADUNAS TIOMANA DRIVING LICENCE"));
        Assert.assertEquals(true, LevenshteinDistance.isValidLicense("CEADUNAS TIOMANA"));
        Assert.assertEquals(true, LevenshteinDistance.isValidLicense("DRIVING LICENCE"));
        Assert.assertEquals(true, LevenshteinDistance.isValidLicense("TIOMANA DRIVING"));
        Assert.assertEquals(false, LevenshteinDistance.isValidLicense("CDNS TMN DRVNG LCNC"));
        Assert.assertEquals(false, LevenshteinDistance.isValidLicense("CEADUNAS DRIVING "));
        Assert.assertEquals(false, LevenshteinDistance.isValidLicense(""));
        Assert.assertEquals(false, LevenshteinDistance.isValidLicense("CEAD FOGHLAMORA LEARNER PERMIT"));
        Assert.assertEquals(false, LevenshteinDistance.isValidLicense("FOGHLAMORA LEARNER"));
        Assert.assertEquals(false, LevenshteinDistance.isValidLicense("CEAD"));
        Assert.assertEquals(false, LevenshteinDistance.isValidLicense("LEARNER PERMIT"));
        Assert.assertEquals(false, LevenshteinDistance.isValidLicense("CD FGHLMR LRNR PRMT"));
    }
}