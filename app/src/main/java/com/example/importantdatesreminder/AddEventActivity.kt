package com.example.importantdatesreminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.importantdatesreminder.data.Event
import com.example.importantdatesreminder.databinding.ActivityAddEventBinding
import java.util.Calendar

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private lateinit var eventViewModel: EventViewModel
    private var selectedDate: Long = 0
    private var isEditMode = false
    private var eventId: Long = 0

    private val calendar = Calendar.getInstance()

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_EVENT_TITLE = "extra_event_title"
        const val EXTRA_EVENT_DATE = "extra_event_date"
        const val EXTRA_EVENT_NOTIFICATION = "extra_event_notification"

        private const val TAG = "AddEventActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // ตรวจสอบว่าเป็นโหมดแก้ไขหรือไม่
        if (intent.hasExtra(EXTRA_EVENT_ID)) {
            isEditMode = true
            eventId = intent.getLongExtra(EXTRA_EVENT_ID, 0)
            binding.etEventTitle.setText(intent.getStringExtra(EXTRA_EVENT_TITLE))
            selectedDate = intent.getLongExtra(EXTRA_EVENT_DATE, 0)
            binding.swEnableNotification.isChecked = intent.getBooleanExtra(EXTRA_EVENT_NOTIFICATION, true)

            // อัพเดท UI
            calendar.timeInMillis = selectedDate
            updateSelectedDateUI()

            // เปลี่ยนชื่อหัวข้อและปุ่ม
            binding.tvAddEventTitle.text = getString(R.string.edit_event)
            binding.btnSaveEvent.text = getString(R.string.save_event)
        } else {
            // ตั้งค่าเริ่มต้นเป็นวันปัจจุบัน
            selectedDate = calendar.timeInMillis
            updateSelectedDateUI()
        }

        // ตั้งค่าปุ่มเลือกวันที่
        binding.btnPickDate.setOnClickListener {
            showDatePicker()
        }

        // ตั้งค่าปุ่มบันทึก
        binding.btnSaveEvent.setOnClickListener {
            saveEvent()
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(Calendar.YEAR, selectedYear)
            calendar.set(Calendar.MONTH, selectedMonth)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            selectedDate = calendar.timeInMillis
            updateSelectedDateUI()
        }, year, month, day).show()
    }

    private fun updateSelectedDateUI() {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        binding.tvSelectedDate.text = "$day/$month/$year"
    }

    private fun saveEvent() {
        val title = binding.etEventTitle.text.toString().trim()

        if (title.isEmpty()) {
            binding.tilEventTitle.error = "กรุณาระบุชื่อวันสำคัญ"
            return
        }

        val notificationEnabled = binding.swEnableNotification.isChecked

        // 🔍 Logcat เพื่อ debug ข้อมูลที่บันทึก
        Log.d(TAG, "saveEvent() called")
        Log.d(TAG, "Title: $title")
        Log.d(TAG, "Selected Date (millis): $selectedDate")
        Log.d(TAG, "Notification Enabled: $notificationEnabled")
        Log.d(TAG, "Is Edit Mode: $isEditMode")
        Log.d(TAG, "Event ID: $eventId")

        val event = if (isEditMode) {
            Event(eventId, title, selectedDate, notificationEnabled)
        } else {
            Event(title = title, date = selectedDate, notificationEnabled = notificationEnabled)
        }

        if (isEditMode) {
            eventViewModel.update(event)

            if (notificationEnabled) {
                AlarmManagerUtils.scheduleAlarm(this, event)
            } else {
                AlarmManagerUtils.cancelAlarm(this, event.id.toInt())
            }

            Toast.makeText(this, "อัพเดทข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show()
        } else {
            eventViewModel.insert(event)

            if (notificationEnabled) {
                // ตั้งค่า Alarm จะทำใน NotificationWorker ภายหลัง
            }

            Toast.makeText(this, "เพิ่มวันสำคัญเรียบร้อย", Toast.LENGTH_SHORT).show()
        }

        setResult(RESULT_OK)
        finish()
    }
}
