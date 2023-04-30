package com.example.sharingapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sharingapp.responses.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


object FakeDummyGeneratorData {

    fun getBearerToken() = "My Token"

    fun getFakeStories(): List<Story> {
        val items = arrayListOf<Story>()

        for (i in 0 until 10) {
            val story = Story(
                id = "My id",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1682585713309_zNJVuVng.jpg",
                createdAt = "2023-04-27T08:44:14.012Z",
                name = "Test2",
                description = "Saya menyerah bikin ini",
                lon = "7.2318384234",
                lat = "2.324234"
            )

            items.add(story)
        }

        return items
    }
}

