package com.example.importantdatesreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // ตั้งค่า daily alarm ใหม่
            AlarmManagerUtils.setupDailyAlarm(context)

            // ตั้งค่าการแจ้งเตือนสำหรับวันสำคัญทั้งหมดใหม่
            val eventViewModel = EventViewModel(context.applicationContext as android.app.Application)

            CoroutineScope(Dispatchers.IO).launch {
                eventViewModel.allEvents.value?.forEach { event ->
                    if (event.notificationEnabled) {
                        AlarmManagerUtils.scheduleAlarm(context, event)
                    }
                }
            }
        }
    }
}