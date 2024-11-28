package pt.isec.ams.quizec.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.viewmodel.QuizScreenViewModel

@Composable
fun QuizScreen(viewModel: QuizScreenViewModel = viewModel()) {
    var quizId by remember { mutableStateOf("") }
    val quiz by viewModel.quiz
    val question by viewModel.question
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // Función para cargar el cuestionario
    fun loadQuiz() {
        if (quizId.isNotBlank()) {
            viewModel.loadQuizAndFirstQuestion(quizId)
        }
    }

    // Función para ir a la siguiente pregunta
    fun nextQuestion() {
        viewModel.loadNextQuestion()
    }

    // Función para ir a la pregunta anterior
    fun previousQuestion() {
        viewModel.loadPreviousQuestion()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Campo de texto para introducir el ID del cuestionario
        TextField(
            value = quizId,
            onValueChange = { quizId = it },
            label = { Text("Enter Quiz ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cargar el cuestionario
        Button(
            onClick = { loadQuiz() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Load Quiz")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar estado de carga
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (quiz != null && question != null) {
            // Mostrar detalles del cuestionario
            Text(text = "Quiz: ${quiz?.title}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar la pregunta dependiendo de su tipo
            when (question?.type) {
                QuestionType.P03 -> {
                    MultipleChoiceQuestion(question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                QuestionType.P01 -> {
                    TrueFalseQuestion(question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                else -> {
                    Text("Unsupported question type.")
                }
            }
        }
    }
}

// Composable para mostrar preguntas de opción múltiple
@Composable
fun MultipleChoiceQuestion(question: Question, onNext: () -> Unit, onPrevious: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)
        question.options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = false,
                    onClick = { /* Handle answer selection */ }
                )
                Text(text = option, modifier = Modifier.padding(start = 8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onPrevious) {
                Text("Previous Question")
            }
            Button(onClick = onNext) {
                Text("Next Question")
            }
        }
    }
}

// Composable para mostrar preguntas de verdadero/falso
@Composable
fun TrueFalseQuestion(question: Question, onNext: () -> Unit, onPrevious: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = false,
                onClick = { /* Handle answer selection */ }
            )
            Text(text = "True", modifier = Modifier.padding(start = 8.dp))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = false,
                onClick = { /* Handle answer selection */ }
            )
            Text(text = "False", modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onPrevious) {
                Text("Previous Question")
            }
            Button(onClick = onNext) {
                Text("Next Question")
            }
        }
    }
}
