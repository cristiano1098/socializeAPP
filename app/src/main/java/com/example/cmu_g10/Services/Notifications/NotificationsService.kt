package com.example.cmu_g10.Services.Notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.core.app.NotificationCompat
import com.example.cmu_g10.R
import com.example.cmu_g10.main.MainActivity

const val NOTIFICATION_CHANNEL_ID = "ch-1"
const val NOTIFICATION_CHANNEL_NAME = "Notifications"
const val NOTIFICATION_ID = 100
const val REQUEST_CODE = 200

class NotificationsService (
    private val context : Context
) {
    private val notificationManager : NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val myIntent = Intent(context, MainActivity::class.java)
    private val pendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        myIntent,
        PendingIntent.FLAG_MUTABLE
    )

    /**
     * Creates a notification
     *
     * @param title
     * @param text
     */
    fun showNotification(title: String, text: String) {
        val notification : Notification =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.socialize_logo)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}