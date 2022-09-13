package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.activities.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import classes.RelevantItem;

import static classes.Functions.getAddress;
import static classes.Functions.getImage;

/**
 *
 * Type: Adapter
 * This adapter class places relevant queries based on the Dashboard Search Results into a recyclerview in SearchReturnActivity
 *
 **/
public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ResultsViewHolder>
{
    /**
     *
     * Class Variables
     *
     **/
    private Context context;
    private ArrayList<RelevantItem> routes;
    private OnPostClickListener mOnPostClickListener;

    /**
     *
     * Type:Class Constructor
     * This class takes in a list of routes which are deemed by their relevance to a search query
     *
     **/
    public SearchResultsAdapter(Context context, ArrayList<RelevantItem> routes, OnPostClickListener onPostClickListener)
    {
        this.context = context;
        this.routes = routes;
        this.mOnPostClickListener = onPostClickListener;
    }

    /**
     *
     * Getters and Setters for Context
     *
     **/
    public Context getContext() { return context; }

    public void setContext(Context context) { this.context = context; }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Takes in a ViewGroup which is the XML layout item and UNUSED int viewtype for optional different display items in recyclerview object
     * Defines what XML layout will define each item in the RecyclerView
     *
     **/
    @NonNull
    @Override
    public SearchResultsAdapter.ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_post, parent, false);
        return new ResultsViewHolder(view, mOnPostClickListener);
    }

    /**
     *
     * Type:RecyclerView.Adapter Function
     * Recycclerview.setAdapter() calls this function to display the data at a specified position
     * Takes in Route model class, ResultsViewHolder as layout descriptor and integer position of object
     *
     **/
    @Override
    public void onBindViewHolder(@NonNull SearchResultsAdapter.ResultsViewHolder holder, final int position)
    {
        String name = routes.get(position).getRoute().getFirst_name() + " " + routes.get(position).getRoute().getSurname();
        holder.myName.setText(name);
        holder.myDate.setText(routes.get(position).getRoute().getDate());
        holder.myTime.setText(routes.get(position).getRoute().getTime());
        holder.myStart.setText(getAddress(routes.get(position).getRoute().getRoute_start()));
        holder.myEnd.setText(getAddress(routes.get(position).getRoute().getRoute_end()));
        holder.imageType.setImageResource(getImage(routes.get(position).getRoute().getIs_offer()));
        holder.imageCancel.setVisibility(View.INVISIBLE);

    }

    /**
     *
     * Type: Function
     * Retrieve the size of the routes arraylist returned from a search query
     *
     **/
    @Override
    public int getItemCount()
    {
        return routes.size();
    }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Defines what each item in the XML layout will hold. Each itemView has a set of ID's defined which are matched to variable View items.
     *
     **/
    public static class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        /**
         *
         * Class Variables
         *
         **/
        TextView myName, myDate, myStart, myEnd, myTime;
        ImageView imageType, imageCancel;
        OnPostClickListener onPostClickListener;

        /**
         *
         * Type: RecyclerView.Adapter Function
         * Instantiates the XML data as java variables
         * Implements onClickable interface object
         *
         **/
        ResultsViewHolder(@NonNull View itemView, OnPostClickListener listener)
        {
            super(itemView);

            myName = itemView.findViewById(R.id.textView53);
            myDate = itemView.findViewById(R.id.textView54);
            myTime = itemView.findViewById(R.id.textView79);
            myStart = itemView.findViewById(R.id.textView55);
            myEnd = itemView.findViewById(R.id.textView56);
            imageType = itemView.findViewById(R.id.imageView9);
            imageCancel = itemView.findViewById(R.id.imageView12);
            this.onPostClickListener = listener;

            itemView.setOnClickListener(this);
        }

        /**
         *
         * Type: OnClickListener
         * OnClickListener retrieves index position of an item which has been clicked
         *
         **/
        @Override
        public void onClick(View view)
        {
            onPostClickListener.onPostClick(getAdapterPosition());
        }
    }

    /**
     *
     * Type: Interface
     * Interface describes an OnClickListener
     *
     */
    public interface OnPostClickListener
    {
        void onPostClick(int position);
    }
}
