package com.example.importantdatesreminder.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository(private val eventDao: EventDao) {

    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    suspend fun insert(event: Event): Long {
        return withContext(Dispatchers.IO) {
            eventDao.insert(event)
        }
    }

    suspend fun update(event: Event) {
        withContext(Dispatchers.IO) {
            eventDao.update(event)
        }
    }

    suspend fun delete(event: Event) {
        withContext(Dispatchers.IO) {
            eventDao.delete(event)
        }
    }

    suspend fun getEventsForDate(startDate: Long, endDate: Long): List<Event> {
        return withContext(Dispatchers.IO) {
            eventDao.getEventsForDate(startDate, endDate)
        }
    }
}