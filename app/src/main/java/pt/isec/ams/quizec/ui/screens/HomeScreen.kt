package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    ) {
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
                    color = MaterialTheme.colorScheme.tertiary, // Cambia el color a un tono azul más vibrante.
                    fontSize = 32.sp, // Tamaño de fuente más grande.
                    fontWeight = FontWeight.ExtraBold, // Negrita extra para mayor énfasis.
                    textAlign = TextAlign.Center, // Alineación centrada.
                    modifier = Modifier
                        .fillMaxWidth() // Asegura que el texto ocupe todo el ancho.
                        .padding(vertical = 8.dp) // Añade padding alrededor del texto.
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.navigate("quizCreation") },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(16.dp)
                            ), // Añadido borde
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            stringResource(id = R.string.create_quiz_button),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        onClick = {
                            val quizId = "someQuizId"
                            navController.navigate("quizAccessScreen/$quizId")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(16.dp)
                            ), // Añadido borde
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            stringResource(id = R.string.join_quiz_button),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.navigate("quizHistory/$creatorId") },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(16.dp)
                            ), // Añadido borde
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            stringResource(id = R.string.quiz_history_button),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        onClick = { navController.navigate("manageQuiz") },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(16.dp)
                            ), // Añadido borde
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            stringResource(id = R.string.manage_quiz_button),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
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
                        .padding(vertical = 12.dp) // Añade un padding vertical más grande.
                        .clip(RoundedCornerShape(16.dp)) // Bordes redondeados para coherencia
                        .background(MaterialTheme.colorScheme.tertiary) // Color de fondo
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(16.dp)
                        ), // Añadido borde
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(
                        stringResource(id = R.string.logout_button), // Texto dentro del botón.
                        color = Color.White, // Texto blanco.
                        style = MaterialTheme.typography.titleMedium // Estilo de texto del botón.
                    )
                }
            }
        }
    }
}




