package com.example.siplagmovil.data.network

import com.example.siplagmovil.data.request.LoginRequest
import com.example.siplagmovil.data.response.LoginResponse
import com.example.siplagmovil.data.response.UploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService { //, Esta solicitud POST es para iniciar sesión en el sistema
    @POST("api/login") // Adjust the endpoint if necessary
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>


// New function for image upload
@Multipart
@POST("api/upload-image") // Replace with your endpoint
suspend fun uploadImage(
    @Header("Authorization") token: String, // JWT Token
    @Part file: MultipartBody.Part
): Response<UploadResponse> // Define a response model for this request

}
