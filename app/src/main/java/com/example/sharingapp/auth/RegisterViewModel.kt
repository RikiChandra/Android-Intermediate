package com.example.sharingapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.responses.RegisterResponses
import com.example.sharingapp.setting.SettingEvent
import com.example.sharingapp.setting.SharedPreference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val sharedPreference: SharedPreference) : ViewModel() {

    private val _isRegister: MutableLiveData<RegisterResponses> = MutableLiveData()
    private val _errorRegister: MutableLiveData<SettingEvent<String>> = MutableLiveData()


    fun _getRegister(): LiveData<RegisterResponses>{
        return _isRegister
    }

    fun errorRegister(): LiveData<SettingEvent<String>>{
        return _errorRegister
    }

    private val isRegisterd: MutableLiveData<Boolean> = MutableLiveData()
    fun isRegisterd(): LiveData<Boolean>{
        return isRegisterd
    }

    private val isSuccessful: MutableLiveData<Boolean> = MutableLiveData()
    fun isSuccessful(): LiveData<Boolean>{
        return isSuccessful
    }


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


//    fun register(name: String, email: String, password: String) {
//        _isLoading.postValue(true)
//        val client = ApiConfig.getApiService().register(name, email, password)
//        client.enqueue(object : Callback<RegisterResponses> {
//            override fun onResponse(
//                call: Call<RegisterResponses>,
//                response: Response<RegisterResponses>
//            ) {
//                _isLoading.postValue(false)
//                if (response.isSuccessful) {
//                    response.body()?.let{
//                        if (it.error as Boolean){
//                            _errorRegister.value = SettingEvent(it.message)
//                        } else{
//                            _isRegister.value= response.body() as RegisterResponses
//                        }
//                    } ?: run {
//                        _errorRegister.value = SettingEvent("Terjadi Kesalahan")
//                    }
//                } else {
//                    val body: RegisterResponses? = Gson().fromJson(response.errorBody()?.string(), RegisterResponses::class.java)
//
//                    _errorRegister.value = SettingEvent(body?.message ?: "Terjadi Kesalahan")
//                }
//            }
//
//            override fun onFailure(call: Call<RegisterResponses>, t: Throwable) {
//                _isLoading.postValue(false)
//                _errorRegister.value = SettingEvent(t.message ?: "Terjadi Kesalahan")
//            }
//
//        })
//    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponses> {
            override fun onResponse(
                call: Call<RegisterResponses>,
                response: Response<RegisterResponses>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    if (response.body()?.error == false) {
                        _isRegister.value = response.body()
                    } else {
                        _errorRegister.value = SettingEvent(response.body()?.message ?: "Terjadi Kesalahan")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = Gson().fromJson(errorBody, RegisterResponses::class.java)?.message
                    _errorRegister.value = SettingEvent(errorMessage ?: "Terjadi Kesalahan")
                }
            }

            override fun onFailure(call: Call<RegisterResponses>, t: Throwable) {
                _isLoading.value = false
                _errorRegister.value = SettingEvent(t.message ?: "Terjadi Kesalahan")
            }
        })
    }





}