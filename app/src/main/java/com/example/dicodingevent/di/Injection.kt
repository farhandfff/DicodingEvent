package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.database.FavoriteEventRoomDatabase
import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteEventRoomDatabase.getDatabase(context)
        val favoriteEventDao = database.favoriteEventDao()
        return EventRepository(apiService, favoriteEventDao)
    }
}