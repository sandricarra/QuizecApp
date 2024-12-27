package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.data.models.UserRole
import pt.isec.ams.quizec.ui.viewmodel.RegisterState
import pt.isec.ams.quizec.ui.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    var role by remember { mutableStateOf<UserRole>(UserRole.STUDENT) }
    var isDropdownOpen by remember { mutableStateOf(false) }
    val registerState by viewModel.registerState.observeAsState()
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
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD) // Azul clarito
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD) // Azul clarito
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD) // Azul clarito
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Button(
            onClick = { isDropdownOpen = !isDropdownOpen },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Select Role", color = Color.White)
        }

        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { isDropdownOpen = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenuItem(
                onClick = { role = UserRole.STUDENT; isDropdownOpen = false },
                text = { Text("Student\uD83E\uDDD1\uD83C\uDFFB\u200D\uD83C\uDF93") }
            )
            DropdownMenuItem(
                onClick = { role = UserRole.TEACHER; isDropdownOpen = false },
                text = { Text("Teacher\uD83E\uDDD1\uD83C\uDFFB\u200D\uD83C\uDFEB") }
            )
        }

        Text(
            text = "Selected Role: ${role.name}",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Button(
            onClick = {
                viewModel.register(
                    email.value,
                    password.value,
                    name.value,
                    UserRole.valueOf(role.name)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Register", color = Color.White)
        }

        errorMessage.value?.let { message ->
            Snackbar(
                modifier = Modifier.padding(8.dp),
                action = {}
            ) {
                Text(text = message)
            }
        }
    }
}








