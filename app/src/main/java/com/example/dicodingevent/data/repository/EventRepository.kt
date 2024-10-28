package com.example.dicodingevent.data.repository

import com.example.dicodingevent.data.database.FavoriteEventDao
import com.example.dicodingevent.data.Results
import com.example.dicodingevent.data.database.EventEntity
import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.data.response.Event
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class EventRepository(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao
) {
    suspend fun getUpcomingEvents(): Result<List<ListEventsItem>> {
        return try {
            val response = apiService.getAllEvent()
            if (response.isSuccessful) {
                Result.success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch events"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFinishedEvents(): Result<List<ListEventsItem>> {
        return try {
            val response = apiService.getFinishedEvents()
            if (response.isSuccessful) {
                Result.success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch events"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDetailEvent(eventId: Int): Result<Event?> {
        return try {
            val response = apiService.getDetailEvent(eventId)
            if (response.isSuccessful) {
                Result.success(response.body()?.event)
            } else {
                Result.failure(Exception("Failed to fetch events"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchEvent(keyword: String): Result<List<ListEventsItem>> {
        return try {
            val response = apiService.searchEvent(keyword)
            if (response.isSuccessful) {
                Result.success(response.body()?.listEvents ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch events"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFavoriteEvents(): Flow<List<EventEntity>> {
        return favoriteEventDao.getFavoriteEvents()
    }

    suspend fun addFavoriteEvent(event: EventEntity) {
        favoriteEventDao.insert(event)
        val favorite = FavoriteEvent(
            id = event.id,
            name = event.name,
            mediaCover = event.mediaCover
        )
        favoriteEventDao.insertFavorite(favorite)
    }

    suspend fun deleteFavoriteEvent(event: EventEntity) {
        favoriteEventDao.delete(event)
    }

    suspend fun deleteFavoriteEvent(favorite: FavoriteEvent) {
        favoriteEventDao.deleteFavoriteEvent(favorite)
    }

    fun isEventFavorite(eventId: String): Flow<Boolean> {
        return favoriteEventDao.isEventFavorite(eventId)
    }
}