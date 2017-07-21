package predix.ge.com.referenceapplication;

import android.app.Notification;
import android.content.Context;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by 212460388 on 2/6/17.
 */
public class NotificationAtTimeBroadcastReceiverTest {

    @Test
    public void contextIsMandatoryForCreatingANotification() throws Exception {
        // Given
        NotificationAtTimeBroadcastReceiver broadcastReceiver = new NotificationAtTimeBroadcastReceiver();

        // When
        Notification notification = broadcastReceiver.createNotification(null, null, null, null);

        // Then
        Assert.assertNull("notification should be null when context not received", notification);
    }


    @Test
    public void intentIsMandatoryForCreatingANotification() throws Exception {
        // Given
        NotificationAtTimeBroadcastReceiver broadcastReceiver = new NotificationAtTimeBroadcastReceiver();
        Context context = Mockito.mock(Context.class);

        // When
        Notification notification = broadcastReceiver.createNotification(context, null, null, null);

        // Then
        Assert.assertNull("notification should be null when context not received", notification);
    }


}