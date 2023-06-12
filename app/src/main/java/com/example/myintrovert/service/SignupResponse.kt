package com.example.myintrovert.service

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("response") val success: Boolean,
    @SerializedName("payload") val payload: SignupPayload
)
