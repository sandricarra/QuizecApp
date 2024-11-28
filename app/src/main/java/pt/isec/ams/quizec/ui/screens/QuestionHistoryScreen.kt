package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pt.isec.ams.quizec.viewmodel.QuestionHistoryViewModel

@Composable
fun QuestionHistoryScreen(
    navController: NavController,
    quizId: String,
    viewModel: QuestionHistoryViewModel = viewModel()
) {
    // Observar las preguntas del cuestionario desde el ViewModel
    val questions = viewModel.questions.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

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
            items(questions.value.size) { index ->
                val question = questions.value[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Título de la pregunta
                        Text(text = question.title, style = MaterialTheme.typography.titleMedium)

                        // Botones de acciones (Editar, Duplicar, Eliminar)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = {
                                    navController.navigate("questionEdit/${question.id}")
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Edit")
                            }
                            Button(
                                onClick = { viewModel.duplicateQuestion(question, quizId) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Duplicate")
                            }
                            Button(
                                onClick = { viewModel.deleteQuestion(question.id, quizId)  },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Delete", color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                }
            }
        }
    }
}
