package com.example.sharingapp.view.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.R
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.camera.CameraXActivity
import com.example.sharingapp.databinding.ActivityAddStoryBinding
import com.example.sharingapp.responses.AddResponse
import com.example.sharingapp.setting.*
import com.example.sharingapp.view.main.MainActivity
import java.io.*

@Suppress("DEPRECATION")
class AddStoryActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityAddStoryBinding
    private var getFile : File? = null
    private lateinit var viewModel: StoryViewModel
    private val FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    private lateinit var locationManager: LocationManager



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.noPerimission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val preference = SharedPreference.getInstance(dataStore)
        viewModel =
            ViewModelProvider(this, ViewModelFactory(preference, ApiConfig.getApiService()))[StoryViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION_PERMISSION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(FINE_LOCATION_PERMISSION), REQUEST_CODE_PERMISSIONS)
        }


        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.galleryButton.setOnClickListener {
            launcherIntentGallery.launch(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                    getString(R.string.selectPicture)
                )
            )
        }

        binding.shareLoc.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(this, FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(FINE_LOCATION_PERMISSION), REQUEST_CODE_PERMISSIONS)
                }
            } else {
                locationManager.removeUpdates(locationListener)
            }
        }

        binding.postButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            val file = getFile
            var latitude: Float? = null
            var longitude: Float? = null

            if (binding.shareLoc.isChecked) {
                if (ContextCompat.checkSelfPermission(this, FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    latitude = location?.latitude?.toFloat()
                    longitude = location?.longitude?.toFloat()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(FINE_LOCATION_PERMISSION), REQUEST_CODE_PERMISSIONS)
                    return@setOnClickListener
                }
            }

            if (file != null && description.isNotEmpty()) {
                val compressedFile = reduceFileImage(file)
                viewModel.store(
                    object : OnSuccessCallback<AddResponse> {
                        override fun onSuccess(message: AddResponse) {
                            Toast.makeText(this@AddStoryActivity, message.message, Toast.LENGTH_SHORT).show()
                            setResult(MainActivity.INTENT_ADD_STORY)
                            finish()
                        }
                    },
                    description,
                    compressedFile,
                    latitude,
                    longitude
                )
            } else {
                Toast.makeText(this, getString(R.string.fillField), Toast.LENGTH_SHORT).show()
            }
        }






        viewModel.isLoading.observe(this) {
           loadings ->
            binding.loading.visibility = if (loadings) View.VISIBLE else View.GONE
            binding.cameraButton.isEnabled = !loadings
            binding.galleryButton.isEnabled = !loadings
            binding.postButton.isEnabled = !loadings
            binding.descriptionEditText.isEnabled = !loadings
        }

        viewModel.error.observe(this) {
            error -> error.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }

        }


    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude
            // Lakukan sesuatu dengan latitude dan longitude, misalnya mengirim ke server
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {}
    }





    private fun startCamera() {
        val cameraXIntent = Intent(this, CameraXActivity::class.java)
        launcherIntentCameraX.launch(cameraXIntent)

    }


    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK){
            val imageResult = uriToFile(this, it.data?.data as Uri)
            getFile = imageResult
            binding.previewImage.setImageURI(it.data?.data)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(BitmapFactory.decodeFile(getFile?.path), isBackCamera)

            val matrix = Matrix()
            matrix.postRotate(if (isBackCamera) 270f else 270f)
            val rotatedResult = Bitmap.createBitmap(result, 0, 0, result.width, result.height, matrix, true)
            binding.previewImage.setImageBitmap(rotatedResult)

            result.recycle()
        }
    }





    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }




}