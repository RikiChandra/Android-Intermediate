package com.example.sharingapp.view.story

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharingapp.R
import com.example.sharingapp.databinding.CardMenuBinding
import com.example.sharingapp.responses.Story
import com.example.sharingapp.setting.formatElapsedTime
import com.example.sharingapp.view.detail.DetailActivity

class StoryAdapter(private val context: Context) : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(
    DIFF_CALLBACK
) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
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
        val context = holder.itemView.context

        story?.let {
            holder.binding.title.text = story.name
            holder.binding.createdAt.text = formatElapsedTime(story.createdAt, context)


            Glide.with(holder.itemView.context)
                .load(story.photoUrl)
                .into(holder.binding.img)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("story", story)

                val optionCompat : ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        holder.itemView.context as Activity,
                        Pair(
                            holder.binding.img,
                            context.getString(R.string.transitionImage)),

                        Pair(holder.binding.title,
                            context.getString(R.string.name)),

                        Pair(holder.binding.createdAt,
                            context.getString(R.string.createdAt)),
                    )


                context.startActivity(intent, optionCompat.toBundle())
            }

        }





    }

    inner class ViewHolder(val binding : CardMenuBinding) : RecyclerView.ViewHolder(binding.root)
}

