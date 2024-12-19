package pt.isec.ams.quizec.ui.screens


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.viewmodel.QuizScreenViewModel


@Composable
fun QuizAccessScreen(
    viewModel: QuizScreenViewModel = viewModel(),
) {
    var quizId by remember { mutableStateOf("") }
    //var locationError by remember { mutableStateOf("") }
   // var isLocationValidState by remember { mutableStateOf(false) }
    var isQuizStarted by remember { mutableStateOf(false) } // Control del estado de inicio del quiz
    val quiz by viewModel.quiz
    val question by viewModel.question
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val isQuizFinished by viewModel.isQuizFinished
    val timeRemaining by viewModel.timeRemaining

    val context = LocalContext.current

    fun nextQuestion() {
        viewModel.loadNextQuestion()
    }

    fun previousQuestion() {
        viewModel.loadPreviousQuestion()
    }

    fun loadQuiz() {
        if (quizId.isNotBlank()) {
            viewModel.loadQuizAndFirstQuestion(quizId)
            isQuizStarted = true // Cambiar al estado de inicio del quiz
        }
    }

    fun calculateResults(): String {
        val correctAnswers = viewModel.correctAnswers.value
        return "You got $correctAnswers correct."
    }

    // Iniciar el contador cuando el cuestionario se cargue
    LaunchedEffect(timeRemaining) {
        if (isQuizStarted && timeRemaining != null && timeRemaining!! > 0) {
            while (timeRemaining!! > 0) {
                delay(1000L) // Esperar 1 segundo
                viewModel.decrementTimeRemaining()
            }
            // Finalizar el cuestionario cuando el tiempo se agote
            viewModel.finishQuiz()
            isQuizStarted = false
            Toast.makeText(context, "Time's up! Quiz finished.", Toast.LENGTH_LONG).show()
        }
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

        }
    } else if (isQuizFinished) {
        // Pantalla final con los botones Check Answers y Exit Quiz
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Quiz Finished!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
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
        }
    } else {
        // Pantalla del cuestionario con preguntas
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
            } else if (quiz != null && question != null) {
                Text(
                    text = "Quiz: ${quiz?.title}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar el contador de tiempo restante
                if (timeRemaining != null) {
                    Text(
                        text = "Time remaining: ${timeRemaining!! / 60}:${timeRemaining!! % 60}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

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














