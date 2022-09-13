package adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.activities.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import classes.Route;

import static classes.Functions.getAddress;
import static classes.Functions.getImage;

/**
 *
 * Type: Adapter
 * This adapter class provides a connection between the Firestore Routes collection and the DashboardActivity RecyclerView.
 * Routes are updated simultaneously with this adapter
 *
 **/
public class DashboardPostAdapter extends FirestoreRecyclerAdapter<Route, DashboardPostAdapter.PostViewHolder>
{
    /**
     *
     * Class Variables
     *
     **/
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private OnItemClickListener listener;
    private DatabaseReference databaseRouteReference = FirebaseDatabase.getInstance().getReference().child("Routes");

    /**
     *
     * Type:Class Constructor
     * This class takes in a FirestoreRecycler object which derives from an open source Firebase UI library.
     * The FirestoreRecycler library handles all data retrieval in its own library which saves time having to implement custom functions
     *
     **/
    public DashboardPostAdapter(@NonNull FirestoreRecyclerOptions<Route> options) {
        super(options);
    }

    /**
     *
     * Type:RecyclerView.Adapter Function
     * Called by FirebaseRecyclerAdapter to display the data at a specified position
     * Takes in Route model class, PostViewHolder as layout descriptor and integer position of object
     *
     **/
    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull final Route model)
    {
        final String DOCPATH = model.getUser_identifier() + "_" + model.getDate().replaceAll("/", "") + model.getRoute_distance().replaceAll(" ", "").replace(".", "");
        if(compareDates(model.getDate_for_comp()))
        {
            final String userID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
            holder.myName.setText(model.getFirst_name()+" " +model.getSurname());
            holder.myDate.setText(model.getDate());
            holder.myTime.setText(model.getTime());
            holder.imageType.setImageResource(getImage(model.getIs_offer()));
            holder.myStart.setText(getAddress(model.getRoute_start()));
            holder.myEnd.setText(getAddress(model.getRoute_end()));
            if(model.getUser_identifier().equals(userID))
            {
                holder.imageCancel.setVisibility(View.VISIBLE);
            }

            holder.imageCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    deleteItem(position, DOCPATH);
                }
            });
        }
        else
        {
            deleteItem(position, DOCPATH);
        }
    }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Takes in a ViewGroup which is the XML layout item and UNUSED int viewtype for optional different display items in recyclerview object
     * Defines what XML layout will define each item in the RecyclerView
     *
     **/
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_post, parent, false);
        return new PostViewHolder(view);
    }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Defines what each item in the XML layout will hold. Each itemView has a set of ID's defined which are matched to variable View items.
     *
     **/
    class PostViewHolder extends RecyclerView.ViewHolder
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
         * Implements onClickable interface object
         *
         **/
        PostViewHolder(@NonNull View itemView)
        {
            super(itemView);
            myName = itemView.findViewById(R.id.textView53);
            myDate = itemView.findViewById(R.id.textView54);
            myTime = itemView.findViewById(R.id.textView79);
            myStart = itemView.findViewById(R.id.textView55);
            myEnd = itemView.findViewById(R.id.textView56);
            imageType = itemView.findViewById(R.id.imageView9);
            imageCancel = itemView.findViewById(R.id.imageView12);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null)
                    {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }


    }

    /**
     *
     * Type: Listener
     * OnClickListener retrieves snapshot of an item which has been clicked
     *
     **/
    public interface OnItemClickListener
    {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    /**
     *
     * Type: ListenerOnSet
     * Defines the listener to be enabled for OnItemClick
     *
     **/
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    /**
     *
     * Type: Function
     * Deletes Item at position. Activated through onClick of 'X' image on a user's owned post. Deletes item on Firestore and RTDB
     *
     **/
    private void deleteItem(int position, String routeID)
    {
        getSnapshots().getSnapshot(position).getReference().delete();
        databaseRouteReference.child(routeID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Log.d("DELETED ROUTE", task.toString());
            }
        });
    }

    /**
     *
     * Type: Function
     * Takes in an item date as a parameter and compares it to the current time. If the item date is less than the current date then the item is deleted
     * Automatic Garbage collection of out-of-date routes
     *
     **/
    private boolean compareDates(int date)
    {
        Calendar c = Calendar.getInstance();
        c.get(Calendar.YEAR);
        c.get(Calendar.MONTH);
        c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        String compDate = ""  + c.get(Calendar.YEAR);

        if(c.get(Calendar.MONTH) < 10)
        {
            compDate += "0" + month;
        }
        else {
            compDate += month;
        }
        if(c.get(Calendar.DAY_OF_MONTH) < 10)
        {
            compDate += "0" + c.get(Calendar.DAY_OF_MONTH);
        }
        else {
            compDate += c.get(Calendar.DAY_OF_MONTH);
        }

        int compDateInt = Integer.parseInt(compDate);
        Log.d("TAG", "" + compDateInt);
        Log.d("TAG","" + date);
        if(date>=compDateInt)
        {
            Log.d("TAG",compDate + "");
            return true;

        }
        else
        {
            Log.d("TAG",compDate + "");
            return false;
        }
    }

}