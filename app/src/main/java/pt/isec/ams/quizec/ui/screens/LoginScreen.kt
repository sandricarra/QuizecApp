// LoginScreen.kt
package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.ui.viewmodel.LoginState
import pt.isec.ams.quizec.ui.viewmodel.LoginViewModel

// Composable que representa la pantalla de login
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val loginState by viewModel.loginState.observeAsState()
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Contenedor general con fondo
    Box(
        modifier = Modifier
            .fillMaxSize() // Asegura que ocupe todo el tamaño de la pantalla

    ) {
        Image(
            painter = painterResource(id = R.drawable.background), // Reemplaza con el ID de tu imagen
            contentDescription = null, // Imagen decorativa
            contentScale = ContentScale.Crop, // Escala para cubrir toda la pantalla
            modifier = Modifier.fillMaxSize() // La imagen ocupa toda la pantalla
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            // Campo de texto para el email
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(stringResource(R.string.email)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de texto para la contraseña
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de login
            Button(
                onClick = {
                    if (email.value.isBlank() || password.value.isBlank()) {
                        errorMessage = "Please fill in both fields."
                    } else {
                        errorMessage = null
                        viewModel.login(email.value, password.value)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.login))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Muestra errores de login
            when (val state = loginState) {
                is LoginState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red
                    )
                }
                else -> {}
            }

            if (loginState is LoginState.Success) {
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                    viewModel.resetState()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón para registro
            Button(
                onClick = {
                    navController.navigate("register")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.register))
            }
        }
    }
}
