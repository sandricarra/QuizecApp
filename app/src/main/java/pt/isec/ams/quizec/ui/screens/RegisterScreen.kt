package pt.isec.ams.quizec.ui.screens
import RegisterViewModel
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

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val registerState by viewModel.registerState.observeAsState()

    // Estado para controlar el mensaje de error
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Manejo de efectos secundarios (navegación y error)
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                // Navegar a la pantalla de inicio de sesión y limpiar la pila
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }

            is RegisterState.Error -> {
                errorMessage.value = (registerState as RegisterState.Error).message
            }

            else -> Unit // No hacer nada si el estado es nulo
        }

        // Reiniciar el estado de registro después de procesarlo
        viewModel.resetRegisterState()
    }

    // Contenido de la pantalla de registro
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.ic_logo), contentDescription = null)

        Spacer(modifier = Modifier.height(16.dp))

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

        // Mostrar mensaje de error si existe
        errorMessage.value?.let { message ->
            Snackbar {
                Text(text = message)
            }
        }
    }
}
