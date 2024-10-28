package com.example.dicodingevent.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.data.SettingPreferences
import com.example.dicodingevent.data.dataStore
import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.di.Injection

class ViewModelFactory(
    private val pref: SettingPreferences,
    private val eventRepository: EventRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref, eventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    SettingPreferences.getInstance(context.dataStore),
                    Injection.provideRepository(context)
                ).also { instance = it }
            }
    }
}