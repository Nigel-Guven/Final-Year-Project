package adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import classes.Message;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static classes.Functions.downloadImage;

/**
 *
 * Type: Adapter
 * The message adapter is used by PairChatActivity
 * Handles correct display and functionality of messages in the activity RecyclerView
 *
 **/
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    /**
     *
     * Class Variables
     *
     **/
    private List<Message> userMessageList;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference profile_imagesRef;

    /**
     *
     * Type: Constructor
     * Takes in a list of messages between two users as a constructor (Taken from RTDB)
     *
     **/
    public MessageAdapter (List<Message> userMessageList)
    {
        this.userMessageList = userMessageList;
    }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Defines a holder for a message object
     *
     **/
    static class MessageViewHolder extends RecyclerView.ViewHolder
    {
        /**
         *
         * Class Variables
         *
         **/
        TextView senderMessageText, receiverMessageText;
        CircleImageView receiverProfileImage;
        ImageView messageSenderMedia, messageReceiverMedia;

        /**
         *
         * Type: RecyclerView.Adapter Function
         * Defines what each item in the XML layout will hold. Each itemView has a set of ID's defined which are matched to variable View items.
         *
         **/
        MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_messages);
            receiverMessageText = itemView.findViewById(R.id.receiver_messages);
            receiverProfileImage = itemView.findViewById(R.id.message_image);
            messageSenderMedia = itemView.findViewById(R.id.message_sender_media);
            messageReceiverMedia = itemView.findViewById(R.id.message_receiver_media);
        }
    }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Takes in a ViewGroup which is the XML layout item and UNUSED int viewtype for optional different display items in recyclerview object
     *
     **/
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent, false);
        mFirebaseAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    /**
     *
     * Type: RecyclerView.Adapter Function
     * Takes in a holder item and a position for that item
     * Depending on if the item is the current user or not, display by that XAML layout
     * Enable on long click which deletes the message, document or image
     *
     **/
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
    {
        String messageSenderID = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
        Message message = userMessageList.get(position);
        String fromUserID = message.getAuthor();
        String fromMessageType = message.getMessage_type();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("profile_photo_url"))
                {
                    String receiverImageFileName = Objects.requireNonNull(dataSnapshot.child("profile_photo_url").getValue()).toString();
                    profile_imagesRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + receiverImageFileName);
                    downloadImage(profile_imagesRef, holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d("TAG", databaseError.toString());
            }
        });

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderMedia.setVisibility(View.GONE);
        holder.messageReceiverMedia.setVisibility(View.GONE);


        //noinspection IfCanBeSwitch
        if(fromMessageType.equals("text"))
        {
            if(fromUserID.equals(messageSenderID))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.messages_layout_sender);

                String message_text = message.getMessage_contents();
                String message_timestamp = "\n" + message.getTime();

                SpannableString spannable_message = new SpannableString(message_text);
                SpannableString spannable_timestamp = new SpannableString(message_timestamp);

                spannable_message.setSpan(new AbsoluteSizeSpan(48),0,message_text.length(), SPAN_INCLUSIVE_INCLUSIVE);
                spannable_timestamp.setSpan(new StyleSpan(Typeface.ITALIC), 0, message_timestamp.length(), SPAN_INCLUSIVE_INCLUSIVE);
                spannable_timestamp.setSpan(new AbsoluteSizeSpan(36),0,message_timestamp.length(), SPAN_INCLUSIVE_INCLUSIVE);

                holder.senderMessageText.setText(spannable_message);
                holder.senderMessageText.append(spannable_timestamp);
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setBackgroundResource(R.drawable.messages_layout_receiver);

                String message_text = message.getMessage_contents();
                String message_timestamp = "\n" + message.getTime();

                SpannableString spannable_message = new SpannableString(message_text);
                SpannableString spannable_timestamp = new SpannableString(message_timestamp);

                spannable_message.setSpan(new AbsoluteSizeSpan(48),0,message_text.length(), SPAN_INCLUSIVE_INCLUSIVE);
                spannable_timestamp.setSpan(new StyleSpan(Typeface.ITALIC), 0, message_timestamp.length(), SPAN_INCLUSIVE_INCLUSIVE);
                spannable_timestamp.setSpan(new AbsoluteSizeSpan(36),0, message_timestamp.length(), SPAN_INCLUSIVE_INCLUSIVE);

                holder.receiverMessageText.setText(spannable_message);
                holder.receiverMessageText.append(spannable_timestamp);
            }
        }
        else if(fromMessageType.equals("image"))
        {
            if(fromUserID.equals(messageSenderID))
            {
                holder.messageSenderMedia.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage_contents()).into(holder.messageSenderMedia);

            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverMedia.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage_contents()).into(holder.messageReceiverMedia);

            }
        }
        else if(fromMessageType.equals("document"))
        {
            if(fromUserID.equals(messageSenderID))
            {
                holder.messageSenderMedia.setVisibility(View.VISIBLE);
                holder.messageSenderMedia.setBackgroundResource(R.drawable.file_resource);

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessageList.get(position).getMessage_contents()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverMedia.setVisibility(View.VISIBLE);
                holder.messageReceiverMedia.setBackgroundResource(R.drawable.file_resource);

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessageList.get(position).getMessage_contents()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
        if(fromUserID.equals(messageSenderID))
        {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    if(userMessageList.get(position).getMessage_type().equals("document"))
                    {
                        CharSequence [] options = new CharSequence[] { "Download Document","Delete for Me", "Delete for Everyone", "Cancel"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i == 0)
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessageList.get(position).getMessage_contents()));
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if(i == 1)
                                {
                                    deleteSentMessage(position,holder);
                                }
                                if(i == 2)
                                {
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    }
                    if(userMessageList.get(position).getMessage_type().equals("image"))
                    {
                        CharSequence [] options = new CharSequence[] { "Download Image","Delete for Me", "Delete for Everyone", "Cancel"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i == 0)
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessageList.get(position).getMessage_contents()));
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                if(i == 1)
                                {
                                    deleteSentMessage(position,holder);
                                }
                                if(i == 2)
                                {
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    }
                    if(userMessageList.get(position).getMessage_type().equals("text"))
                    {
                        CharSequence [] options = new CharSequence[] { "Delete for Me", "Delete for Everyone", "Cancel"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(i == 0)
                                {
                                    deleteSentMessage(position,holder);
                                }
                                if(i == 1)
                                {
                                    deleteMessageForEveryone(position, holder);
                                }
                            }
                        });
                        builder.show();
                    }
                    return true;
                }
            });

        }

    }

    /**
     *
     * Type: Function
     * Retrieve the size of the message list between two users
     *
     **/
    @Override
    public int getItemCount()
    {
        return userMessageList.size();
    }

    /**
     *
     * Type: Function
     * Called from OnBindViewHolder(), delete a message for the current user
     *
     **/
    private void deleteSentMessage(final int position, final MessageViewHolder holder)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessageList.get(position).getAuthor())
                .child(userMessageList.get(position).getMessage_receiver())
                .child(userMessageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            holder.itemView.setVisibility(View.GONE);
                            Toast.makeText(holder.itemView.getContext(), "Message deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }

    /**
     *
     * Type: Function
     * Called from OnBindViewHolder(), delete a message for both users in the chat
     *
     **/
    private void deleteMessageForEveryone(final int position, final MessageViewHolder holder)
    {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessageList.get(position).getMessage_receiver())
                .child(userMessageList.get(position).getAuthor())
                .child(userMessageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            rootRef.child("Messages")
                                    .child(userMessageList.get(position).getAuthor())
                                    .child(userMessageList.get(position).getMessage_receiver())
                                    .child(userMessageList.get(position).getMessageID())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                holder.itemView.setVisibility(View.GONE);
                                                Toast.makeText(holder.itemView.getContext(), "Message deleted successfully for everyone", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                                        }
                                    });
                        }
                        else
                            Log.d("TAG", Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }
}
