package classes;

import org.junit.Assert;
import org.junit.Test;

public class TripListItemTest
{
    @Test
    public void formatTime()
    {
        Assert.assertEquals(TripListItem.formatTime("23 minutes."), 23);
        Assert.assertEquals(TripListItem.formatTime( "3 hours, 50 minutes."), 230);
        Assert.assertEquals(TripListItem.formatTime( "3 minutes."), 3);
        Assert.assertEquals(TripListItem.formatTime( "50 minutes."), 50);
        Assert.assertEquals(TripListItem.formatTime( "1 minute."), 1);
        Assert.assertEquals(TripListItem.formatTime( "0 minutes."), 0);
    }

    @Test
    public void timeToString()
    {
        Assert.assertEquals(TripListItem.timeToString(23), "23 minutes.");
        Assert.assertEquals(TripListItem.timeToString(32), "32 minutes.");

        Assert.assertEquals(TripListItem.timeToString(230), "3 hours, 50 minutes.");
        Assert.assertEquals(TripListItem.timeToString(60), "1 hour.");
        Assert.assertEquals(TripListItem.timeToString(61), "1 hour, 1 minute.");
        Assert.assertEquals(TripListItem.timeToString(1), "1 minute.");
        Assert.assertEquals(TripListItem.timeToString(0), "0 minutes.");
    }

    @Test
    public void distanceToString()
    {
        Assert.assertEquals(TripListItem.distanceToString(23.0), "23 km's.");
        Assert.assertEquals(TripListItem.distanceToString(27.0), "27 km's.");
        Assert.assertEquals(TripListItem.distanceToString(23.4), "23.4 km's.");
        Assert.assertEquals(TripListItem.distanceToString(0.0), "0 km's.");

        Assert.assertNotEquals(TripListItem.distanceToString(23.0), "2 km's.");
        Assert.assertNotEquals(TripListItem.distanceToString(23.0), "23.0 km's.");
        Assert.assertNotEquals(TripListItem.distanceToString(0.0), "0.0 km's.");
        Assert.assertNotEquals(TripListItem.distanceToString(0.0), "00 km's.");
    }

    @Test
    public void formatDistance()
    {
        Assert.assertEquals(TripListItem.formatDistance("23 km's."), 23.0,0.0002);
        Assert.assertEquals(TripListItem.formatDistance("28.7 km's."), 28.7, 0.0002);
        Assert.assertEquals(TripListItem.formatDistance("21 km's."), 21.0,0.0002);
        Assert.assertEquals(TripListItem.formatDistance("3 km's."), 3.0,0.0002);
        Assert.assertEquals(TripListItem.formatDistance("0 km's."), 0.0,0.0002);
    }
}