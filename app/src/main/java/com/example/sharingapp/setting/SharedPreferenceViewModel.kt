package com.example.sharingapp.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.MainViewModel
import com.example.sharingapp.auth.LoginViewModel

class SharedPreferenceViewModel(private val preference: SharedPreference, private val context: Context) : ViewModelProvider.Factory {



    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preference) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preference) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class" + modelClass.name)
        }
    }

}