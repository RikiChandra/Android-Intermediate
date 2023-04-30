package com.example.sharingapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.api.ApiService
import com.example.sharingapp.responses.AddResponse
import com.example.sharingapp.setting.SettingEvent
import com.example.sharingapp.setting.SharedPreference
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryViewModel(private val preference: SharedPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val postError = MutableLiveData<SettingEvent<String>>()
    val error: LiveData<SettingEvent<String>>
        get() = postError


    fun store(
        onSuccessCallback: OnSuccessCallback<AddResponse>,
        description: String,
        photo: File,
        lat: Float?,
        lon: Float?
    ) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            val token = preference.ambilToken().first()

            ApiConfig.getApiService().storeStory(
                authorization = "Bearer $token",
                description = description.toRequestBody("text/plain".toMediaType()),
                photo = MultipartBody.Part.createFormData(
                    "photo",
                    photo.name,
                    photo.asRequestBody("image/*".toMediaType())
                ),
                lat = lat?.toString()?.toRequestBody("text/plain".toMediaType()),
                lon = lon?.toString()?.toRequestBody("text/plain".toMediaType())
            ).enqueue(object : Callback<AddResponse> {
                override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                    if (response.isSuccessful){
                        response.body()?.let {
                            if(it.error as Boolean){
                                postError.postValue(SettingEvent(it.message as String))
                            } else {
                                onSuccessCallback.onSuccess(response.body()!!)
                            }
                        } ?: run {
                            postError.postValue(SettingEvent("Something went wrong"))
                        }
                    } else {
                        val body: AddResponse? = Gson().fromJson(response.errorBody()?.charStream(), AddResponse::class.java)
                        postError.postValue(SettingEvent(body?.message as String))
                    }
                }

                override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    postError.postValue(SettingEvent(t.message as String))
                }
            })
        }
    }

}

interface OnSuccessCallback<T> {
    fun onSuccess(message: T)
}