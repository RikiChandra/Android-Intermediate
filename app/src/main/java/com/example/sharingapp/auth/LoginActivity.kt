package com.example.sharingapp.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

//        binding.loginButton.setOnClickListener {
//            val username = binding.email.text.toString()
//            val password = binding.password.text.toString()
//            viewModel.login(username, password)
//            hideKeyboard()
//        }

        // Inisialisasi TextChangedListener pada onCreate()
        val passwordInput = binding.password
        val passwordLayout = binding.passwordLayout

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Password validation
                // Display error automatically if the password doesn't meet certain criteria
                if (!s.isNullOrEmpty() && s.length < 8) {
                    passwordLayout.error = getString(R.string.error)
                } else {
                    passwordLayout.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.loginButton.setOnClickListener {
            val username = binding.email.text.toString()
            val password = passwordInput.text.toString()

            if (password.length < 8) {
                passwordLayout.error = getString(R.string.error)
                return@setOnClickListener
            } else {
                passwordLayout.error = null
            }

            viewModel.login(username, password)
            hideKeyboard()
        }



        viewModel.isLoading.observe(this) {
            binding.loading.visibility = View.VISIBLE
        }

        viewModel.isLogged.observe(this) { isLogged ->
            if (isLogged) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                binding.loading.visibility = View.GONE
                AlertDialog.Builder(this)
                    .setTitle("Login Failed")
                    .setMessage("Invalid email or password")
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