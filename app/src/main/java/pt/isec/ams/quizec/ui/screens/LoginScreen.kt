// LoginScreen.kt
package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.isec.ams.quizec.viewmodel.LoginViewModel
import pt.isec.ams.quizec.viewmodel.LoginState

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {

    // Usamos el remember para mantener el estado del TextField (campos email y password)
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Observa el estado de autenticación del ViewModel
    val loginState by viewModel.loginState.observeAsState()

    // Configuración de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Campo texto para email
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo texto para contraseña
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            // Para ocultar el texto registrado (contraseña)
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón login
        Button(
            onClick = {
                viewModel.login(email.value, password.value)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar mensaje en función del estado de autenticación
        when (loginState) {
            is LoginState.Success -> {
                Text(text = "Login exitoso", color = androidx.compose.ui.graphics.Color.Green)
                // Navegar a la pantalla principal o mostrar un mensaje de bienvenida
            }
            is LoginState.Error -> {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            else -> {
                // No hacer nada cuando el estado es nulo
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón register
        Button(
            // Redirige a la pantalla de registro
            onClick = {
                // Aquí puedes navegar a la pantalla de registro
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

