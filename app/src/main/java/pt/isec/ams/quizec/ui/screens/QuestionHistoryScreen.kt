package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.isec.ams.quizec.ui.viewmodel.QuestionHistoryViewModel

@Composable
fun QuestionHistoryScreen(
    navController: NavController,
    quizId: String,
    userId: String,
    viewModel: QuestionHistoryViewModel = viewModel()
) {
    // Observar las preguntas del cuestionario desde el ViewModel
    val questions by viewModel.questions.collectAsState(initial = emptyList())
    val creatorId by viewModel.creatorId.collectAsState(initial = null)

    // Cargar preguntas al entrar en la pantalla
    LaunchedEffect(quizId) {
        viewModel.loadQuestions(quizId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Text(text = "Questions for Quiz $quizId", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(questions.size) { index ->
                val question = questions[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFBFDEFF)),

                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Título de la pregunta
                        Text(text = question.title, style = MaterialTheme.typography.titleMedium)

                        // Mostrar botones solo si el usuario es el creador
                        if (creatorId == userId) {
                            // Botones de acciones (Editar, Duplicar, Eliminar, Resultados)
                            Spacer(modifier = Modifier.height(8.dp))

                            Column {
                                // Fila 1: Editar y Duplicar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre botones
                                ) {
                                    Button(
                                        onClick = {
                                            navController.navigate("editQuestion/${question.id}")
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Text("Edit")
                                    }
                                    Button(
                                        onClick = { viewModel.duplicateQuestion(question, quizId) },
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Text("Duplicate")
                                    }
                                }

                                // Fila 2: Eliminar y Ver Resultados
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre botones
                                ) {
                                    Button(
                                        onClick = { viewModel.deleteQuestion(question.id, quizId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Text("Delete", color = MaterialTheme.colorScheme.onError)
                                    }
                                    Button(
                                        onClick = {
                                            navController.navigate("resultsScreen/${question.id}")
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Text("View Results")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


