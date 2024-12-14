package com.example.siplagmovil.data.model

import android.content.SharedPreferences
import android.util.Log
import com.example.siplagmovil.data.request.LoginRequest
import com.example.siplagmovil.data.response.LoginResponse
import java.io.IOException
import com.example.siplagmovil.data.network.ApiService // Import the ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.siplagmovil.data.model.local.SharedPreferencesManager

class AuthRepository(
    private val authApi: ApiService,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        val loginRequest = LoginRequest(email, password)

        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.login(loginRequest)
                //Log.d("AuthRepository", "respuesta de authApi $response") TESTEANDO
                if (response.isSuccessful) {
                    val responseBody = response.body()!!

                    // Store the JWT token using SharedPreferencesManager
                    responseBody.token?.let {
                        sharedPreferencesManager.saveToken(it)  // Only save if the token is non-null
                        //Log.d("AuthRepository", "Token saved: $it")   TESTEANDO
                    } ?: throw Exception("Token is null")  // Optionally handle the null case


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
    // Retrieve the token using SharedPreferencesManager
    fun getToken(): String? {
        return sharedPreferencesManager.getToken()  //FUNCION DE PRUEBA
    }
}

