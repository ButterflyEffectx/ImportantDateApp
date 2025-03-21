package com.example.importantdatesreminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: Long, // เก็บเป็น timestamp
    val notificationEnabled: Boolean = true
) {
    fun getFormattedDate(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return "$day/$month/$year"
    }

    fun getDaysUntil(): Int {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val eventDate = Calendar.getInstance()
        eventDate.timeInMillis = date
        eventDate.set(Calendar.HOUR_OF_DAY, 0)
        eventDate.set(Calendar.MINUTE, 0)
        eventDate.set(Calendar.SECOND, 0)
        eventDate.set(Calendar.MILLISECOND, 0)

        // ถ้าวันที่ผ่านไปแล้วในปีนี้ ให้ตั้งเป็นปีหน้า
        if (eventDate.before(today)) {
            eventDate.add(Calendar.YEAR, 1)
        }

        val diff = eventDate.timeInMillis - today.timeInMillis
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }
}