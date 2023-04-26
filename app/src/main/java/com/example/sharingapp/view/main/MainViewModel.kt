package com.example.sharingapp.view.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.data.AuthRepository
import com.example.sharingapp.data.StoryRepository
import com.example.sharingapp.responses.Story
import com.example.sharingapp.responses.StoryResponses
import com.example.sharingapp.setting.SettingEvent
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val storyRepository: StoryRepository, private val authRepository: AuthRepository) : ViewModel() {




    private val storyError = MutableLiveData<SettingEvent<String>>()
    val error: LiveData<SettingEvent<String>>
        get() = storyError


    fun stories() = storyRepository.getStory().cachedIn(viewModelScope)

    fun isLogout() = authRepository.logout()

    fun isToken() = authRepository.getToken()




}