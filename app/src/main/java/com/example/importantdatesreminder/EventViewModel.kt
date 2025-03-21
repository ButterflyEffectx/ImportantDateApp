package com.example.importantdatesreminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.importantdatesreminder.data.Event
import com.example.importantdatesreminder.data.EventDatabase
import com.example.importantdatesreminder.data.EventRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository
    val allEvents: LiveData<List<Event>>

    init {
        val eventDao = EventDatabase.getDatabase(application).eventDao()
        repository = EventRepository(eventDao)
        allEvents = repository.allEvents
    }

    fun insert(event: Event) = viewModelScope.launch {
        repository.insert(event)
    }

    fun update(event: Event) = viewModelScope.launch {
        repository.update(event)
    }

    fun delete(event: Event) = viewModelScope.launch {
        repository.delete(event)
    }

    suspend fun getTodayEvents(): List<Event> {
        val calendar = Calendar.getInstance()

        // ตั้งเวลาเป็นเที่ยงคืนของวันนี้
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // ตั้งเวลาเป็น 23:59:59 ของวันนี้
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return repository.getEventsForDate(startOfDay, endOfDay)
    }
}