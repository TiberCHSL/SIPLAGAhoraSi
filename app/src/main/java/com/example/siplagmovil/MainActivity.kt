package com.example.siplagmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.siplagmovil.data.model.AuthRepository
import com.example.siplagmovil.data.model.local.SharedPreferencesManager
import com.example.siplagmovil.data.network.RetrofitInstance
import com.example.siplagmovil.domain.LoginUseCase
import com.example.siplagmovil.ui.LoginScreen
import com.example.siplagmovil.ui.LoginViewModel
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
                    LoginScreen(loginViewModel)
                }
            }
        }
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