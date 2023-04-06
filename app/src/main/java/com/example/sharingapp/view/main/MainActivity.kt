package com.example.sharingapp.view.main


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharingapp.R
import com.example.sharingapp.auth.LoginActivity
import com.example.sharingapp.databinding.ActivityMainBinding
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.setting.ViewModelFactory
import com.example.sharingapp.view.story.AddStoryActivity
import com.example.sharingapp.view.story.StoryAdapter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val storyAdapter = StoryAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.datas.layoutManager = LinearLayoutManager(this)

        val preference = SharedPreference.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(preference, this))[MainViewModel::class.java]

        binding.datas.adapter = storyAdapter

        viewModel.story.observe(this) { stories ->
            stories?.let {
                storyAdapter.submitList(it.listStory)
            }
        }

        viewModel.getStories(null, null, null)


        viewModel.error.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }


        binding.swipe.setOnRefreshListener {
            viewModel.getStories(null, null, null)
            binding.swipe.isRefreshing = false
        }

        viewModel.isLoading.observe(this) {
            binding.swipe.isRefreshing = it
        }

        addActivity()

    }

    private fun addActivity(){
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        menu?.clear()
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}