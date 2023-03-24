package com.example.sharingapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.launch

class MainViewModel(private val preference: SharedPreference) : ViewModel() {


    private val _isLogged = MutableLiveData<Boolean>()
    val isLogged: LiveData<Boolean>
        get() = _isLogged

    init {
        viewModelScope.launch {
            preference.isLogged().collect {
                _isLogged.postValue(it)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preference.isLogout()
            _isLogged.postValue(false)
        }
    }


}