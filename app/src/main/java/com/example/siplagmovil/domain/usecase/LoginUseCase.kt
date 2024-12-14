package com.example.siplagmovil.domain

import android.util.Log
import com.example.siplagmovil.data.model.AuthRepository
import com.example.siplagmovil.data.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginUseCase(private val authRepository : AuthRepository) { //Cambiar authRepository public a private despues de testear

    suspend fun execute(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val loginResponse = authRepository.login(email, password)
                //Log.d("LoginUseCase", "Respuesta del usecase: $loginResponse") ERA PARA TESTEO

                if (loginResponse.isSuccess) {
                    // Check if the token is saved after successful login
                    val token = authRepository.getToken()  // Retrieve the saved token
                    //Log.d("LoginUseCase", "Token after login: $token")

                    if (!token.isNullOrEmpty()) {
                        Result.success(Unit)  // Login successful, and token saved
                    } else {
                        Result.failure(Exception("Token not saved after login"))
                    }
                } else {
                    Result.failure(Exception("Login failed: ${loginResponse.exceptionOrNull()?.localizedMessage}"))
                }
            } catch (e: Exception) {
                // Handle any exceptions (network issues, parsing errors, etc.)
                Result.failure(e)
            }
        }
    }
}