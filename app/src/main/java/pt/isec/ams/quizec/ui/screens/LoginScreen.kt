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
import androidx.navigation.NavController
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.viewmodel.LoginViewModel
import pt.isec.ams.quizec.viewmodel.LoginState
import pt.isec.ams.quizec.data.models.Question






@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Observa el estado de autenticación del ViewModel
    val loginState by viewModel.loginState.observeAsState()

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
                viewModel.login(email.value, password.value)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Manejar el estado de autenticación
        when (loginState) {
            is LoginState.Error -> {
                Text(
                    text = (loginState as LoginState.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            else -> {
                // No hacer nada
            }
        }

        // Usar LaunchedEffect para manejar la navegación
        if (loginState is LoginState.Success) {
            LaunchedEffect(Unit) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
                // Restablecer el estado a Idle después de la navegación
                viewModel.resetState()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("register")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}
@Composable
fun MultipleChoiceQuestion(
    question: Question,
    onOptionsChanged: (List<String>) -> Unit
) {
    var options by remember { mutableStateOf(question.options) }

    Column {
        Text(text = question.questionText)

        options.forEachIndexed { index, option ->
            TextField(
                value = option,
                onValueChange = {
                    options = options.toMutableList().apply { set(index, it) }
                    onOptionsChanged(options)
                },
                label = { Text("Opción ${index + 1}") }
            )
        }
    }
}

