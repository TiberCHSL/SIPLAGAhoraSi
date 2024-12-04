package com.example.siplagmovil.data.response

// Data class to handle the response from the API
data class LoginResponse(       // Recibe respuesta de la API
    val success: Boolean,       // Indicates if the login was successful
    val message: String,        // API response message
    val token: String? = null   // JWT token returned by the API upon successful login
)
