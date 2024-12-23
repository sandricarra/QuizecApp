package pt.isec.ams.quizec.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.viewmodel.QuizScreenViewModel
import androidx.core.app.ActivityCompat
import android.app.Activity
import pt.isec.ams.quizec.data.models.QuizStatus
import pt.isec.ams.quizec.viewmodel.HomeScreenViewModel

@Composable
fun QuizAccessScreen(
    viewModel: QuizScreenViewModel = viewModel(),creatorId: String
) {
    var quizId by remember { mutableStateOf("") }
    var isQuizStarted by remember { mutableStateOf(false) } // Control del estado de inicio del quiz
    val quiz by viewModel.quiz
    val question by viewModel.question
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val isQuizFinished by viewModel.isQuizFinished
    val timeRemaining by viewModel.timeRemaining

    val userName = quiz?.creatorId // Define el nombre del usuario


    val context = LocalContext.current
    val quizStatus by viewModel.quizStatus
    var isQuizLoaded by remember { mutableStateOf(false) }

    // Estado para manejar permisos de ubicación y posición del usuario
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Función para verificar permisos y obtener la ubicación
    fun checkLocationPermissionsAndFetch() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permisos concedidos, obtener la ubicación
            val provider = LocationManager.GPS_PROVIDER
            val location = locationManager.getLastKnownLocation(provider)
            location?.let {
                userLocation = Pair(it.latitude, it.longitude)
            } ?: Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
        } else {
            if (context is Activity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1001
                )
            } else {
                Toast.makeText(context, "Cannot request permissions in this context", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Mover la función loadQuiz fuera de LaunchedEffect
    fun loadQuiz() {
        if (quizId.isNotBlank()) {
            viewModel.loadQuizAndFirstQuestion(quizId)

            // Verificar si el cuestionario tiene restricciones de ubicación
            val quizData = viewModel.quiz.value
            if (quizData?.isGeolocationRestricted == true && userLocation != null && quizData.location != null) {
                val userLat = userLocation!!.first
                val userLon = userLocation!!.second
                val quizLat = quizData.location.latitude
                val quizLon = quizData.location.longitude

                val distance = FloatArray(1)
                Location.distanceBetween(userLat, userLon, quizLat, quizLon, distance)

                // Considerar una distancia de 50 km como límite permitido
                if (distance[0] > 50000) {
                    Toast.makeText(
                        context,
                        "You are not in the allowed region for this quiz.",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }

            isQuizStarted = true // Cambiar al estado de inicio del quiz
        }
    }



    fun nextQuestion() {
        viewModel.loadNextQuestion()
    }

    fun previousQuestion() {
        viewModel.loadPreviousQuestion()
    }

    fun calculateResults(): String {
        val correctAnswers = viewModel.correctAnswers.value
        return "You got $correctAnswers correct."
    }

    LaunchedEffect(quizId) {
        if (quizId.isNotBlank()) {
            // Bucle para verificar el estado de quizStatus
            val userId = creatorId // Obtén el ID del usuario actual
            viewModel.addUserToWaitingList(quizId, userId)
            while (quizStatus != QuizStatus.AVAILABLE) {

                // Espera y vuelve a verificar el estado cada 3 segundos
                delay(3000)
                viewModel.checkQuizStatus(quizId)
                println("Quiz status: $quizStatus")
            }
            viewModel.loadQuizAndFirstQuestion(quizId)
            viewModel.removeUserFromWaitingList(quiz?.id.toString(),creatorId)
            // El quiz está disponible, ahora cargamos el quiz y marcamos que ha sido cargado
            isQuizLoaded = true
            isQuizStarted = true

        }
    }

    // Comprobar permisos de ubicación al cargar la pantalla
    LaunchedEffect(Unit) {
        checkLocationPermissionsAndFetch()
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

    if (quiz?.status == QuizStatus.LOCKED) {
        // Quiz está bloqueado, mostramos un mensaje
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "The quiz is currently locked. Please wait for the creator to unlock it.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    } else {
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
                        // Llamar a la función onQuizCompleted


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
}


