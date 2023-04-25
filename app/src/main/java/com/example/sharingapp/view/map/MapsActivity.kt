package com.example.sharingapp.view.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.R
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.databinding.ActivityMapsBinding
import com.example.sharingapp.responses.StoryResponses
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.setting.ViewModelFactory
import com.example.sharingapp.view.main.MainViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val apiService = ApiConfig.getApiService()
    private lateinit var viewModel: MapsViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()

        val preference = SharedPreference.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(preference, ApiConfig.getApiService()))[MapsViewModel::class.java]

        viewModel.storyList.observe(this) { stories ->
            var firstMarkerAdded = false
            for (story in stories) {
                if (story.lat != null && story.lon != null) {
                    val latLng = LatLng(story.lat.toDouble(), story.lon.toDouble())
                    if (!firstMarkerAdded) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                        mMap.addMarker(MarkerOptions().position(latLng).title(story.name))
                        firstMarkerAdded = true
                    } else {
                        mMap.addMarker(MarkerOptions().position(latLng).title(story.name))
                    }
                }
            }
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


}