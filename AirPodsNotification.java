package iciclez.airpods;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AirPodsNotification extends Thread {

    private NotificationManager notificationManager;

    public AirPodsNotification(NotificationManager notificationManager)
    {
        this.notificationManager = notificationManager;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("AirPods", "AirPods", NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void run()
    {

    }
}
