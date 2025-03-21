package com.example.importantdatesreminder

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.importantdatesreminder.data.Event
import com.example.importantdatesreminder.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: EventAdapter
    private lateinit var addEventLauncher: ActivityResultLauncher<Intent>
    private lateinit var editEventLauncher: ActivityResultLauncher<Intent>
    private var currentLanguage = "th" // ค่าเริ่มต้นเป็นภาษาไทย

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ดึงค่าภาษาที่บันทึกไว้
        currentLanguage = LocaleHelper.getLanguage(this)
        setupLanguageButton()

        // เตรียม ActivityResultLauncher สำหรับการเพิ่มวันสำคัญ
        addEventLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // รีเฟรชข้อมูลจะอัพเดทโดยอัตโนมัติผ่าน LiveData
            }
        }

        // เตรียม ActivityResultLauncher สำหรับการแก้ไขวันสำคัญ
        editEventLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // รีเฟรชข้อมูลจะอัพเดทโดยอัตโนมัติผ่าน LiveData
            }
        }

        // ตั้งค่า RecyclerView
        adapter = EventAdapter(this) { event ->
            showEventOptions(event)
        }
        binding.rvEvents.adapter = adapter
        binding.rvEvents.layoutManager = LinearLayoutManager(this)

        // ตั้งค่า ViewModel
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        eventViewModel.allEvents.observe(this) { events ->
            adapter.submitList(events)

            // แสดงข้อความเมื่อไม่มีข้อมูล
            if (events.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvEvents.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvEvents.visibility = View.VISIBLE
            }
        }

        // ตั้งค่าปุ่มเพิ่มวันสำคัญ
        binding.fabAddEvent.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            addEventLauncher.launch(intent)
        }

        // ตั้งค่าปุ่มเปลี่ยนภาษา
        binding.btnLanguage.setOnClickListener {
            toggleLanguage()
        }

        // ตั้งค่าปุ่มออกจากแอพ
        binding.btnExit.setOnClickListener {
            confirmExit()
        }

        // เริ่มตั้งค่า Notification Service
        NotificationUtils.createNotificationChannel(this)

        // ตั้งค่า Alarm Manager สำหรับตรวจสอบการแจ้งเตือนทุกวัน
        AlarmManagerUtils.setupDailyAlarm(this)
    }

    private fun setupLanguageButton() {
        // ตั้งค่าข้อความของปุ่มตามภาษาปัจจุบัน
        if (currentLanguage == "th") {
            binding.btnLanguage.text = getString(R.string.language_button_en)
        } else {
            binding.btnLanguage.text = getString(R.string.language_button_th)
        }
    }

    private fun toggleLanguage() {
        // สลับภาษาระหว่างไทยและอังกฤษ
        currentLanguage = if (currentLanguage == "th") "en" else "th"

        // บันทึกและเปลี่ยนภาษา
        LocaleHelper.setLocale(this, currentLanguage)

        // รีสตาร์ทแอคทิวิตี้เพื่อให้การเปลี่ยนภาษามีผล
        recreate()
    }

    private fun confirmExit() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_app)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                finish()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEventOptions(event: Event) {
        val options = arrayOf(
            getString(R.string.edit_event),
            getString(R.string.delete_event)
        )

        AlertDialog.Builder(this)
            .setTitle(event.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editEvent(event)
                    1 -> deleteEvent(event)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun editEvent(event: Event) {
        val intent = Intent(this, AddEventActivity::class.java).apply {
            putExtra(AddEventActivity.EXTRA_EVENT_ID, event.id)
            putExtra(AddEventActivity.EXTRA_EVENT_TITLE, event.title)
            putExtra(AddEventActivity.EXTRA_EVENT_DATE, event.date)
            putExtra(AddEventActivity.EXTRA_EVENT_NOTIFICATION, event.notificationEnabled)
        }
        editEventLauncher.launch(intent)
    }

    private fun deleteEvent(event: Event) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_event)
            .setMessage(getString(R.string.delete_confirmation, event.title))
            .setPositiveButton(R.string.yes) { _, _ ->
                eventViewModel.delete(event)
                // ยกเลิกการแจ้งเตือนถ้ามี
                AlarmManagerUtils.cancelAlarm(this, event.id.toInt())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    // ตัวจัดการปุ่มย้อนกลับของระบบ
    @Override
    override fun onBackPressed() {
        confirmExit()
    }
}