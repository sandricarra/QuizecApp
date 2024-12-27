// LoginScreen.kt
package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.ui.viewmodel.LoginState
import pt.isec.ams.quizec.ui.viewmodel.LoginViewModel

// Composable que representa la pantalla de login
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {

    // Estado mutable para almacenar el email y la contraseña introducidos por el usuario
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Observa el estado de login del ViewModel para actualizar la UI según el estado
    val loginState by viewModel.loginState.observeAsState()

    // Muestra el logo de la aplicación
    Image(painter = painterResource(id = R.drawable.ic_logo), contentDescription = "Logo")

    // Contenedor de los elementos de la pantalla, alineado al centro
    Column(
        modifier = Modifier
            .fillMaxSize()  // Rellena toda la pantalla
            .padding(16.dp),  // Añade un margen alrededor
        horizontalAlignment = Alignment.CenterHorizontally,  // Centra los elementos horizontalmente
        verticalArrangement = Arrangement.Center  // Centra los elementos verticalmente
    ) {
        // Campo de texto para el email
        TextField(
            value = email.value,
            onValueChange = { email.value = it },  // Actualiza el estado cuando el usuario escribe
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()  // Asegura que el campo de texto ocupe todo el ancho disponible
        )

        // Espaciador para separar los campos
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de texto para la contraseña, usando una transformación visual para ocultar los caracteres
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),  // Oculta la contraseña
            modifier = Modifier.fillMaxWidth()
        )

        // Espaciador para separar el botón
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para realizar login
        Button(
            onClick = {
                // Llama a la función login del ViewModel con los datos proporcionados
                viewModel.login(email.value, password.value)
            },
            modifier = Modifier.fillMaxWidth()  // Asegura que el botón ocupe todo el ancho disponible
        ) {
            Text("Login")  // Texto dentro del botón
        }

        // Espaciador adicional entre el login y el mensaje de error (si lo hay)
        Spacer(modifier = Modifier.height(8.dp))

        // Muestra un mensaje de error si el estado de login es de error
        when (loginState) {
            is LoginState.Error -> {
                // Muestra el mensaje de error en rojo
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            else -> {
                // No hace nada si el estado no es de error
            }
        }

        // Si el login es exitoso, navega a la pantalla principal
        if (loginState is LoginState.Success) {
            LaunchedEffect(Unit) {
                // Realiza la navegación a la pantalla "home" y elimina la pantalla de login del stack
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
                // Restablece el estado del ViewModel a Idle después de la navegación
                viewModel.resetState()
            }
        }

        // Espaciador adicional antes del botón de registro
        Spacer(modifier = Modifier.height(8.dp))

        // Botón para navegar a la pantalla de registro
        Button(
            onClick = {
                // Navega a la pantalla de registro
                navController.navigate("register")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")  // Texto dentro del botón
        }
    }
}


