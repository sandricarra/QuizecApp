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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
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
import pt.isec.ams.quizec.viewmodel.RegisterState
import pt.isec.ams.quizec.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    // Estados para manejar los campos de entrada del formulario
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }

    // Estado para manejar el rol seleccionado (por defecto, Student)
    var role by remember { mutableStateOf<UserRole>(UserRole.STUDENT) }

    // Estado para controlar la visibilidad del DropdownMenu
    var isDropdownOpen by remember { mutableStateOf(false) }

    // Observamos el estado de registro desde el ViewModel
    val registerState by viewModel.registerState.observeAsState()

    // Estado para controlar el mensaje de error
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Efecto que maneja la navegación y la presentación de mensajes de error
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                // Si el registro es exitoso, redirige al login y cierra la pantalla de registro
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }

            is RegisterState.Error -> {
                // Si hay un error, muestra el mensaje de error
                errorMessage.value = (registerState as RegisterState.Error).message
            }

            else -> Unit
        }

        // Reinicia el estado del registro después de la navegación
        viewModel.resetRegisterState()
    }

    // Diseño de la pantalla de registro
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Espaciado alrededor de toda la columna
        horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente
        verticalArrangement = Arrangement.Center // Centra verticalmente
    ) {
        // Muestra el logo en la parte superior con un tamaño adecuado
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp) // Espaciado entre el logo y el formulario
        )

        // Campo para ingresar el nombre
        TextField(
            value = name.value,
            onValueChange = { name.value = it }, // Actualiza el nombre
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp) // Espaciado entre los campos
        )

        // Campo para ingresar el correo electrónico
        TextField(
            value = email.value,
            onValueChange = { email.value = it }, // Actualiza el correo electrónico
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp) // Espaciado entre los campos
        )

        // Campo para ingresar la contraseña, con transformación visual para ocultarla
        TextField(
            value = password.value,
            onValueChange = { password.value = it }, // Actualiza la contraseña
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), // Oculta la contraseña
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp) // Espaciado entre los campos
        )

        // Botón para abrir el DropdownMenu y seleccionar el rol
        Button(
            onClick = { isDropdownOpen = !isDropdownOpen },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp), // Espaciado entre el botón y el siguiente campo
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Color de fondo
        ) {
            Text("Select Role", color = Color.White) // Texto blanco para el botón
        }

        // Menú desplegable para seleccionar el rol (Student o Teacher)
        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = { isDropdownOpen = false }, // Cierra el menú si se toca fuera
            modifier = Modifier.fillMaxWidth() // Asegura que el DropdownMenu tenga el mismo ancho que el botón
        ) {
            // Opción para seleccionar el rol de Student
            DropdownMenuItem(
                onClick = { role = UserRole.STUDENT; isDropdownOpen = false },
                text = { Text("Student") }
            )

            // Opción para seleccionar el rol de Teacher
            DropdownMenuItem(
                onClick = { role = UserRole.TEACHER; isDropdownOpen = false },
                text = { Text("Teacher") }
            )
        }

        // Muestra el rol seleccionado con un diseño más estilizado
        Text(
            text = "Selected Role: ${role.name}",
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Centra el texto horizontalmente
                .padding(vertical = 8.dp), // Espaciado alrededor del texto
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray // Color gris para diferenciarlo del resto del texto
        )


        // Botón para enviar el formulario y registrar al usuario
        Button(
            onClick = {
                // Llama a la función de registro en el ViewModel
                viewModel.register(
                    email.value,
                    password.value,
                    name.value,
                    UserRole.valueOf(role.name) // Convierte el rol a UserRole
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp), // Espaciado antes del mensaje de error
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Color de fondo
        ) {
            Text("Register", color = Color.White) // Texto blanco para el botón
        }

        // Si hay un mensaje de error, lo muestra en un Snackbar
        errorMessage.value?.let { message ->
            Snackbar(
                modifier = Modifier.padding(8.dp), // Añadir padding alrededor del Snackbar
                action = {
                    // Acciones del Snackbar (si las hay)
                }
            ) {
                Text(text = message)
            }
        }
    }
}
