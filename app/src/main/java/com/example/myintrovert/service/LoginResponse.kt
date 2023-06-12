package com.example.myintrovert.service

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("response") val success: Boolean
)
