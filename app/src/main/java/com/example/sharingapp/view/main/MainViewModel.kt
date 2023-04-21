package com.example.sharingapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.data.StoryRepository
import com.example.sharingapp.responses.Story
import com.example.sharingapp.responses.StoryResponses
import com.example.sharingapp.setting.SettingEvent
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val preference: SharedPreference, private val storyRepository: StoryRepository) : ViewModel() {




    private val storyError = MutableLiveData<SettingEvent<String>>()
    val error: LiveData<SettingEvent<String>>
        get() = storyError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    private val _isLogged = MutableLiveData<Boolean>()


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


    val stories : LiveData<PagingData<Story>> =
        storyRepository.getStory().cachedIn(viewModelScope)




}