package com.example.sharingapp.view.main


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.data.LoadingStateAdapter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val storyAdapter = StoryAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        val preference = SharedPreference.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(preference, ApiConfig.getApiService()))[MainViewModel::class.java]

        if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
            binding.datas.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.datas.layoutManager = LinearLayoutManager(this)
        }

        binding.datas.adapter = storyAdapter

        viewModel.stories.observe(this) { stories ->
            stories?.let {
                storyAdapter.submitData(lifecycle, stories)
                binding.datas.postDelayed({
                    binding.datas.layoutManager?.scrollToPosition(0)
                }, 500) // tambahkan penundaan selama 500ms
            }
        }


        viewModel.error.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.swipe.setOnRefreshListener {
            refreshData()
        }

        storyAdapter.loadStateFlow.asLiveData().observe(this) {
            binding.swipe.isRefreshing = it.source.refresh is LoadState.Loading
        }

        addActivity()
        setData()

    }

    private fun addActivity(){
        binding.fab.setOnClickListener {
            storyIntent.launch(Intent(this, AddStoryActivity::class.java))
        }
    }

    private val storyIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == INTENT_ADD_STORY) {
                refreshData()
            }
        }


    private fun setData(){
        binding.datas.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

    }

    private fun refreshData(){
        storyAdapter.refresh()
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

    companion object {
        const val INTENT_ADD_STORY = 2222
    }


}