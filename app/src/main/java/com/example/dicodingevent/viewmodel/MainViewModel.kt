package com.example.dicodingevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.data.SettingPreferences
import com.example.dicodingevent.data.database.EventEntity
import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.data.response.Event
import com.example.dicodingevent.data.response.ListEventsItem
import kotlinx.coroutines.launch

class MainViewModel(
    private val pref: SettingPreferences,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _upcomingEvent = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvent: LiveData<List<ListEventsItem>> = _upcomingEvent
    private val _finishedEvent = MutableLiveData<List<ListEventsItem>>()
    val finishedEvent: LiveData<List<ListEventsItem>> = _finishedEvent
    private val _detailEvent = MutableLiveData<Event>()
    val detailEvent: LiveData<Event> = _detailEvent
    private val _searchEvent = MutableLiveData<List<ListEventsItem>>()
    val searchEvent: LiveData<List<ListEventsItem>> = _searchEvent
    private val _favoriteEvents = MutableLiveData<List<EventEntity>>()
    val favoriteEvents: LiveData<List<EventEntity>> = _favoriteEvents
    private val _favoriteStatus = MutableLiveData<Boolean>()
    val favoriteStatus: LiveData<Boolean> = _favoriteStatus

    init {
        getUpcomingEvent()
        getFinishedEvent()
        getFavoriteEvents()
    }

    fun getUpcomingEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.getUpcomingEvents()
            _isLoading.value = false
            result.onSuccess {
                _upcomingEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun getFinishedEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.getFinishedEvents()
            _isLoading.value = false
            result.onSuccess {
                _finishedEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun getDetailEvent(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.getDetailEvent(id)
            _isLoading.value = false
            result.onSuccess {
                _detailEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    fun searchEvent(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.searchEvent(keyword)
            _isLoading.value = false
            result.onSuccess {
                _searchEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }
    fun checkFavoriteStatus(eventId: String) {
        viewModelScope.launch {
            eventRepository.isEventFavorite(eventId)
                .collect { isFavorite ->
                    _favoriteStatus.value = isFavorite
                }
        }
    }

    fun addToFavorite(event: EventEntity) {
        viewModelScope.launch {
            try {
                eventRepository.addFavoriteEvent(event)
                _favoriteStatus.value = true
                clearErrorMessage()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add favorite event"
            }
        }
    }

    fun removeFromFavorite(event: EventEntity) {
        viewModelScope.launch {
            try {
                // Delete dari events table
                eventRepository.deleteFavoriteEvent(event)
                // Delete dari favorite table
                eventRepository.deleteFavoriteEvent(FavoriteEvent(id = event.id))
                _favoriteStatus.value = false
                clearErrorMessage()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove favorite event"
            }
        }
    }

    fun getFavoriteEvents() {
        viewModelScope.launch {
            eventRepository.getFavoriteEvents()
                .collect { events ->
                    _favoriteEvents.value = events
                }
        }
    }

    fun isEventFavorite(eventId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            eventRepository.isEventFavorite(eventId)
                .collect { isFavorite ->
                    result.value = isFavorite
                }
        }
        return result
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getReminderSettings(): LiveData<Boolean> {
        return pref.getReminderSetting().asLiveData()
    }

    fun saveReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(isReminderActive)
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}