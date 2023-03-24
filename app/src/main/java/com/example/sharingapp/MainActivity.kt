package com.example.sharingapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.auth.LoginActivity
import com.example.sharingapp.databinding.ActivityMainBinding
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.setting.SharedPreferenceViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val preference  = SharedPreference.getInstance(dataStore)

        viewModel = ViewModelProvider(this, SharedPreferenceViewModel(preference, this))[MainViewModel::class.java]

        viewModel.isLogged.observe(this){
            if (!it){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.logout.setOnClickListener {
            viewModel.logout()
        }


    }
}