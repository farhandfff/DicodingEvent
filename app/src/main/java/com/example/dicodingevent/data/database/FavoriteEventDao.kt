package com.example.dicodingevent.data.database

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

    @Query("SELECT * from events ORDER BY id ASC")
    fun getAllFavoriteEvent(): Flow<List<EventEntity>>

    @Query("SELECT * from events WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEvent)

    @Delete
    suspend fun deleteFavoriteEvent(favorite: FavoriteEvent): Int

    @Query("SELECT EXISTS(SELECT * FROM events WHERE id = :eventId)")
    fun isEventFavorite(eventId: String): Flow<Boolean>

    @Query("""
        SELECT events.* FROM events 
        INNER JOIN favorite ON events.id = favorite.id
    """)
    fun getFavoriteEvents(): Flow<List<EventEntity>>
}