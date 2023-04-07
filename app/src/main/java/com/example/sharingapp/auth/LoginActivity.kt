package com.example.sharingapp.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.view.main.MainActivity
import com.example.sharingapp.R
import com.example.sharingapp.databinding.ActivityLoginBinding
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.setting.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()


        viewModel = ViewModelProvider(this, ViewModelFactory(SharedPreference.getInstance(dataStore), this))[LoginViewModel::class.java]


        binding.loginButton.setOnClickListener {
            val username = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, getString(R.string.emptyField), Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Menghentikan eksekusi kode di sini jika username atau password kosong
            }

            viewModel.login(username, password)
            hideKeyboard()
        }




        binding.textViewLogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }



        viewModel.isLoading.observe(this) {
            loadings : Boolean ->
            binding.apply {
                loading.visibility = if (loadings) View.VISIBLE else View.GONE
                loginButton.isEnabled = !loadings
            }
        }

        viewModel.isLogged.observe(this) { isLogged ->
            if (isLogged) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                binding.loading.visibility = View.GONE
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.loginFailed))
                    .setMessage(getString(R.string.invalidPassowordOrEmail))
                    .setPositiveButton("OK", null)
                    .show()
            }
        }




    }

    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}