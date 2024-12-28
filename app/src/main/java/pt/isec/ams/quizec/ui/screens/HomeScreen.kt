package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pt.isec.ams.quizec.R


@Composable
fun HomeScreen(navController: NavController, creatorId: String) {

   Box(
       modifier = Modifier.fillMaxSize()
   ){
       Image(
           painter = painterResource(id = R.drawable.background), // Tu imagen de fondo
           contentDescription = null, // Descripción (opcional)
           contentScale = ContentScale.Crop, // Ajusta la imagen al tamaño de la pantalla
           modifier = Modifier.fillMaxSize() // La imagen debe ocupar toda la pantalla
       )


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
                text = stringResource(id = R.string.welcome_message), // Texto de bienvenida.
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
                text = stringResource(id = R.string.homepage_title), // Título de la página de inicio.
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
                    stringResource(id = R.string.create_quiz_button), // Texto dentro del botón.
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
                    stringResource(id = R.string.join_quiz_button), // Texto dentro del botón.
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
                    stringResource(id = R.string.quiz_history_button), // Texto dentro del botón.
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
                    stringResource(id = R.string.manage_quiz_button), // Texto dentro del botón.
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary) // Color rojo para el botón de logout.
            ) {
                Text(
                    stringResource(id = R.string.logout_button), // Texto dentro del botón", // Texto dentro del botón.
                    color = Color.White, // Texto blanco.
                    style = MaterialTheme.typography.titleMedium // Estilo de texto del botón.
                )
            }
        }



    }
}}


