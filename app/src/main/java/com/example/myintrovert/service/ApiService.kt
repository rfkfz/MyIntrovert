package com.example.myintrovert.service

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {
    @FormUrlEncoded
    @POST("dummy/login_service.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("dummy/create.php")
    fun signup(
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("image") image: String
    ): Call<SignupResponse>

    @FormUrlEncoded
    @POST("dummy/notes.php") // Replace with your notes storage endpoint URL
    fun saveNote(
        @Field("title") title: String,
        @Field("summary") summary: String // Correct the parameter name to "summary"
    ): Call<NotesResponse>

    @GET("dummy/fetchNotes.php") // Replace with your notes retrieval endpoint URL
    fun getNotes(): Call<List<NotesPayload>>

    // Endpoint untuk upload gambar
    @Multipart
    @POST("dummy/create.php")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ResponseBody>
}
