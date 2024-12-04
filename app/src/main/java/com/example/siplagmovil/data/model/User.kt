package com.example.siplagmovil.data.model



data class User(    // Guarda los datos del usuario que retorna la API, vienen de LoginResponse
    val rut: Int,
    val correo: String,
    val nombres: String,
    val apellidos: String
)
