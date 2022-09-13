package classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 *
 * Type: Object Class
 * Notifications are assigned to a channel. Since API 26, this is necessary.
 * Application Channels define a specific to send Notification on
 *
 **/
public class ApplicationChannels extends android.app.Application
{
    /**
     *
     * Class Variables
     *
     **/
    public static final String CHANNEL_1_ID = "channel1";

    /**
     *
     * Type: onCreate
     * Method assigned to an onCreate method
     *
     **/
    @Override
    public void onCreate()
    {
        super.onCreate();

        createNotificationChannels();
    }

    /**
     *
     * Type: Function
     * Assign a notification channel to a Notification Manager which handles sending notificaiton between two devices
     *
     **/
    private void createNotificationChannels()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel1);
        }
    }

}
