package classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.example.activities.R;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

public class Functions
{
    /**
     *
     * Type: Function
     * Downloads image from Firebase storage reference
     *
     **/
    public static void downloadImage(StorageReference profile_imagesRef, final CircleImageView userImage)
    {
        final long ONE_MEGABYTE = 1024 * 1024;
        profile_imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] bytes)
            {
                Bitmap profile = ByteArrayToBitmap(bytes);
                userImage.setImageBitmap(profile);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Log.d("TAG", "CONTENTS" + exception.toString());
            }
        });
    }

    /**
     *
     * Type: Function
     * Downloads image from Firebase storage reference
     *
     **/
    public static void downloadImage(StorageReference profile_imagesRef, final ImageView profile_image)
    {
        final long ONE_MEGABYTE = 1024 * 1024;
        profile_imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] bytes)
            {
                Bitmap profile = ByteArrayToBitmap(bytes);
                profile_image.setImageBitmap(profile);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Log.d("TAG", "CONTENTS" + exception.toString());
            }
        });
    }

    /**
     *
     * Type: Function
     * Called by downloadImage(), converts a byte array image into a Bitmap which can be displayed by the UI.
     *
     **/
    private static Bitmap ByteArrayToBitmap(byte[] byteArray)
    {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        return BitmapFactory.decodeStream(arrayInputStream);
    }

    /**
     *
     * Type: Function
     * Get Unique Users array and from that, the count.
     *
     **/
    public static Integer getUniqueUsers(ObservableSnapshotArray<Route> list)
    {
        Integer n = list.size();
        HashSet<String> hs = new HashSet<>();
        for(Integer i = 0;i < n;i++)
        {
            hs.add(list.get(i).getUser_identifier());
        }
        return hs.size();
    }

    /**
     *
     * Type: Function
     * Check if string contains a numeric value
     *
     **/
    public static boolean containsDigit(String str)
    {
        boolean containsDigit = false;

        if (str != null && !str.isEmpty()) {
            for (char c : str.toCharArray()) {
                if (containsDigit = Character.isDigit(c)) {
                    break;
                }
            }
        }

        return containsDigit;
    }

    /**
     *
     * Type: Function
     * This function takes a long address as a parameter and returns a shorter, more readable and user-friendly address to display on a dashboard post.
     * The reason behind this was the limit of space on a phone screen
     *
     **/
    public static String getAddress(String long_address)
    {
        String finalResult = "";
        String [] tmp = long_address.split(",");
        if(tmp.length<=3)
        {
            finalResult += tmp[0] + "," + tmp[1];
        }
        else if(tmp.length == 4)
        {
            finalResult += tmp[1] + "," + tmp[tmp.length-1];
        }
        else {
            finalResult += tmp[1] + "," + tmp[3];
        }
        return finalResult;
    }

    /**
     *
     * Type: Function
     * Capitalize each word in a string sentence
     *
     **/
    public static String format_home_address(String str)
    {
        StringBuilder str_formatted = new StringBuilder();
        String [] str_array = str.split(" ");
        for(String i : str_array)
        {
            i = capitalize(i);
            str_formatted.append(" ").append(i);
        }
        return str_formatted.toString();
    }

    /**
     *
     * Type: Function
     * Capitalize word
     *
     **/
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     *
     * Type: Function
     * Get drawable image based on whether user is a driver or passenger. Displays on recyclerview, a drawable item.
     *
     **/
    public static int getImage(boolean isDriverFlag)
    {
        if(isDriverFlag)
        {
            return R.drawable.driver_post;
        }
        else
            return R.drawable.passenger_post;
    }

    /**
     *
     * Type: Function
     * Check for valid email address input
     *
     **/
    public static boolean isValidEmail(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
