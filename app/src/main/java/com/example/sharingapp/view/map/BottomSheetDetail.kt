package com.example.sharingapp.view.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sharingapp.databinding.ActivityBottomSheetDetailBinding

class BottomSheetDetail : AppCompatActivity() {

    private lateinit var binding: ActivityBottomSheetDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomSheetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}