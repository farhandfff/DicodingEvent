package com.dicoding.myapplication16.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dicodingevent.data.database.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteEvent: EventEntity)

    @Delete
    suspend fun delete(favoriteEvent: EventEntity)

    @Query("SELECT * from favorite ORDER BY id ASC")
    fun getAllFavoriteEvent(): Flow<List<EventEntity>>

    @Query("SELECT * from events WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: EventEntity)

    @Delete
    suspend fun deleteFavoriteEvent(favorite: EventEntity): Int

    @Query("SELECT EXISTS(SELECT * FROM favorite WHERE id = :eventId)")
    fun isEventFavorite(eventId: String): Flow<Boolean>

    @Query("SELECT * FROM events WHERE id IN (SELECT id FROM favorite)")
    fun getFavoriteEvents(): Flow<List<EventEntity>>
}