package com.example.siplagmovil.ui

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siplagmovil.domain.LoginUseCase
import com.example.siplagmovil.data.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Called when the user changes their email or password
    fun onLoginChange(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    // Simple validation checks for email and password
    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean = password.length >= 2

    // Perform login and handle result
    fun onLoginSelected() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = loginUseCase.execute(email.value ?: "", password.value ?: "")

            _isLoading.value = false

            if (result.isSuccess) {
                _loginResult.value = true
            } else {
                _loginResult.value = false
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Login failed"
            }
        }
    }
}
