package com.example.dicodingevent.data.repository

import com.dicoding.myapplication16.data.database.FavoriteEntity
import com.dicoding.myapplication16.data.database.FavoriteEventDao
import com.example.dicodingevent.data.Results
import com.example.dicodingevent.data.database.EventEntity
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

    fun getFavoriteEvents(toString: String): Flow<Results<List<EventEntity>>> {
        return favoriteEventDao.getFavoriteEvents()
            .map { Results.Success(it)}
            .catch { Results.Error(it.message ?: "An unknown error occurred") }

    }

    suspend fun addFavoriteEvent(favoriteId: String) {
        val favorite = EventEntity(favoriteId)
        favoriteEventDao.insertFavorite(favorite)
    }

    suspend fun deleteFavoriteEvent(favoriteId: String) {
        val favorite = EventEntity(favoriteId)
        favoriteEventDao.deleteFavoriteEvent(favorite)
    }

    fun isEventFavorite(eventId: String): Flow<Boolean> {
        return favoriteEventDao.isEventFavorite(eventId)
    }
}
