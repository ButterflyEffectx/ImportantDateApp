package com.example.importantdatesreminder.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): LiveData<List<Event>>

    @Insert
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM events WHERE date >= :startDate AND date <= :endDate")
    fun getEventsForDate(startDate: Long, endDate: Long): List<Event>
}