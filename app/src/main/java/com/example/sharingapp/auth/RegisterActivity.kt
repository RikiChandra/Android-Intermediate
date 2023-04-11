package com.example.sharingapp.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.R
import com.example.sharingapp.databinding.ActivityRegisterBinding
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.setting.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()


        val pref = SharedPreference.getInstance(dataStore)
        val viewModel = ViewModelProvider(this, ViewModelFactory(pref, this@RegisterActivity))[RegisterViewModel::class.java]


        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (name.isEmpty()) {
                binding.editTextName.error = getString(R.string.error_nama)
                binding.editTextName.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.editTextEmail.error = getString(R.string.error_email)
                binding.editTextEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 8) {
                binding.editTextPassword.error = getString(R.string.password)
                binding.editTextPassword.requestFocus()
                return@setOnClickListener
            }

            viewModel.register(name, email, password)
        }




        viewModel.isRegisterd().observe(this) {
            isRegisterd ->
            if (isRegisterd) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }


        viewModel._getRegister().observe(this) {
            responsesRegister ->
            if (!(responsesRegister.error as Boolean)) {
                Snackbar.make(binding.root, responsesRegister.message, Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorRegister().observe(this) {
            error ->
            error.getContentIfNotHandled()?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonRegister.isEnabled = !isLoading
            binding.editTextName.isEnabled = !isLoading
            binding.editTextEmail.isEnabled = !isLoading
            binding.editTextPassword.isEnabled = !isLoading
        }



    }
}