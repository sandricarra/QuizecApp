package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.viewmodel.RegisterState
import pt.isec.ams.quizec.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Observar el estado de registro
    val registerState by viewModel.registerState.observeAsState()



        when (registerState) {
            is RegisterState.Success -> {
                // Navegar a la pantalla de inicio de sesión y limpiar la pila
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is RegisterState.Error -> {
                val errorMessage = (registerState as RegisterState.Error).message
                // Mostrar mensaje de error
                Snackbar { Text(text = errorMessage) }
            }
            else -> Unit // No hacer nada si el estado es nulo
        }

    Image(painter = painterResource(id = R.drawable.ic_logo), contentDescription = "Logo")
    // Contenido de la pantalla de registro
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.register(email.value, password.value)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar el estado de error si existe
        if (registerState is RegisterState.Error) {
            Text(
                text = (registerState as RegisterState.Error).message,
                color = androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}
