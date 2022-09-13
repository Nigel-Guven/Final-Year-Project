package classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 *
 * Type: Object Class
 * Defines an item retrieved from the search bar.
 * If the item is over the levenshtein threshold, this object is created with the route ID and levenshtein score of relevancy for that object
 *
 **/
@SuppressWarnings({"unused"})
public class RelevantItem implements Parcelable
{
    /**
     *
     * Class Variables
     *
     **/
    private String documentId;
    private double relevance_score;
    private Route route;

    /**
     *
     * Constructors
     *
     **/
    public RelevantItem(){}

    public RelevantItem(String documentId, Route route , double relevance_score)
    {
        this.documentId = documentId;
        this.relevance_score = relevance_score;
        this.route = route;
    }

    /**
     *
     * Parcelable Functions
     *
     **/
    private RelevantItem(Parcel in)
    {
        documentId = in.readString();
        relevance_score = in.readDouble();
        route = in.readParcelable(Route.class.getClassLoader());
    }

    public static final Creator<RelevantItem> CREATOR = new Creator<RelevantItem>()
    {
        @Override
        public RelevantItem createFromParcel(Parcel in)
        {
            return new RelevantItem(in);
        }

        @Override
        public RelevantItem[] newArray(int size)
        {
            return new RelevantItem[size];
        }
    };

    /**
     *
     * Getters and Setters
     *
     **/
    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    private double getRelevance_score() { return relevance_score; }

    public void setRelevance_score(double relevance_score) { this.relevance_score = relevance_score; }

    public Route getRoute() { return route; }

    public void setRoute(Route route) { this.route = route; }

    /**
     *
     * Parcelable Functions
     *
     **/
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(documentId);
        parcel.writeDouble(relevance_score);
        parcel.writeParcelable(route, i);
    }

    /**
     *
     * Implemented Comparator for sorting Arraylist of Relevant Items by Date
     *
     **/
    public static class RouteComparatorDate implements Comparator<RelevantItem>
    {
        @Override
        public int compare(RelevantItem o1, RelevantItem o2)
        {
            return o1.getRoute().getDate_for_comp().compareTo(o2.getRoute().getDate_for_comp());
        }
    }

    /**
     *
     * Implemented Comparator for sorting Arraylist of Relevant Items by Relevance
     *
     **/
    public static class RouteComparatorRelevance implements Comparator<RelevantItem>
    {
        @Override
        public int compare(RelevantItem o1, RelevantItem o2)
        {
            return Double.compare(o1.getRelevance_score(),o2.getRelevance_score());
        }
    }
}
