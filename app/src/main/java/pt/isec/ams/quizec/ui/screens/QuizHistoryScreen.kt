package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pt.isec.ams.quizec.data.models.Quiz
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
                        coroutineScope.launch {
                            navController.navigate("quizScreen/${quiz.id}")
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = quiz.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Creado el: ${viewModel.formatDate(quiz.createdAt)}", // Formatear la fecha
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = { viewModel.deleteQuiz(quiz.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                            }
                            Button(
                                onClick = { viewModel.duplicateQuiz(quiz) }
                            ) {
                                Text("Duplicar")
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
        Button(onClick = { expanded = !expanded }) {
            Text(text = "Filtrar: $selectedStatus")
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


