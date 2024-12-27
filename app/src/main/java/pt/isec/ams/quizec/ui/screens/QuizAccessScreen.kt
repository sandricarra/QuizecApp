@file:OptIn(ExperimentalMaterial3Api::class)

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
import pt.isec.ams.quizec.ui.viewmodel.QuizScreenViewModel
import androidx.core.app.ActivityCompat
import android.app.Activity
import pt.isec.ams.quizec.data.models.QuizStatus
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.Quiz


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizAccessScreen(navController: NavController,
                     viewModel: QuizScreenViewModel = viewModel(), creatorId: String
) {

    var isQuizStarted by remember { mutableStateOf(false) } // Control del estado de inicio del quiz
    // Usa collectAsState para observar los StateFlows
    val quiz by viewModel.quiz.collectAsState()
    val question by viewModel.question.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isQuizFinished by viewModel.isQuizFinished.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val correctAnswers by viewModel.correctAnswers.collectAsState()
    val quizStatus by viewModel.quizStatus.collectAsState()











    var isQuizLoaded by remember { mutableStateOf(false) }

    // Estado para manejar permisos de ubicación y posición del usuario
    val context = LocalContext.current

    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }

    val viewModel: QuizScreenViewModel = viewModel()
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var quizId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Start) }


    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.getLocation(fusedLocationProviderClient) { location ->
                    userLocation = location
                }
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            viewModel.getLocation(fusedLocationProviderClient) { location ->
                userLocation = location
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Verificar permisos de ubicación y obtener la ubicación del usuario



    // Observar userLocation como State






    /*LaunchedEffect(viewModel.quizStatus.value) {
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

            viewModel.removeUserFromWaitingList(quiz?.id.toString(), creatorId)
            // El quiz está disponible, ahora cargamos el quiz y marcamos que ha sido cargado


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
    }*/



    // Iniciar el contador cuando el cuestionario se cargue
    LaunchedEffect(timeRemaining) {
        if (timeRemaining != null && timeRemaining!! > 0) {
            while (timeRemaining!! > 0) {
                delay(1000L) // Esperar 1 segundo
                viewModel.decrementTimeRemaining()
            }
            // Finalizar el cuestionario cuando el tiempo se agote
            viewModel.finishQuiz()
            viewModel.updateQuizStatusToFinished(quizId)
            viewModel.updateQuizStatusToFinished2(quizId)
            viewModel.removeUserFromPlayingList(quizId, creatorId)
            viewModel.removeUserFromWaitingList(quizId, creatorId)
            viewModel.saveQuizResult(quizId, creatorId, correctAnswers, quiz?.questions?.size ?: 0)
            currentScreen = Screen.Result

            Toast.makeText(context, "Time's up! Quiz finished.", Toast.LENGTH_LONG).show()
        } else if (timeRemaining == 0L) {
            Toast.makeText(context, "Time's up! Quiz finished.", Toast.LENGTH_LONG).show()
            viewModel.finishQuiz()
            viewModel.updateQuizStatusToFinished(quizId)
            viewModel.updateQuizStatusToFinished2(quizId)
            viewModel.removeUserFromPlayingList(quizId, creatorId)
            viewModel.removeUserFromWaitingList(quizId, creatorId)
            viewModel.saveQuizResult(quizId, creatorId, correctAnswers, quiz?.questions?.size ?: 0)
            currentScreen = Screen.Result
        }

    }


    /* if (quiz?.status == QuizStatus.LOCKED) {
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Imagen o logo
                    item {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo), // Cambia esto por el ID de tu logo
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(150.dp)
                                .padding(bottom = 32.dp)
                        )
                    }

                    // Texto de bienvenida
                    item {
                        Text(
                            text = "Start playing!",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Campo de texto para ingresar el ID del cuestionario
                    item {
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
                    }



                    // Botón para cargar el cuestionario
                    item {
                        Button(
                            onClick = { viewModel.loadQuiz( quizId, context,creatorId)
                                      isQuizStarted = true
                                      isQuizLoaded=true },
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
                    }

                    // Mensaje opcional
                    item {
                        Text(
                            text = "Please enter the Quiz ID to proceed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }


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
                            P08Reply(
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

*/
    LaunchedEffect(quizId) {
        if (quizId.isNotBlank()) {
            viewModel.observeQuizStatus(quizId)
        }
    }

    LaunchedEffect(quizStatus) {
        if (quizStatus == QuizStatus.AVAILABLE && currentScreen == Screen.Waiting) {
            currentScreen = Screen.Presentation

        }
    }
    LaunchedEffect(quizStatus) {
        if (quizStatus == QuizStatus.FINISHED && currentScreen == Screen.Question) {
            currentScreen = Screen.Result
            viewModel.saveQuizResult(quizId, creatorId, correctAnswers, quiz?.questions?.size ?: 0)
            viewModel.removeUserFromPlayingList(quizId, creatorId)
            viewModel.finishQuiz()
            viewModel.updateQuizStatusToFinished(quizId)
            viewModel.removeUserFromWaitingList(quizId, creatorId)
        }
    }

    when (currentScreen) {

        Screen.Start -> {
            StartScreen(
                quizId = quizId,
                onQuizIdChange = { quizId = it },

                creatorId = creatorId,
                onStartQuiz = {
                    viewModel.hasUserPlayedQuiz(quizId, creatorId) { hasPlayed ->
                        if (hasPlayed) {
                            Toast.makeText(context, "You have already played this quiz.", Toast.LENGTH_LONG).show()
                        } else {
                            if (quizStatus == QuizStatus.LOCKED && quiz?.isAccessControlled == true) {
                                currentScreen = Screen.Waiting
                            } else if (quizStatus == QuizStatus.AVAILABLE) {
                                // Verificar si la ubicación es necesaria
                                if (quiz?.isGeolocationRestricted == true && userLocation == null ) {
                                    currentScreen = Screen.Start
                                    viewModel.removeUserFromPlayingList(quizId, creatorId)
                                    Toast.makeText(
                                        context,
                                        "Location is required to start this quiz. Please enable location services.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    currentScreen = Screen.Presentation
                                    viewModel.loadQuiz(quizId, context,creatorId)

                                }
                            }
                        }
                    }
                }
            )
        }
        Screen.Presentation -> {
            PresentationScreen(
                quiz = quiz,
                creatorName = quiz?.creatorId.toString(), // Reemplaza con la lógica para obtener el nombre del creador
                onStartQuiz = {
                    currentScreen = Screen.Question

                    viewModel.startTimer()
                    viewModel.removeUserFromWaitingList(quizId, creatorId)
                    viewModel.addUserToPlayingList(quizId, creatorId)
                    viewModel.updateQuizStatus(quizId)

                }
            )
        }

        Screen.Question -> {
            QuestionScreen(
                viewModel = viewModel,
                onQuizFinished = { currentScreen = Screen.Result
                viewModel.saveQuizResult(quizId, creatorId, correctAnswers, quiz?.questions?.size ?: 0)
                viewModel.removeUserFromPlayingList(quizId, creatorId)
                viewModel.finishQuiz()
                    viewModel.updateQuizStatusToFinished(quizId)
                    viewModel.removeUserFromWaitingList(quizId, creatorId)


                }

            )
        }
        Screen.Result -> {
            ResultScreen(
                viewModel = viewModel,
                quizId = quizId,
                creatorId = creatorId,
                context = context,
                onExitQuiz = { currentScreen = Screen.Start }
            )
        }
        Screen.Waiting ->{
            WaitingScreen1(navController, viewModel, quizId, creatorId)
        }
    }
}


@Composable
fun WaitingScreen1(navController: NavController, viewModel: QuizScreenViewModel, quizId: String, creatorId: String) {
    viewModel.addUserToWaitingList(quizId, creatorId)

    BackHandler {
        if (quizId.isNotBlank()) {
            viewModel.removeUserFromWaitingList(quizId, creatorId)
        }
        navController.navigate("home") {
            popUpTo("home") { inclusive = true }
        }
    }

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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    quizId: String,
    creatorId: String, // Añade creatorId como parámetro
    onQuizIdChange: (String) -> Unit,
    onStartQuiz: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
       item {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 32.dp)
            )
        }
        item {
            Text(
                text = "Start playing!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {
            TextField(
                value = quizId,
                onValueChange = onQuizIdChange,
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
        }
        item {
            Button(
                onClick = onStartQuiz,
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
        }
        item {
            Text(
                text = "Please enter the Quiz ID to proceed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun ResultScreen(
    viewModel: QuizScreenViewModel,
    quizId: String,
    creatorId: String,
    context: Context,
    onExitQuiz: () -> Unit
) {
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val quizStatus by viewModel.quizStatus.collectAsState()
    val quiz by viewModel.quiz.collectAsState()

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

        // Mostrar el tiempo restante
        if (timeRemaining != null) {
            Text(
                text = "Time remaining: ${timeRemaining!! / 60}:${timeRemaining!! % 60}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón Check Answers
        Button(
            onClick = {
                val results = viewModel.correctAnswers.value.toString()
                Toast.makeText(context, "You got $results correct.", Toast.LENGTH_LONG).show()
            },
            enabled = quiz?.showResultsImmediately == true || quizStatus == QuizStatus.FINISHED,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check Answers")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExitQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exit Quiz")
        }
    }
}




@Composable
fun QuestionScreen(viewModel: QuizScreenViewModel, onQuizFinished: () -> Unit) {
    val question by viewModel.question.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val context = LocalContext.current
    val quiz by viewModel.quiz.collectAsState()
    val totalQuestions = quiz?.questions?.size ?: 0
    val currentQuestionIndex = viewModel.currentQuestionIndex.value
    val isQuestionAnswered = question?.let { viewModel.isQuestionAnswered(it.id) } ?: false

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
        } else if (question != null) {
            Text(
                text = "Question: ${question?.title}",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (timeRemaining != null) {
                Text(
                    text = "Time remaining: ${timeRemaining!! / 60}:${timeRemaining!! % 60}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
            }


            when (question?.type) {
                QuestionType.P01 -> {
                    P01(
                        question = question!!,
                        onNext = {
                            viewModel.loadNextQuestion()
                        },
                        onPrevious = { viewModel.loadPreviousQuestion() },
                        viewModel = viewModel,
                        context = context,
                        isQuestionAnswered = isQuestionAnswered

                    )
                }

                QuestionType.P02 -> {
                    P02(
                        question = question!!,
                        onNext = { viewModel.loadNextQuestion() },
                        onPrevious = { viewModel.loadPreviousQuestion() },
                        viewModel = viewModel,
                        context = context,
                        isQuestionAnswered = isQuestionAnswered
                    )
                }

                QuestionType.P03 -> {
                    P03(
                        question = question!!,
                        onNext = { viewModel.loadNextQuestion() },
                        onPrevious = { viewModel.loadPreviousQuestion() },
                        viewModel = viewModel,
                                context = context,
                        isQuestionAnswered = isQuestionAnswered
                    )
                }

                QuestionType.P04 -> {
                    P04(
                        question = question!!,
                        onNext = { viewModel.loadNextQuestion() },
                        onPrevious = { viewModel.loadPreviousQuestion() },
                        viewModel = viewModel,
                        context = context,
                        isQuestionAnswered = isQuestionAnswered
                    )
                }

                QuestionType.P05 -> {
                    P05(
                        question = question!!,
                        onNext = { viewModel.loadNextQuestion() },
                        onPrevious = { viewModel.loadPreviousQuestion() },
                        viewModel = viewModel,
                        context = context,
                        isQuestionAnswered = isQuestionAnswered
                    )
                }

                QuestionType.P06 -> {
                    P06(
                        question = question!!,
                        onNext = { viewModel.loadNextQuestion() },
                        onPrevious = { viewModel.loadPreviousQuestion() },
                        viewModel = viewModel,
                        context = context,
                        isQuestionAnswered = isQuestionAnswered
                    )
                }

                QuestionType.P07 -> {
                    P07(
                        question = question!!,
                        onNext = { viewModel.loadNextQuestion() },
                        onPrevious = { viewModel.loadPreviousQuestion() },
                        viewModel = viewModel,
                        context = context,
                        isQuestionAnswered = isQuestionAnswered
                    )
                }

                QuestionType.P08 -> {
                    P08(
                        question = question!!,
                        onNext = { viewModel.loadNextQuestion() },
                        onPrevious = { viewModel.loadPreviousQuestion() },
                        viewModel = viewModel,
                        context = context,
                        isQuestionAnswered = isQuestionAnswered
                    )
                }

                else -> {
                    Text("Unsupported question type.")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Flechas de navegación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.loadPreviousQuestion() },
                        enabled = currentQuestionIndex > 0, // Deshabilitar si es la primera pregunta
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("⬅\uFE0F")
                    }
                    Button(
                        onClick = { viewModel.loadNextQuestion() },
                        enabled = currentQuestionIndex < totalQuestions - 1, // Deshabilitar si es la última pregunta
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("➡\uFE0F")
                    }
                }

                // Botón "Finish Quiz"
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre las flechas y el botón
                Button(
                    onClick = onQuizFinished,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finish Quiz")
                }
            }
        }
    }
}



@Composable
fun PresentationScreen(
    quiz: Quiz?,
    creatorName: String,
    onStartQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        quiz?.let {
            Text(
                text = it.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Time Limit: ${it.timeLimit} minutes",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Number of Questions: ${it.questions.size}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Created by: $creatorName",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar la imagen del quiz si está disponible
            it.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Quiz Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            } ?: run {
                // Mostrar un ícono predeterminado si no hay imagen
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "No image selected",
                    tint = Color.Gray,
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        Button(
            onClick = onStartQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Quiz")
        }
    }
}


enum class Screen {
    Start,
    Question,
    Result,
    Waiting,
    Presentation
}






