package com.example.importantdatesreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.example.importantdatesreminder.data.Event
import java.util.Calendar

object AlarmManagerUtils {

    private const val DAILY_ALARM_REQUEST_CODE = 1001

    fun setupDailyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, DailyCheckReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // ตั้งเวลาให้ทำงานทุกวันเวลา 8:00 น.
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            // ถ้าเวลาปัจจุบันเกิน 8:00 น. แล้ว ให้ตั้งเป็นวันพรุ่งนี้
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun scheduleAlarm(context: Context, event: Event) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra("event_id", event.id)
            putExtra("event_title", event.title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // คำนวณเวลาในการแจ้งเตือน (8:00 น. ของวันนั้น)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = event.date
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // ถ้าวันที่ผ่านไปแล้ว ให้เลื่อนไปปีถัดไป
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.YEAR, 1)
        }

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, EventAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}