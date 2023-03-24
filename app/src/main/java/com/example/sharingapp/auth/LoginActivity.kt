package com.example.sharingapp.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.MainActivity
import com.example.sharingapp.R
import com.example.sharingapp.databinding.ActivityLoginBinding
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.setting.SharedPreferenceViewModel
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)


        viewModel = ViewModelProvider(this, SharedPreferenceViewModel(SharedPreference.getInstance(dataStore), this))[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val username = binding.email.text.toString()
            val password = binding.password.text.toString()
            viewModel.login(username, password)
        }

        viewModel.isLoading.observe(this) {
            binding.loading.visibility = View.VISIBLE
        }

        viewModel.isLogged.observe(this) { isLogged ->
            if (isLogged) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }

    }
}