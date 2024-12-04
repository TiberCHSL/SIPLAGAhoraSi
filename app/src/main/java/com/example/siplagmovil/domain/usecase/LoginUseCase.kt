package com.example.siplagmovil.domain

import com.example.siplagmovil.data.model.AuthRepository
import com.example.siplagmovil.data.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginUseCase(private val authRepository: AuthRepository) {

    suspend fun execute(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val loginResponse = authRepository.login(email, password)

                // Check if the login was successful
                if (loginResponse.isSuccess) {
                    Result.success(Unit)  // Return success with no additional data
                } else {
                    Result.failure(Exception("Login failed"))
                }
            } catch (e: Exception) {
                // Handle exceptions such as network issues or JSON parsing errors
                Result.failure(e)
            }
        }
    }
}