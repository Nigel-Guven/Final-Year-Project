package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import classes.Route;

import static classes.Functions.getAddress;

/**
 *
 * Type: Adapter
 * The message adapter is used by UserStatsActivity
 * Handles correct display and functionality of user owned routes in that activity RecyclerView
 *
 **/
@SuppressWarnings("unused")
public class UserTripsAdapter extends RecyclerView.Adapter<UserTripsAdapter.TripViewHolder>
{
    /**
     *
     * Class Variables
     *
     **/
    @SuppressWarnings("FieldCanBeLocal")
    private Context context;
    private ArrayList<Route> routes;
    private DatabaseReference databaseRouteReference = FirebaseDatabase.getInstance().getReference().child("Routes");
    private CollectionReference routeCollectionReference = FirebaseFirestore.getInstance().collection("routes");

    /**
     *
     * Type: Constructor
     * Takes in a list of routes owned by users as a constructor given a certain context i.e. activity
     *
     **/
    public UserTripsAdapter(Context context, ArrayList<Route> routes)
    {
        this.context = context;
        this.routes = routes;
    }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Takes in a ViewGroup which is the XML layout item and UNUSED int viewtype for optional different display items in recyclerview object
     *
     **/
    @NonNull
    @Override
    public UserTripsAdapter.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_post, parent, false);
        return new TripViewHolder(view);
    }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Takes in a holder item and a position for that item
     * Depending on if the item is the current user or not, display by that XAML layout
     *
     **/
    @Override
    public void onBindViewHolder(@NonNull UserTripsAdapter.TripViewHolder holder, final int position)
    {
        final String DOCPATH =
                routes.get(position).getUser_identifier() + "_"
                + routes.get(position).getDate().replaceAll("/", "")
                + routes.get(position).getRoute_distance().replaceAll(" ", "").replace(".", "");
        String name = routes.get(position).getFirst_name() + " " + routes.get(position).getSurname();
        holder.myName.setText(name);
        holder.myDate.setText(routes.get(position).getDate());
        holder.myTime.setText(routes.get(position).getTime());
        holder.myStart.setText(getAddress(routes.get(position).getRoute_start()));
        holder.myEnd.setText(getAddress(routes.get(position).getRoute_end()));
        holder.imageCancel.setVisibility(View.VISIBLE);
        holder.imageType.setVisibility(View.INVISIBLE);

        holder.imageCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                deleteItem(position, DOCPATH);
            }
        });
    }

    /**
     *
     * Type: Function
     * Retrieve the amount of recyclerview items
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
     * Defines a holder for a route object
     *
     **/
    static class TripViewHolder extends RecyclerView.ViewHolder
    {
        /**
         *
         * Class Variables
         *
         **/
        TextView myName, myDate, myStart, myEnd, myTime;
        ImageView imageType, imageCancel;

        /**
         *
         * Type: RecyclerView.Adapter Function
         * Instantiates the XML data as java variables
         *
         **/
        TripViewHolder(@NonNull View itemView)
        {
            super(itemView);

            myName = itemView.findViewById(R.id.textView53);
            myDate = itemView.findViewById(R.id.textView54);
            myTime = itemView.findViewById(R.id.textView79);
            myStart = itemView.findViewById(R.id.textView55);
            myEnd = itemView.findViewById(R.id.textView56);
            imageType = itemView.findViewById(R.id.imageView9);
            imageCancel = itemView.findViewById(R.id.imageView12);
        }
    }

    /**
     *
     * Type: Function
     * Deletes Item at position. Activated through onClick of 'X' image on a user's owned post. Deletes item on Firestore and RTDB
     *
     **/
    private void deleteItem(int position, String routeID)
    {
        routes.remove(position);

        routeCollectionReference.document(routeID).delete();
        databaseRouteReference.child(routeID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Log.d("DELETED ROUTE", task.toString());
            }
        });
        notifyItemRemoved(position);
    }
}
