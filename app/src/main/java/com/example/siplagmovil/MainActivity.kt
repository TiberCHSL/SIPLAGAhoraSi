package com.example.siplagmovil

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.siplagmovil.data.model.AuthRepository
import com.example.siplagmovil.data.model.local.SharedPreferencesManager
import com.example.siplagmovil.data.network.RetrofitInstance
import com.example.siplagmovil.domain.LoginUseCase
import com.example.siplagmovil.ui.login.LoginScreen
import com.example.siplagmovil.ui.login.LoginViewModel
import com.example.siplagmovil.ui.theme.SIPLAGMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)

        val authRepository = AuthRepository(RetrofitInstance.api, sharedPreferencesManager)


        // Create an instance of LoginUseCase using the AuthRepository
        val loginUseCase = LoginUseCase(authRepository)

        // Create an instance of LoginViewModel and pass LoginUseCase
        val loginViewModel = LoginViewModel(loginUseCase)

        setContent {
            SIPLAGMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { navigateToImagesActivity() })
                }
            }
        }

    }
    private fun navigateToImagesActivity() {
        val intent = Intent(this, ImagesActivity::class.java)
        startActivity(intent)
        finish() // Prevent going back to the login screen
    }
}


//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//Text(
//text = "Hello $name!",
//modifier = modifier
//)
//}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//SIPLAGMovilTheme {
//Greeting("Android")
//}
//}

//Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//Greeting(
//name = "Android",
//modifier = Modifier.padding(innerPadding)
//)
//}