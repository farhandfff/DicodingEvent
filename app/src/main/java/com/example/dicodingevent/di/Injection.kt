package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        return EventRepository(apiService)
    }

}