package com.example.myintrovert.service

import com.google.gson.annotations.SerializedName

data class NotesResponse(
    @SerializedName("response") val success: Boolean,
    @SerializedName("payload") val payload: NotesPayload
)
