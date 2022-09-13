package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import classes.Group;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.activities.GroupActivity;
import com.example.activities.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 *
 * Reference: https://www.youtube.com/channel/UCIHBIPape0dWHKANkivrcJw/about
 * Coding Cafe Whatsapp tutorials aided with Messaging System
 *
 * GroupChatFragment holds list of public group forums. Displays list of user and post counts for each group.
 *
 */
public class GroupChatFragment extends Fragment
{
    private RecyclerView groupListView;
    private DatabaseReference groupRef;

    public GroupChatFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View groupFragmentView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        groupListView = groupFragmentView.findViewById(R.id.list_view_groups);
        groupListView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton buttonCreateGroup = groupFragmentView.findViewById(R.id.floatingActionButton1);
        buttonCreateGroup.setVisibility(View.VISIBLE);


        buttonCreateGroup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Group.requestNewGroup(getContext());
            }
        });

        return groupFragmentView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Group> options = new FirebaseRecyclerOptions.Builder<Group>().setQuery(groupRef.orderByChild("date"), Group.class).build();
        FirebaseRecyclerAdapter<Group, GroupViewHolder> adapter = new FirebaseRecyclerAdapter<Group, GroupViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final GroupViewHolder holder, final int position, @NonNull final Group model)
            {
                final String groupName = getRef(position).getKey();

                assert groupName != null;
                groupRef.child(groupName).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            String mGroupName = (String) dataSnapshot.child("group_name").getValue();
                            String mGroupPosts = Objects.requireNonNull(dataSnapshot.child("post_count").getValue()).toString();
                            String mGroupDate = Objects.requireNonNull(dataSnapshot.child("date_created").getValue()).toString();
                            String mGroupUsers = Objects.requireNonNull(dataSnapshot.child("user_count").getValue()).toString();

                            holder.groupName.setText(mGroupName);
                            holder.groupNumPosts.setText(mGroupPosts);
                            holder.groupDateCreated.setText(mGroupDate);
                            holder.groupNumUsers.setText(mGroupUsers);
                            if(position % 3 == 0)
                            {
                                holder.itemView.setBackgroundResource(R.color.green);
                            }
                            else if(position % 3 == 1)
                            {
                                holder.itemView.setBackgroundResource(R.color.yellow);
                            }
                            else if(position % 3 == 2)
                            {
                                holder.itemView.setBackgroundResource(R.color.orange);
                            }
                            else
                            {
                                holder.itemView.setBackgroundResource(R.color.reddie);
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    Intent intentToGroup = new Intent(getContext(), GroupActivity.class);
                                    intentToGroup.putExtra("GROUPNAME", groupName);
                                    startActivity(intentToGroup);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Log.d("TAG", databaseError.toString());
                    }
                });
            }

            @NonNull
            @Override
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_groupview, parent,false);
                return new GroupViewHolder(view);
            }
        };
        groupListView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder
    {
        TextView groupName, groupNumPosts, groupDateCreated, groupNumUsers;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.textView108);
            groupNumPosts = itemView.findViewById(R.id.textView113);
            groupDateCreated = itemView.findViewById(R.id.textView115);
            groupNumUsers = itemView.findViewById(R.id.textView112);
        }
    }
}
