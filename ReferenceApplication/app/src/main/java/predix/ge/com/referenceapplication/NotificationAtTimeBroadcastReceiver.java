package predix.ge.com.referenceapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.ge.predix.mobile.core.PredixNotificationAtTimeBroadcastReceiver;
import com.ge.predix.mobile.logging.PredixSDKLogger;

/**
 * Created by 212460388 on 2/3/17.
 */

public class NotificationAtTimeBroadcastReceiver extends PredixNotificationAtTimeBroadcastReceiver {
    @Override
    public Notification createNotification(Context context, Intent intent, String notificationPrompt, String notificationAdditional) {
        if (null == intent) {
            PredixSDKLogger.error(this, "intent param is missing!");
            return null;
        }
        if (null == context) {
            PredixSDKLogger.error(this, "context param is missing!");
            return null;
        }

        Bundle bundle = intent.getExtras();
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT /*| PendingIntent.FLAG_ONE_SHOT*/);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        String title = null;
        String contentText = null;
        if (null != bundle) {
            title = bundle.getString(notificationPrompt);
            contentText = bundle.getString(notificationAdditional);
        }
        if (null == title) {
            title = "Predix Mobile ";
        }
        if (null == contentText) {
            contentText = "";
        }
        Notification notification = builder.setContentTitle(title)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setTicker(title)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setContentIntent(pendingIntent).build();

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        return notification;
    }
}
