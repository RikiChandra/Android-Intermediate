package com.example.sharingapp.auth


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class LoginViewModel(private val preference: SharedPreference) : ViewModel() {


    private val _isLogged = MutableLiveData<Boolean>()
       val isLogged: LiveData<Boolean>
            get() = _isLogged
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val client = ApiConfig.getApiService().login(email, password)
                val response = client.awaitResponse()
                _isLoading.postValue(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.loginResult.token != null) {
                        preference.isSaved(responseBody.loginResult.token, responseBody.loginResult.name)
                        preference.isState()
                        _isLogged.postValue(true)
                        Log.d("TAG", "Token Saya: ${responseBody.loginResult.token}")
                        Log.d("TAG", "id Saya: ${responseBody.loginResult.userId}")
                    } else {
                        _isLogged.postValue(false)
                    }
                } else {
                    _isLogged.postValue(false)
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _isLogged.postValue(false)
            }
        }
    }





}