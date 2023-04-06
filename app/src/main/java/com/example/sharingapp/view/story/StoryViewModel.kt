package com.example.sharingapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.responses.AddResponse
import com.example.sharingapp.responses.StoryResponses
import com.example.sharingapp.setting.SettingEvent
import com.example.sharingapp.setting.SharedPreference
import com.google.gson.Gson
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



    fun post(
        onSuccessCallback: OnSuccessCallback<AddResponse>,
        description: String,
        photo : File,
        lat: Float?,
        lon: Float?
    ){
        val token = preference.ambilToken()

        _isLoading.postValue(true)

        val descriptionPart = description.toRequestBody("text/plain".toMediaType())
        val photoPart = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            photo.asRequestBody("image/jpeg".toMediaType())
        )
        val latPart = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val lonPart = lon?.toString()?.toRequestBody("text/plain".toMediaType())

        ApiConfig.getApiService().storeStory("Bearer $token",
            description = descriptionPart,
            photo = photoPart,
            lat = latPart,
            lon = lonPart
        ).enqueue(object : Callback<AddResponse>{
            override fun onResponse(
                call: retrofit2.Call<AddResponse>,
                response: retrofit2.Response<AddResponse>
            ) {
                _isLoading.postValue(false)
                if (response.isSuccessful){
                    response.body()?.let {
                        if (it.error as Boolean){
                            postError.value = SettingEvent(it.message as String)
                        } else{
                            onSuccessCallback.onSuccess(response.body()!!)
                        }
                    } ?: run {
                        postError.value = SettingEvent("Terjadi kesalahan")
                    }
                } else{
                    val body: AddResponse?= Gson().fromJson(response.errorBody()?.string(), AddResponse::class.java)
                    postError.value = SettingEvent(body?.message as String)
                }
            }

            override fun onFailure(call: retrofit2.Call<AddResponse>, t: Throwable) {
                _isLoading.postValue(false)
                postError.value = SettingEvent(t.message.toString())
            }

        }

        )

    }








}

interface OnSuccessCallback<T> {
    fun onSuccess(message: T)
}