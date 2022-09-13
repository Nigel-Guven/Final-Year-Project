package classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.activities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import static classes.Functions.capitalize;

/**
 *
 * Type: Object Class
 * Defines a Group object
 *
 **/
@SuppressWarnings("unused")
public class Group
{
    /**
     *
     * Class Variables
     *
     **/
    private String group_name;
    private int user_count;
    private int post_count;
    private String date_created;

    /**
     *
     * Constructors
     *
     **/
    public Group(){}

    public Group(String group,int users,int posts, String date_created)
    {
        this.group_name = group;
        this.user_count = users;
        this.post_count = posts;
        this.date_created = date_created;
    }

    /**
     *
     * Getters and Setters
     *
     **/
    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getUser_count() {
        return user_count;
    }

    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    public int getPost_count() {
        return post_count;
    }

    public void setPost_count(int post_count) {
        this.post_count = post_count;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    /**
     *
     * Create Group
     *
     **/
    public static void requestNewGroup(final Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(context), R.style.AlertDialog);
        builder.setTitle("Enter the Group Name");
        final EditText groupNameField = new EditText(context);
        groupNameField.setHint("e.g. Carpool Karaoke");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupName = groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(context, "Please provide a group name.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    createNewGroup(groupName, context);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    /**
     *
     * Add Group name to RTDB
     *
     **/
    private static void createNewGroup(final String groupName, final Context context)
    {
        String saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final HashMap<String,Object> groupMap = new HashMap<>();
        groupMap.put("group_name", capitalize(groupName));
        groupMap.put("post_count",0);
        groupMap.put("user_count",0);
        groupMap.put("messages", "");
        groupMap.put("date_created", saveCurrentDate);

        databaseReference.child("Groups").child(groupName).setValue(groupMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(context, "The group: " + groupName + " has been created successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(context, "Failed to create a group." + e.toString(), Toast.LENGTH_SHORT).show();
                Log.d("TAG", e.toString());
            }
        });
    }
}
