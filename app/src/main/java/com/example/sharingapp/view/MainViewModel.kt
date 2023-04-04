package com.example.sharingapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.api.ApiService
import com.example.sharingapp.responses.StoryResponses
import com.example.sharingapp.setting.SettingEvent
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val preference: SharedPreference) : ViewModel() {

    private val storyData = MutableLiveData<StoryResponses>()
    val story: LiveData<StoryResponses>
        get() = storyData


    private val storyError = MutableLiveData<SettingEvent<String>>()
    val error: LiveData<SettingEvent<String>>
        get() = storyError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    private val _isLogged = MutableLiveData<Boolean>()
    val isLogged: LiveData<Boolean>
        get() = _isLogged


    init {
        viewModelScope.launch {
            preference.isLogged().collect {
                _isLogged.postValue(it)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preference.isLogout()
            _isLogged.postValue(false)
        }
    }


    fun getStories(page: Int?, size: Int?, location: Int?) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val token = preference.ambilToken().first()
            val response = withContext(Dispatchers.IO) {
                ApiConfig.getApiService().getStories("Bearer $token", page, size, location).execute()
            }
            if (response.isSuccessful) {
                response.body()?.let { storyData.postValue(it) }
            } else {
                storyError.postValue(SettingEvent(response.errorBody()?.string() ?: "Unknown error"))
            }
            _isLoading.postValue(false)
        }
    }

}