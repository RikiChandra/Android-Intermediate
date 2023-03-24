package com.example.sharingapp.responses

import com.google.gson.annotations.SerializedName

data class LoginResponses(
    @field:SerializedName("loginResult")
    val loginResult: loginResult,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Boolean

)

data class loginResult(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("token")
    val token: String,
)

