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
import androidx.lifecycle.lifecycleScope
import com.example.sharingapp.auth.LoginActivity
import com.example.sharingapp.databinding.ActivitySplashScreenBinding
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashScreen : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val preference = SharedPreference.getInstance(dataStore)

        lifecycleScope.launch {
            val token = preference.ambilToken().firstOrNull()

            withContext(Dispatchers.Main) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (token.isNullOrEmpty()) {
                        val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashScreen, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }, 2000)
            }
        }
    }
}
