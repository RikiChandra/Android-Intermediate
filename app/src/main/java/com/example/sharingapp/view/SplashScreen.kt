package com.example.sharingapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.sharingapp.auth.LoginActivity
import com.example.sharingapp.databinding.ActivitySplashScreenBinding
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.view.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val preference  = SharedPreference.getInstance(dataStore)


        var token: String?

        runBlocking {
            token = preference.ambilToken().first()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (token.isNullOrEmpty()){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)

    }
}