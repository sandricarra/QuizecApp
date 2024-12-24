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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import pt.isec.ams.quizec.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizAccessScreen(navController: NavController,
                     viewModel: QuizScreenViewModel = viewModel(), creatorId: String
) {
    var quizId by remember { mutableStateOf("") }
    var isQuizStarted by remember { mutableStateOf(false) } // Control del estado de inicio del quiz
    val quiz by viewModel.quiz
    val question by viewModel.question
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val isQuizFinished by viewModel.isQuizFinished
    val timeRemaining by viewModel.timeRemaining


    val correctAnswers by viewModel.correctAnswers


    val context = LocalContext.current
    val quizStatus by viewModel.quizStatus
    var isQuizLoaded by remember { mutableStateOf(false) }

    // Estado para manejar permisos de ubicación y posición del usuario
    var userLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    LaunchedEffect(viewModel.quizStatus.value) {
        if (viewModel.quizStatus.value == QuizStatus.IN_PROGRESS) {
            viewModel.startTimer() // Iniciar el temporizador cuando el quiz esté disponible
        }
    }
    LaunchedEffect(quizId) {
        if (quizId.isNotBlank()) {
            viewModel.observeQuizStatus(quizId) // Observar el estado del quiz
        }
    }
    LaunchedEffect(isQuizFinished) {
        if (isQuizFinished) {
            viewModel.saveQuizResult(quizId, creatorId, correctAnswers, quiz?.questions?.size ?: 0)
        }

    }


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
                Toast.makeText(
                    context,
                    "Cannot request permissions in this context",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Mover la función loadQuiz fuera de LaunchedEffect
    fun loadQuiz() {
        if (quizId.isNotBlank()) {

            viewModel.hasUserPlayedQuiz(quizId, creatorId) { hasPlayed ->
                if (hasPlayed) {
                    // Mostrar mensaje si el usuario ya ha jugado el quiz
                    Toast.makeText(context, "You have already played this quiz", Toast.LENGTH_LONG)
                        .show()
                } else {
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

                        }
                    }

                    isQuizStarted = true // Cambiar al estado de inicio del quiz
                }
            }
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


            while (quizStatus != QuizStatus.AVAILABLE) {

                // Espera y vuelve a verificar el estado cada 3 segundos
                delay(500)
                viewModel.checkQuizStatus(quizId)
                println("Quiz status: $quizStatus")
            }
            loadQuiz()
            viewModel.removeUserFromWaitingList(quiz?.id.toString(), creatorId)
            // El quiz está disponible, ahora cargamos el quiz y marcamos que ha sido cargado
            isQuizLoaded = true
            isQuizStarted = true

        }
    }
    LaunchedEffect(quizId) {
        if (quizId.isNotBlank()) {
            // Bucle para verificar el estado de quizStatus

            while (quizStatus != QuizStatus.FINISHED) {

                // Espera y vuelve a verificar el estado cada 3 segundos
                delay(3000)
                viewModel.checkQuizStatus(quizId)
                viewModel.updateQuizStatus(quizId)
                println("Quiz status: $quizStatus")
            }


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
            viewModel.updateQuizStatusToFinished(quizId)
            Toast.makeText(context, "Time's up! Quiz finished.", Toast.LENGTH_LONG).show()
        }
    }


    if (quiz?.status == QuizStatus.LOCKED) {
        viewModel.addUserToWaitingList(quizId, creatorId)

        // Manejar el botón de retroceso
        BackHandler {
            // Remover el usuario de la lista de espera al retroceder
            if (quizId.isNotBlank()) {
                viewModel.removeUserFromWaitingList(quizId, creatorId)
            }

            // Navegar de vuelta al HomeScreen
            navController.navigate("home") {
                // Limitar el historial de navegación para evitar regresar al QuizAccessScreen
                popUpTo("home") { inclusive = true }
            }
        }

        // Mostrar mensaje de espera
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
            viewModel.removeUserFromWaitingList(quiz?.id.toString(), creatorId)
            viewModel.removeUserFromPlayingList(quiz?.id.toString(), creatorId)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Imagen o logo
                Image(
                    painter = painterResource(id = R.drawable.ic_logo), // Cambia esto por el ID de tu logo
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 32.dp)
                )

                // Texto de bienvenida
                Text(
                    text = "Welcome to Quizec!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Campo de texto para ingresar el ID del cuestionario
                TextField(
                    value = quizId,
                    onValueChange = { quizId = it },
                    label = { Text("Enter Quiz ID") },
                    placeholder = { Text("e.g., ABC123") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,

                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para cargar el cuestionario
                Button(
                    onClick = { loadQuiz() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Load Quiz", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Mensaje opcional
                Text(
                    text = "Please enter the Quiz ID to proceed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
        } else if (isQuizFinished || quizStatus == QuizStatus.FINISHED || timeRemaining == 0L) {

            viewModel.removeUserFromWaitingList(quiz?.id.toString(), creatorId)
            viewModel.removeUserFromPlayingList(quiz?.id.toString(), creatorId)

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

                // Mostrar el contador de tiempo restante
                if (timeRemaining != null) {
                    Text(
                        text = "Time remaining: ${timeRemaining!! / 60}:${timeRemaining!! % 60}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        if (quiz?.showResultsImmediately == true || quizStatus == QuizStatus.FINISHED) {
                            val results = calculateResults()
                            Toast.makeText(context, results, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Please wait until the end of the quiz to see the answers.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    enabled = quiz?.showResultsImmediately == true || quizStatus == QuizStatus.FINISHED || timeRemaining == 0L,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check Answers")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isQuizStarted = false // Volver al estado inicial
                        viewModel.removeUserFromPlayingList(quiz?.id.toString(), creatorId)


                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Exit Quiz")
                }
            }
        } else {
            BackHandler {
                // Remover el usuario de la lista de espera al retroceder
                if (quizId.isNotBlank()) {
                    viewModel.removeUserFromWaitingList(quizId, creatorId)
                    viewModel.removeUserFromPlayingList(quizId, creatorId)
                }
                // Navegar de vuelta al HomeScreen
                navController.navigate("home") {
                    // Limitar el historial de navegación para evitar regresar al QuizAccessScreen
                    popUpTo("home") { inclusive = true }
                }
            }
            viewModel.addUserToPlayingList(quiz?.id.toString(), creatorId)
            viewModel.removeUserFromWaitingList(quiz?.id.toString(), creatorId)
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



