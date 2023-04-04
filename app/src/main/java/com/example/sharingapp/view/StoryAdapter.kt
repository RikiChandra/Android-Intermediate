package com.example.sharingapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharingapp.databinding.CardMenuBinding
import com.example.sharingapp.responses.Story

class StoryAdapter(private val context: Context) : ListAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardMenuBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    inner class ViewHolder(private val binding : CardMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.title.text = story.name
            binding.desc.text = story.createdAt

            Glide.with(context)
                .load(story.photoUrl)
                .into(binding.img)
        }
    }
}

