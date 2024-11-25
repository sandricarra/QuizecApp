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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.data.models.UserRole
import pt.isec.ams.quizec.viewmodel.RegisterState
import pt.isec.ams.quizec.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    var role by remember { mutableStateOf<UserRole>(UserRole.STUDENT) } // Valor por defecto
    var isDropdownOpen by remember { mutableStateOf(false) }

    val registerState by viewModel.registerState.observeAsState()

    // Estado para controlar el mensaje de error
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }

            is RegisterState.Error -> {
                errorMessage.value = (registerState as RegisterState.Error).message
            }

            else -> Unit
        }

        viewModel.resetRegisterState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.baseline_quiz_24), contentDescription = null)

        Spacer(modifier = Modifier.height(16.dp))


        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown para seleccionar el rol
        Button(
            onClick = { isDropdownOpen = !isDropdownOpen },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Role")
        }
        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { isDropdownOpen = false }
        ) {
            // Opción para el rol de Student
            DropdownMenuItem(
                onClick = { role = UserRole.STUDENT; isDropdownOpen = false },
                text = { Text("Student") }
            )

            // Opción para el rol de Teacher
            DropdownMenuItem(
                onClick = { role = UserRole.TEACHER; isDropdownOpen = false },
                text = { Text("Teacher") }
            )
        }

        // Mostrar el rol seleccionado
        Text("Selected Role: ${role.name}")



        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.register(
                    email.value,
                    password.value,
                    name.value,
                    UserRole.valueOf(role.name)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        errorMessage.value?.let { message ->
            Snackbar {
                Text(text = message)
            }
        }
    }
}



