package com.example.sharingapp.view.detail



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sharingapp.R
import com.example.sharingapp.databinding.ActivityDetailBinding
import com.example.sharingapp.responses.Story
import com.example.sharingapp.setting.formatElapsedTime

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val story = intent.getParcelableExtra<Story>("story")

        story?.let {
            binding.nameTextView.text = it.name
            binding.descriptionTextView.text = it.description
            binding.createdAtTextView.text = formatElapsedTime(it.createdAt, this)
            binding.latLonTextView.text = "${it.lat}, ${it.lon}"

            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.imageView)
        } ?: run {
            Toast.makeText(this, getString(R.string.errorDetail), Toast.LENGTH_SHORT).show()
        }
    }
}