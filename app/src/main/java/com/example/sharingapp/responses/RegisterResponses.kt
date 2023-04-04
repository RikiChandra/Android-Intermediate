package com.example.sharingapp.responses

import com.google.gson.annotations.SerializedName

data class RegisterResponses(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,


)
