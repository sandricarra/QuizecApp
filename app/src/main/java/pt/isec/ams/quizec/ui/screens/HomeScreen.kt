package pt.isec.ams.quizec.ui.screens



import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext




import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.data.models.QuizStatus
import pt.isec.ams.quizec.viewmodel.HomeScreenViewModel





@Composable
fun HomeScreen(navController: NavController, creatorId: String,viewModel: HomeScreenViewModel) {

    val context = LocalContext.current

    // Estado para manejar el estado del cuestionario
   var userid by remember { mutableStateOf("") }
    var participants by remember { mutableStateOf(listOf<String>()) }

    // Lista de participantes en espera




    // LazyColumn para mostrar una lista desplazable de elementos.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // Asegura que ocupe todo el tamaño disponible.
            .padding(16.dp), // Añade padding alrededor de la lista.
        horizontalAlignment = Alignment.CenterHorizontally // Centra los elementos horizontalmente.
    ) {
        // Primer item de la lista: logo de la aplicación.
        item {
            Image(
                painter = painterResource(id = R.drawable.ic_logo), // Carga la imagen del logo.
                contentDescription = null, // Descripción de la imagen (para accesibilidad).
                modifier = Modifier
                    .fillMaxWidth() // La imagen ocupa todo el ancho disponible.
                    .height(200.dp) // Establece la altura de la imagen.
                    .padding(bottom = 32.dp) // Padding inferior para separar de los elementos siguientes.
            )
        }

        // Segundo item de la lista: mensaje de bienvenida.
        item {
            Text(
                text = "Welcome to Quizec!", // Texto de bienvenida.
                color = Color(0xFF1E88E5), // Cambia el color a un tono azul más vibrante.
                fontSize = 32.sp, // Tamaño de fuente más grande.
                fontWeight = FontWeight.ExtraBold, // Negrita extra para mayor énfasis.
                textAlign = TextAlign.Center, // Alineación centrada.
                modifier = Modifier
                    .fillMaxWidth() // Asegura que el texto ocupe todo el ancho.
                    .padding(vertical = 8.dp) // Añade padding alrededor del texto.
            )
        }

        // Tercer item de la lista: título "HOMEPAGE".
        item {
            Text(
                text = "HOMEPAGE", // Título de la página de inicio.
                style = MaterialTheme.typography.titleLarge.copy(
                    // Uso de estilo más grande.
                    fontWeight = FontWeight.Bold, // Negrita para mayor impacto.
                    color = Color(0xFF6200EE), // Tono morado para el encabezado.
                ),
                modifier = Modifier
                    .padding(vertical = 24.dp) // Padding para separar del contenido superior e inferior.
            )
        }

        // Cuarto item de la lista: botón para crear un cuestionario.
        item {
            Button(
                onClick = { navController.navigate("quizCreation") }, // Navega a la pantalla de creación de cuestionarios.
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho disponible.
                    .padding(vertical = 12.dp), // Añade un padding vertical más grande.
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Cambia el color de fondo del botón.
            ) {
                Text(
                    "Create a Quiz", // Texto dentro del botón.
                    color = Color.White, // Color de texto blanco para contraste.
                    style = MaterialTheme.typography.titleMedium // Estilo de texto del botón.
                )
            }
        }

        // Quinto item de la lista: botón para participar en un cuestionario (pasando un quizId).
        item {
            val quizId = "someQuizId"  // Este debe ser el ID del cuestionario que quieras pasar.
            Button(
                onClick = {
                    // Navega a la pantalla del cuestionario pasando quizId como parámetro en la URL.
                    navController.navigate("quizAccessScreen/$quizId")
                },
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho disponible.
                    .padding(vertical = 12.dp), // Añade un padding vertical más grande.
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6)) // Cambia el color del botón a verde-agua.
            ) {
                Text(
                    "Participate in a Quiz", // Texto dentro del botón.
                    color = Color.White, // Texto blanco.
                    style = MaterialTheme.typography.titleMedium // Estilo de texto del botón.
                )
            }
        }

        // Sexto item de la lista: botón para acceder al historial de cuestionarios.
        item {
            Button(
                onClick = {
                    navController.navigate("quizHistory/$creatorId")
                    // Navega a la pantalla de historial de cuestionarios.
                },
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho disponible.
                    .padding(vertical = 12.dp), // Añade un padding vertical más grande.
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E)) // Color gris para el botón.
            ) {
                Text(
                    "My Quiz History", // Texto dentro del botón.
                    color = Color.White, // Texto blanco.
                    style = MaterialTheme.typography.titleMedium // Estilo de texto del botón.
                )
            }
        }
        item {
            Button(
                onClick = { navController.navigate("manageQuiz") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Cambia el color de fondo del botón.
            ) {
                Text(
                    "Manage Quiz", // Texto dentro del botón.
                    color = Color.White, // Color de texto blanco para contraste.
                    style = MaterialTheme.typography.titleMedium // Estilo de texto del botón.
                )
            }
        }


        // Séptimo item de la lista: botón para cerrar sesión.
        item {
            Button(
                onClick = {
                    navController.navigate("login") { // Navega a la pantalla de login.
                        popUpTo("home") {
                            inclusive = true
                        } // Remueve todas las pantallas anteriores en la pila, excepto la de login.
                    }
                },
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho disponible.
                    .padding(vertical = 12.dp), // Añade un padding vertical más grande.
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Color rojo para el botón de logout.
            ) {
                Text(
                    "Logout", // Texto dentro del botón.
                    color = Color.White, // Texto blanco.
                    style = MaterialTheme.typography.titleMedium // Estilo de texto del botón.
                )
            }
        }


        item {
            if (participants.isNotEmpty()) {
                Text(
                    text = "Participants Waiting:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                participants.forEach { participant ->
                    Text(
                        text = participant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}


