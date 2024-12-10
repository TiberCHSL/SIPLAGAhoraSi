package com.example.siplagmovil.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.siplagmovil.R
import kotlinx.coroutines.launch

@Composable

fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center), viewModel, onLoginSuccess)

    }
}

@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel, onLoginSuccess: () -> Unit) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val loginResult: Boolean? by viewModel.loginResult.observeAsState() // Observe login result
    val coroutineScope = rememberCoroutineScope()

    // Show loading indicator if login is in progress
    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(16.dp))
            EmailField(email) { viewModel.onLoginChange(it, password) }
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) { viewModel.onLoginChange(email, it) }
            Spacer(modifier = Modifier.padding(8.dp))
            ForgotPassword(Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.padding(16.dp))
            LoginButton(loginEnable) {
                coroutineScope.launch {
                    viewModel.onLoginSelected()
                }
            }
        }

        // Observe the login result and show a success or failure message
        loginResult?.let { success ->
            if (success) {
                // Show a success message
                SuccessMessage()
                onLoginSuccess()
            } else {
                // Show an error message
                ErrorMessage()
            }
        }
    }
}


@Composable
fun LoginButton(loginEnable: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() }, modifier = Modifier
            .fillMaxWidth()
            .height(48.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0A5EB0),
            disabledContainerColor = Color(0xFF0A97B0),
            contentColor = Color.White,
            disabledContentColor = Color.White,
        ), enabled = loginEnable
    ) {
        Text(text = "Iniciar sesión")
    }
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = "Olvidaste la contraseña?",
        modifier = modifier.clickable { },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0A5EB0)
    )
}

@Composable
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = password, onValueChange = { onTextFieldChanged(it) },
        placeholder = { Text(text = "Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color(0xFF636262),
            unfocusedTextColor = Color(0xFF636262),
            focusedContainerColor = Color(0xFFDEDDDD),
            unfocusedContainerColor = Color(0xFFDEDDDD),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )

    )
}

@Composable
fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {

    TextField(
        value = email,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Correo") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color(0xFF636262),
            unfocusedTextColor = Color(0xFF636262),
            focusedContainerColor = Color(0xFFDEDDDD),
            unfocusedContainerColor = Color(0xFFDEDDDD),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )

}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.gobierno),
        contentDescription = "HEADER",
        modifier = modifier
    )
}

@Composable
fun SuccessMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Inicio de sesión exitoso!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
    }
}

@Composable
fun ErrorMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Correo o contraseña inválidos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
    }
}

