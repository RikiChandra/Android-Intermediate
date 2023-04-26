package com.example.sharingapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sharingapp.responses.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


object FakeDummyGeneratorData {

    fun getBearerToken() = "bearer token"

}

