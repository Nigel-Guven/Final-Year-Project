package classes;

//https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.IOException;

/**
 *
 * Type: Object Class
 * Defines a Levenchtein Distance double. Used to examine relevancy between two strings
 *
 **/
@SuppressWarnings("unused")
public class LevenshteinDistance
{
    /**
     *
     * Class Variables
     *
     **/
    private double relevance_score;

    /**
     *
     * Constructors
     *
     **/
    public LevenshteinDistance (){}

    public LevenshteinDistance(double relevance_score) { this.relevance_score = relevance_score; }

    /**
     *
     * Getters and Setters
     *
     **/
    public double getRelevance_score() { return relevance_score; }

    public void setRelevance_score(double relevance_score) { this.relevance_score = relevance_score; }

    /**
     *
     * Type: Levenshtein algorithm
     * Return integer number of edits required to change a string into another i.e distance of string
     *
     **/
    private static int computeDistance(String s1, String s2)
    {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++)
        {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++)
            {
                if (i == 0)
                {
                    costs[j] = j;
                }
                else
                {
                    if (j > 0)
                    {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                        {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
            {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    /**
     *
     * Type: Levenshtein algorithm
     * Place a bias on the longer string and return the levenshtein distance
     *
     **/
    public static double returnDistance(String s1, String s2)
    {
        double relevance_score;
        int editDistance;
        if (s1.length() < s2.length())
        {
            String swap = s1;
            s1 = s2;
            s2 = swap;
        }
        int bigLen = s1.length();
        editDistance = computeDistance(s1, s2);
        if (bigLen == 0) {
            relevance_score = 1.0;
        } else {
            relevance_score = (bigLen - editDistance) / (double) bigLen;
        }
        return relevance_score;
    }

    /**
     *
     * Type: LCS algorithm
     * Returns the longest common substring in length. Used to analyse a driver license against ML KIT OCR text in DriverLicenseActivity
     * https://stackoverflow.com/questions/17150311/java-implementation-for-longest-common-substring-of-n-strings
     *
     **/
    public static int longestSubstr(String first, String second)
    {
        first = first.toLowerCase();
        second = second.toLowerCase();
        if( first.length() == 0 || second.length() == 0)
        {
            return 0;
        }

        int maxLen = 0;
        int fl = first.length();
        int sl = second.length();
        int[][] table = new int[fl][sl];

        for (int i = 0; i < fl; i++)
        {
            for (int j = 0; j < sl; j++)
            {
                if (first.charAt(i) == second.charAt(j))
                {
                    if (i == 0 || j == 0)
                    {
                        table[i][j] = 1;
                    }
                    else
                    {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    }
                    if (table[i][j] > maxLen)
                    {
                        maxLen = table[i][j];
                    }
                }
            }
        }
        return maxLen;
    }

    public static Boolean scrapeAPI(String car_registration) throws IOException
    {

        String url = "https://www.motorcheck.ie/free-car-check/?vrm=" + car_registration;
        Document doc = Jsoup.connect(url).get();
        String html_soup = doc.select("div[class=col-md-6 align-items-center]").toString();
        html_soup = Jsoup.clean(html_soup, Whitelist.none());
        Log.d("TAG", "+++ DEBUG +++ " + html_soup);
        if(html_soup.equals(""))
        {
            return false;
        }
        else
            return true;
    }

    public static Boolean isValidLicense(String licenceReturned)
    {
        String validLicence = "CEADUNAS TIOMANA DRIVING LICENCE";
        String invalidLicence = "CEAD FOGHLAMORA LEARNER PERMIT";
        int confidenceValid, confidenceInvalid = 0;
        confidenceValid = longestSubstr(licenceReturned, validLicence);
        confidenceInvalid = longestSubstr(licenceReturned, invalidLicence);
        if(confidenceValid >= confidenceInvalid && confidenceValid > 10)
        {
            return true;
        }
        else
            return false;

    }


}
