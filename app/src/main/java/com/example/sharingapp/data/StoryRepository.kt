package com.example.sharingapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.sharingapp.api.ApiService
import com.example.sharingapp.responses.Story
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class StoryRepository(private val sharedPreference: SharedPreference, private val apiService: ApiService) {

    fun getStory(): LiveData<PagingData<Story>>{
        return Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = true),
                pagingSourceFactory = { StoryPagingSource(apiService,sharedPreference) }
        ).liveData

    }




}