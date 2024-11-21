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
import androidx.navigation.NavController
import pt.isec.ams.quizec.viewmodel.RegisterViewModel
import pt.isec.ams.quizec.viewmodel.RegisterState

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Observar el estado de registro
    val registerState by viewModel.registerState.observeAsState()

    // Usar LaunchedEffect para manejar la navegación basándonos en el estado de registro
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                // Navegar a la pantalla de inicio de sesión y limpiar la pila
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is RegisterState.Error -> {
                // Manejar el error si es necesario (puedes mostrar un mensaje o realizar otra acción)
            }
            else -> Unit // No hacer nada si el estado es nulo
        }
    }

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
