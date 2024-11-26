package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.isec.ams.quizec.viewmodel.QuizScreenViewModel

@Composable
fun QuizScreen(quizId: String, viewModel: QuizScreenViewModel = viewModel()) {
    var quizId by remember { mutableStateOf("") }
    val quiz by viewModel.quiz
    val questions by viewModel.questions
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    LaunchedEffect(quizId) {
        if (quizId.isNotBlank()) {
            viewModel.loadQuiz(quizId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo de texto para ingresar el quizId
        TextField(
            value = quizId,
            onValueChange = { quizId = it },
            label = { Text("Enter Quiz ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para cargar el cuestionario
        Button(
            onClick = {
                if (quizId.isNotBlank()) {
                    viewModel.loadQuiz(quizId)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Load Quiz")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra el estado de carga
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
            }
            quiz != null -> {
                // Mostrar detalles del cuestionario
                Text(
                    text = "Quiz Details",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("ID: ${quiz?.id}")
                Text("Title: ${quiz?.title}")
                Text("Description: ${quiz?.description}")
                Text("Creator: ${quiz?.creatorId}")
                Text("Time Limit: ${quiz?.timeLimit} seconds")
                Text("Geolocation Restricted: ${quiz?.isGeolocationRestricted}")
                Text("Access Controlled: ${quiz?.isAccessControlled}")
                Text("Show Results Immediately: ${quiz?.showResultsImmediately}")
                Text("Status: ${quiz?.status}")
                Text("Participants: ${quiz?.participants?.joinToString()}")
                Text("Questions Count: ${quiz?.questions?.size ?: 0}")
                Text("Image URL: ${quiz?.imageUrl}")
                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar detalles de las preguntas
                Text(
                    text = "Questions",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                questions.forEachIndexed { index, question ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Question ${index + 1}")
                            Text("ID: ${question.id}")
                            Text("Title: ${question.title}")
                            Text("Type: ${question.type}")
                            Text("Options: ${question.options.joinToString()}")
                            Text("Correct Answers: ${question.correctAnswers.joinToString()}")
                            question.imageUrl?.let {
                                Text("Image URL: $it")
                            }
                        }
                    }
                }
            }
        }
    }
}

