package com.example.importantdatesreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.importantdatesreminder.data.Event

object NotificationUtils {

    private const val CHANNEL_ID = "important_dates_channel"

    fun createNotificationChannel(context: Context) {
        // เฉพาะ Android 8.0 (API level 26) ขึ้นไปที่ต้องสร้าง Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "วันสำคัญ"
            val descriptionText = "แจ้งเตือนวันสำคัญ"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, event: Event) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = context.getString(R.string.notification_title, event.title)
        val text = context.getString(R.string.notification_text, event.title)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(event.id.toInt(), builder.build())
            } catch (e: SecurityException) {
                // จัดการกรณีที่ไม่ได้รับสิทธิ์ในการแจ้งเตือน
                e.printStackTrace()
            }
        }
    }
}