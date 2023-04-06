package com.example.sharingapp.view.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.sharingapp.camera.CameraXActivity
import com.example.sharingapp.databinding.ActivityAddStoryBinding
import com.example.sharingapp.responses.AddResponse
import com.example.sharingapp.responses.StoryResponses
import com.example.sharingapp.setting.*
import com.example.sharingapp.view.main.MainActivity
import java.io.*

class AddStoryActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var photoPath: String

    private var getFile : File? = null

    private lateinit var viewModel: StoryViewModel

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

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
                    "Tidak mendapatkan permission.",
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
            ViewModelProvider(this, ViewModelFactory(preference, this))[StoryViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.galleryButton.setOnClickListener {
            openGallery()
        }

        binding.postButton.setOnClickListener {
            Log.d("AddStoryActivity", "Post button clicked")
            if (getFile != null && !TextUtils.isEmpty(binding.descriptionEditText.text.toString())) {
                val compressedFile = reduceFileImage(getFile!!)
                if (compressedFile != null) {
                    viewModel.post(
                        onSuccessCallback = object : OnSuccessCallback<AddResponse> {
                            override fun onSuccess(response: AddResponse) {
                                // handle success
                                Toast.makeText(
                                    this@AddStoryActivity,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                setResult(MainActivity.INTENT_ADD_STORY)
                                finish()
                            }
                        },
                        description = binding.descriptionEditText.text.toString(),
                        photo = compressedFile,
                        lat = null, // or pass your desired value
                        lon = null // or pass your desired value
                    )
                } else {
                    Toast.makeText(this, "Gagal memampatkan file gambar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Isi semua form", Toast.LENGTH_SHORT).show()
            }
        }




        viewModel.isLoading.observe(this) {
           binding.loading.visibility = if (it) android.view.View.VISIBLE else android.view.View.GONE
        }


    }



    private fun startCamera() {
        val cameraXIntent = Intent(this, CameraXActivity::class.java)
        launcherIntentCameraX.launch(cameraXIntent)

    }

    private fun openGallery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile

            binding.previewImage.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.previewImage.setImageBitmap(result)
        }
    }




}