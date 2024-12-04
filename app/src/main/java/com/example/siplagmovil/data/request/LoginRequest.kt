package com.example.siplagmovil.data.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("Correo")
    val email: String,

    @SerializedName("Contrasena")
    val password: String
)
