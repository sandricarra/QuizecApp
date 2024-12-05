package pt.isec.ams.quizec.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.viewmodel.QuizScreenViewModel
import pt.isec.ams.quizec.utils.LocationUtils
import pt.isec.ams.quizec.utils.calculateDistance

@Composable
fun QuizAccessScreen(
    viewModel: QuizScreenViewModel = viewModel(),
    isGeolocationRestricted: Boolean,
    isLocationValid: () -> Unit, // Tipo de parámetro explícito
    onError: (String) -> Unit // Tipo de parámetro explícito
) {
    var quizId by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf("") }
    var isLocationValidState by remember { mutableStateOf(false) }
    var isQuizStarted by remember { mutableStateOf(false) } // Control del estado de inicio del quiz
    val quiz by viewModel.quiz
    val question by viewModel.question
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val isQuizFinished by viewModel.isQuizFinished

    val context = LocalContext.current

    fun addParticipant(quizId: String, userId: String) { /* Función existente */ }

    fun checkAccess(quizId: String) { /* Función existente */ }

    fun nextQuestion() {
        viewModel.loadNextQuestion()
    }

    fun previousQuestion() {
        viewModel.loadPreviousQuestion()
    }

    fun checkLocation(quizId: String) { /* Función existente */ }

    fun loadQuiz() {
        if (quizId.isNotBlank()) {
            viewModel.loadQuizAndFirstQuestion(quizId)
            if (isGeolocationRestricted) {
                checkLocation(quizId)
            } else {
                isLocationValidState = true
                isLocationValid()
            }
            isQuizStarted = true // Cambiar al estado de inicio del quiz
        }
    }

    fun calculateResults(): String {
        val correctAnswers = viewModel.correctAnswers.value
        return "You got $correctAnswers correct."
    }

    if (!isQuizStarted) {
        // Pantalla inicial para introducir el ID del cuestionario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = quizId,
                onValueChange = { quizId = it },
                label = { Text("Enter Quiz ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { loadQuiz() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Load Quiz")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (locationError.isNotEmpty()) {
                Text(text = locationError, color = Color.Red, modifier = Modifier.padding(8.dp))
            }

            if (isLocationValidState) {
                Text(text = "You are within range to access this quiz.", color = Color.Green)
            }
        }
    } else {
        // Pantalla del cuestionario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (isQuizFinished) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val results = calculateResults()
                        Toast.makeText(context, results, Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check Answers")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isQuizStarted = false // Volver al estado inicial
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Exit Quiz")
                }
            } else if (quiz != null && question != null) {
                Text(text = "Quiz: ${quiz?.title}", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar la pregunta dependiendo de su tipo
                when (question?.type) {
                    QuestionType.P01 -> {
                        P01(
                            question = question!!,
                            onNext = { nextQuestion() },
                            onPrevious = { previousQuestion() },
                            viewModel = viewModel
                        )
                    }

                    QuestionType.P02 -> {
                        P02(
                            question = question!!,
                            onNext = { nextQuestion() },
                            onPrevious = { previousQuestion() },
                            viewModel = viewModel
                        )
                    }

                    QuestionType.P03 -> {
                        P03(
                            question = question!!,
                            onNext = { nextQuestion() },
                            onPrevious = { previousQuestion() },
                            viewModel = viewModel
                        )
                    }

                    QuestionType.P04 -> {
                        P04(
                            question = question!!,
                            onNext = { nextQuestion() },
                            onPrevious = { previousQuestion() },
                            viewModel = viewModel
                        )
                    }

                    QuestionType.P05 -> {
                        P05(
                            question = question!!,
                            onNext = { nextQuestion() },
                            onPrevious = { previousQuestion() },
                            viewModel = viewModel
                        )
                    }

                    QuestionType.P06 -> {
                        P06(
                            question = question!!,
                            onNext = { nextQuestion() },
                            onPrevious = { previousQuestion() },
                            viewModel = viewModel
                        )
                    }

                    QuestionType.P07 -> {
                        P07(
                            question = question!!,
                            onNext = { nextQuestion() },
                            onPrevious = { previousQuestion() },
                            viewModel = viewModel
                        )
                    }

                    QuestionType.P08 -> {
                        P08(
                            question = question!!,
                            onNext = { nextQuestion() },
                            onPrevious = { previousQuestion() },
                            viewModel = viewModel
                        )
                    }

                    else -> {
                        Text("Unsupported question type.")
                    }
                }
            }
        }
    }
}














