package com.example.sharingapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.sharingapp.api.ApiService
import com.example.sharingapp.responses.Story
import com.example.sharingapp.setting.SharedPreference

class StoryRepository(private val sharedPreference: SharedPreference, private val apiService: ApiService) {

    fun getStory(): LiveData<PagingData<Story>>{
        return Pager(
            config = PagingConfig(pageSize = 5, prefetchDistance = 2, initialLoadSize = 5),
                pagingSourceFactory = { StoryPagingSource(apiService,sharedPreference) }
        ).liveData

    }


}