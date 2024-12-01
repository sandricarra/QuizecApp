package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pt.isec.ams.quizec.viewmodel.QuizHistoryViewModel

@Composable
fun QuizHistoryScreen(navController: NavController, viewModel: QuizHistoryViewModel = viewModel()) {

    // Observar la lista de cuestionarios filtrados desde el ViewModel
    val quizzes = viewModel.filteredQuizzes.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Text(text = "My Quiz History", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown para filtrar por estado
        DropdownMenuFilter(
            selectedStatus = viewModel.selectedStatus,
            onStatusChange = { status -> viewModel.filterByStatus(status) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(quizzes.value.size) { index ->
                val quiz = quizzes.value[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = {
                        // Navegar al historial de preguntas del cuestionario seleccionado
                        navController.navigate("questionHistory/${quiz.id}")
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Mostrar el título y la fecha de creación del cuestionario
                        Text(text = quiz.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Created on: ${viewModel.formatDate(quiz.createdAt)}", // Formatear la fecha
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {

                            // Botón para eliminar el cuestionario
                            Button(
                                onClick = { viewModel.deleteQuiz(quiz.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Delete", color = MaterialTheme.colorScheme.onError)
                            }

                            // Botón para duplicar el cuestionario
                            Button(
                                onClick = { viewModel.duplicateQuiz(quiz) }
                            ) {
                                Text("Duplicate")
                            }

                        }
                    }
                }
            }
        }
    }
}

// Componente para mostrar el menú desplegable de filtro por estado
@Composable
fun DropdownMenuFilter(selectedStatus: String, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = !expanded }) {
            Text(text = "Filter: $selectedStatus")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("All", "Available", "Completed", "Locked").forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onStatusChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}



