package com.example.sharingapp.view.main

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.sharingapp.data.AuthRepository
import com.example.sharingapp.data.StoryRepository
import com.example.sharingapp.setting.SettingEvent


class MainViewModel(private val storyRepository: StoryRepository, private val authRepository: AuthRepository) : ViewModel() {




    private val storyError = MutableLiveData<SettingEvent<String>>()
    val error: LiveData<SettingEvent<String>>
        get() = storyError


    fun stories() = storyRepository.getStory().cachedIn(viewModelScope)


    fun isLogout() = authRepository.logout()

    fun isToken() = authRepository.getToken()




}