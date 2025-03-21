package com.example.importantdatesreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.importantdatesreminder.data.Event

class EventAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getLongExtra("event_id", -1)
        val eventTitle = intent.getStringExtra("event_title") ?: "Unknown Event"

        if (eventId != -1L) {
            val event = Event(
                id = eventId,
                title = eventTitle,
                date = System.currentTimeMillis(),
                notificationEnabled = true
            )

            // แสดงการแจ้งเตือน
            NotificationUtils.showNotification(context, event)

            // ตั้งการแจ้งเตือนสำหรับปีถัดไป
            AlarmManagerUtils.scheduleAlarm(context, event)
        }
    }
}