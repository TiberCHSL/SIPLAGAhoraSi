package com.example.siplagmovil.data.model

import android.content.SharedPreferences
import com.example.siplagmovil.data.request.LoginRequest
import com.example.siplagmovil.data.response.LoginResponse
import java.io.IOException
import com.example.siplagmovil.data.network.ApiService // Import the ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authApi: ApiService,
    private val sharedPreferences: SharedPreferences
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        val loginRequest = LoginRequest(email, password)

        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.login(loginRequest)
                if (response.isSuccessful) {
                    val responseBody = response.body()!!

                    // Store the JWT token in SharedPreferences
                    saveToken(responseBody.token)

                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Error: ${response.message()}"))
                }
            } catch (e: IOException) {
                Result.failure(Exception("Network error: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Save the token to SharedPreferences
    private fun saveToken(token: String?) {
        token?.let {
            sharedPreferences.edit()
                .putString("JWT_TOKEN", it)
                .apply()
        }
    }
}
