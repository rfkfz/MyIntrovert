package com.example.myintrovert.service

import com.google.gson.annotations.SerializedName

data class NotesPayload(
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String // Correct the property name to "summary"
)
