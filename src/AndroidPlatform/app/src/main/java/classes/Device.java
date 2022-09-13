package classes;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;

/**
 *
 * Type: Object Class
 * REFERENCE CLASS: USED TO DESCRIBE A DEVICE PHONE OBJECT <<-- NOT USED BY ANY ACTIVITY
 * Made for testing with camera devices. Some devices have different camera orientations
 *
 *
 **/
@SuppressWarnings({"unused"})
public class Device
{
    /**
     *
     * Class Variables
     *
     **/
    private String myDeviceModel = android.os.Build.MODEL;
    private static int orientation;

    /**
     *
     * Constructors
     *
     **/
    public Device(){}

    public Device(String m_model)
    {
        this.myDeviceModel = m_model;
    }

    /**
     *
     * Getters and Setters
     *
     **/
    public String getMyDeviceModel() { return myDeviceModel; }

    public void setMyDeviceModel(String myDeviceModel) { this.myDeviceModel = myDeviceModel; }

    public static int getOrientation() { return Device.orientation; }

    /**
     *
     * Type: Function
     * Depending on device model, rotate pictures taken by the camera
     *
     **/
    public static void load()
    {
        //check if emulator is running
        if (Build.BRAND.toLowerCase().contains("generic"))
        {
            Device.orientation = 0;
        } else {
            Device.orientation = 90;
        }
    }

    /**
     *
     * Type: Function
     * Depending on device model, rotate pictures taken by the camera
     *
     * Source: https://github.com/google/cameraview/issues/22
     *
     **/
    private static int fixOrientation(Bitmap bitmap)
    {
        if (bitmap.getWidth() > bitmap.getHeight())
        {
            if(android.os.Build.MODEL.equals("LG-US701"))
            {
                return 270;
            }
            else if(android.os.Build.MODEL.equals("PRA-LX1"))
            {
                return 270;
            }

        }
        return 0;
    }

    /**
     *
     * Type: Function
     * Flips the bits in an image
     *
     **/
    public static Bitmap flipImage(Bitmap bitmap)
    {

        Matrix matrix = new Matrix();
        int rotation = fixOrientation(bitmap);
        matrix.postRotate(rotation);
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
