package com.example.sharingapp.view.map


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.sharingapp.R
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.databinding.ActivityMapsBinding
import com.example.sharingapp.setting.SharedPreference
import com.example.sharingapp.setting.ViewModelFactory
import com.example.sharingapp.view.detail.DetailActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
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

        val preference = SharedPreference.getInstance(dataStore)

        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap

            lifecycleScope.launch {
                val mapType = preference.getMapType().first()
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapsActivity, mapType.value))
            }
        }
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



        mMap.setOnMarkerClickListener { marker ->
            // Dapatkan informasi yang terkait dengan marker yang diklik
            val story = viewModel.storyList.value?.find { it.name == marker.title }

            // Tampilkan BottomSheet dengan informasi yang sesuai
            val bottomSheet = BottomSheetDialog(this)
            val bottomSheetView = layoutInflater.inflate(R.layout.activity_bottom_sheet_detail, null)

            // Isi tampilan BottomSheet dengan informasi yang sesuai
            bottomSheetView.findViewById<TextView>(R.id.title).text = story?.name
            bottomSheetView.findViewById<TextView>(R.id.createdAt).text = story?.description

            // Tampilkan gambar menggunakan Glide
            Glide.with(this)
                .load(story?.photoUrl)
                .into(bottomSheetView.findViewById(R.id.img))

            // Tambahkan onClickListener untuk membuka DetailActivity
            bottomSheetView.setOnClickListener {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("story", story)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(bottomSheetView.findViewById(R.id.img), getString(R.string.transitionImage)),
                    Pair(bottomSheetView.findViewById<TextView>(R.id.title), getString(R.string.name)),
                    Pair(bottomSheetView.findViewById<TextView>(R.id.createdAt), getString(R.string.createdAt))
                )

                startActivity(intent, optionsCompat.toBundle())
            }

            bottomSheet.setContentView(bottomSheetView)
            bottomSheet.show()

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 10f),10, null)

            true
        }






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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        menu?.clear()
        inflater.inflate(R.menu.option_maps, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.chgStyle -> {
                myTypeMap()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun myTypeMap() {
        val preference = SharedPreference.getInstance(dataStore)
        val bottomSheet = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.activity_option_style_map, null)

        bottomSheetView.findViewById<MaterialCardView>(R.id.defaultMap).setOnClickListener {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, SharedPreference.MapType.DEFAULT.value))
            lifecycleScope.launch {
                preference.saveMapType(SharedPreference.MapType.DEFAULT)
            }
            bottomSheet.dismiss()
        }

        bottomSheetView.findViewById<MaterialCardView>(R.id.nightMap).setOnClickListener {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, SharedPreference.MapType.NIGHT.value))
            lifecycleScope.launch {
                preference.saveMapType(SharedPreference.MapType.NIGHT)
            }
            bottomSheet.dismiss()
        }

        bottomSheetView.findViewById<MaterialCardView>(R.id.RetroMap).setOnClickListener {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, SharedPreference.MapType.RETRO.value))
            lifecycleScope.launch {
                preference.saveMapType(SharedPreference.MapType.RETRO)
            }
            bottomSheet.dismiss()
        }

        bottomSheet.setContentView(bottomSheetView)
        bottomSheet.show()
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