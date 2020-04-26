package com.example.todolist_sa.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.todolist_sa.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String PRIMARY_CHANNEL_ID = "primary_channel_id";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        // Récupère l'ID et le message depuis l'Intent
        int notifID = intent.getIntExtra("notifID", 0);
        String message = intent.getStringExtra("message");

        Intent mainIntent = new Intent(context, AddToDoActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, notifID, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager myNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Prépare la notif
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Une tâche se termine aujourd'hui !")
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Notification
        myNotifManager.notify(notifID, builder.build());
    }
}
