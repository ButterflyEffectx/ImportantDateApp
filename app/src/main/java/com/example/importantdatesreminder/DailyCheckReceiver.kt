package com.example.importantdatesreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class DailyCheckReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventViewModel = EventViewModel(context.applicationContext as android.app.Application)

        // ใช้ coroutine เนื่องจาก BroadcastReceiver มีเวลาทำงานจำกัด
        CoroutineScope(Dispatchers.IO).launch {
            val todayEvents = eventViewModel.getTodayEvents()

            // แสดงการแจ้งเตือนสำหรับทุกวันสำคัญวันนี้
            todayEvents.forEach { event ->
                if (event.notificationEnabled) {
                    NotificationUtils.showNotification(context, event)
                }
            }

            // ตั้งค่าการทำงานในวันถัดไป
            AlarmManagerUtils.setupDailyAlarm(context)
        }
    }
}