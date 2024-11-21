// HomeScreen.kt
package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mensaje de bienvenida
        Text(text = "Welcome to Quizec!")

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para crear un nuevo cuestionario
        Button(
            onClick = {
                // Navegar a una pantalla de creación de cuestionarios (a implementar)
                navController.navigate("createQuiz")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Quiz")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ver el historial de cuestionarios
        Button(
            onClick = {
                // Navegar a una pantalla para ver cuestionarios (a implementar)
                navController.navigate("quizHistory")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("My Quiz History")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cerrar sesión (puedes conectarlo con FirebaseAuth más adelante)
        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true } // Navegar a Login y limpiar pila
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
