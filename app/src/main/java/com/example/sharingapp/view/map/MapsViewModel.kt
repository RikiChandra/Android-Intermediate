package com.example.sharingapp.view.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharingapp.api.ApiService
import com.example.sharingapp.responses.Story
import com.example.sharingapp.responses.StoryResponses
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val apiService: ApiService, private val sharedPreference: SharedPreference) : ViewModel() {

    val storyList: MutableLiveData<List<Story>> by lazy {
        MutableLiveData<List<Story>>().also {
            loadStories()
        }
    }

    private fun loadStories() {
        viewModelScope.launch {
            val token = sharedPreference.ambilToken().first()

            apiService.getStories("Bearer $token", null, 50, 1).enqueue(object : Callback<StoryResponses> {
                override fun onResponse(call: Call<StoryResponses>, response: Response<StoryResponses>) {
                    if (response.isSuccessful) {
                        val stories = response.body()?.listStory

                        if (stories != null) {
                            storyList.postValue(stories)
                        }
                    } else {
                        Log.e("MapsViewModel", "Error fetching data from API")
                    }
                }

                override fun onFailure(call: Call<StoryResponses>, t: Throwable) {
                    Log.e("MapsViewModel", "Error fetching data from API: ${t.message}")
                }
            })
        }
    }


}