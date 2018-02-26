package com.giaothuy.ebookone.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.giaothuy.ebookone.R;
import com.giaothuy.ebookone.activity.MainActivity;
import com.giaothuy.ebookone.config.Constant;

/**
 * Created by 1 on 2/26/2018.
 */

public class AlarmService extends IntentService {


    private NotificationManager notificationManager;

    public AlarmService() {
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, "call", Toast.LENGTH_SHORT).show();
        sendNotification(intent.getStringExtra(Constant.TITLE));
    }

    private void sendNotification(String messsage) {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle(messsage).setSmallIcon(R.drawable.ic_icon_app)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messsage))
                .setContentText(messsage);
        alamNotificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(1, alamNotificationBuilder.build());
    }
}
