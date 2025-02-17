package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.isec.ams.quizec.ui.theme.BackgroundImage
import pt.isec.ams.quizec.ui.viewmodel.QuizHistoryViewModel

@Composable
fun QuizHistoryScreen(navController: NavController, viewModel: QuizHistoryViewModel = viewModel(), userId: String) {
    // Cargar el usuario al inicio
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    // Observar la lista de cuestionarios filtrados desde el ViewModel
    val quizzes = viewModel.filteredQuizzes.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        BackgroundImage()

        // Contenido
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la pantalla
            item {
                Text(
                    text = "My Quiz History \uD83D\uDCC3",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Ajusta el color del texto para que sea legible sobre el fondo
                    ),
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }

            // Dropdown para filtrar por estado
            item {
                DropdownMenuFilter(
                    selectedStatus = viewModel.selectedStatus,
                    onStatusChange = { status -> viewModel.filterByStatus(status) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lista de cuestionarios
            items(quizzes.value) { quiz ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFBFDEFF).copy(alpha = 0.9f)), // Ajusta la opacidad de la tarjeta
                    onClick = {
                        // Navegar al historial de preguntas del cuestionario seleccionado
                        navController.navigate("questionHistory/${quiz.id}/$userId")
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Título del cuestionario
                        Text(
                            text = quiz.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Fecha de creación del cuestionario
                        Text(
                            text = "Created on: ${viewModel.formatDate(quiz.createdAt)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF757575)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Botones solo visibles para cuestionarios creados
                        if (quiz.creatorId == viewModel.currentUser?.id) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { viewModel.deleteQuiz(quiz.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                ) {
                                    Text("Delete", color = Color.White)
                                }

                                Button(
                                    onClick = { viewModel.duplicateQuiz(quiz) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Duplicate", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DropdownMenuFilter(selectedStatus: String, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
        ) {
            Text(
                text = "Filter: $selectedStatus",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("All", "CreatedQuizzes", "ParticipatedQuizzes").forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF1E88E5)
                            )
                        )
                    },
                    onClick = {
                        onStatusChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}
