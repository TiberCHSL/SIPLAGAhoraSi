package com.example.siplagmovil.data.network

import com.example.siplagmovil.data.request.LoginRequest
import com.example.siplagmovil.data.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface ApiService { //, Esta solicitud POST es para iniciar sesión en el sistema
    @POST("api/login") // Adjust the endpoint if necessary
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
