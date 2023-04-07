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
import android.view.View
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
    private var getFile : File? = null
    private lateinit var viewModel: StoryViewModel


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
            launcherIntentGallery.launch(
                Intent.createChooser(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                    "Select Picture"
                )
            )
        }

        binding.postButton.setOnClickListener {
            if (getFile != null && !TextUtils.isEmpty((binding.descriptionEditText.text.toString()))){
                val comperssedFile = reduceFileImage(getFile!!)

                viewModel.store(
                    object : OnSuccessCallback<AddResponse> {
                        override fun onSuccess(message: AddResponse) {
                            Toast.makeText(this@AddStoryActivity, message.message, Toast.LENGTH_SHORT).show()
                            setResult(MainActivity.INTENT_ADD_STORY)
                            finish()
                        }

                    },
                    binding.descriptionEditText.text.toString(),
                    comperssedFile,
                    lat = null,
                    lon = null
                )

            } else {
                Toast.makeText(this, "Please fill all field", Toast.LENGTH_SHORT).show()
            }

        }




        viewModel.isLoading.observe(this) {
           isLoading ->
            binding.apply {
                loading.visibility = if (isLoading) View.VISIBLE else View.GONE
                cameraButton.isEnabled = !isLoading
                galleryButton.isEnabled = !isLoading
                postButton.isEnabled = !isLoading
                descriptionEditText.isEnabled = !isLoading
            }
        }

        viewModel.error.observe(this) {
            error -> error.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }

        }


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
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.previewImage.setImageBitmap(result)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }




}