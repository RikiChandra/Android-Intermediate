package com.example.sharingapp.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.api.ApiService
import com.example.sharingapp.view.main.MainViewModel
import com.example.sharingapp.auth.LoginViewModel
import com.example.sharingapp.auth.RegisterViewModel
import com.example.sharingapp.data.StoryRepository
import com.example.sharingapp.view.map.MapsViewModel
import com.example.sharingapp.view.story.StoryViewModel

class ViewModelFactory(private val preference: SharedPreference, private val apiService: ApiService) : ViewModelProvider.Factory {

    private val storyRepository = StoryRepository(preference, apiService)


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preference) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preference, storyRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(preference) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(preference) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(apiService, preference) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class" + modelClass.name)
        }
    }

}