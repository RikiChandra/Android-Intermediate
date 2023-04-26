package com.example.sharingapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.runBlocking

class AuthRepository(private val sharedPreference: SharedPreference) {


    private val _isLogged = MutableLiveData<Boolean>()

    fun getToken(): LiveData<String?> {
        return sharedPreference.ambilToken().asLiveData()
    }

    fun isLogged(): LiveData<Boolean> {
        return sharedPreference.isLogged().asLiveData()
    }

    fun logout() {
        runBlocking {
            sharedPreference.isLogout()
            _isLogged.postValue(false)
        }
    }

}