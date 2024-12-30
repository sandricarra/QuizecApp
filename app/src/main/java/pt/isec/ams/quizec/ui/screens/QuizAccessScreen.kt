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
import android.util.Log
import pt.isec.ams.quizec.data.models.QuizStatus
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.perf.util.Timer
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.Quiz


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizAccessScreen(navController: NavController,
                     viewModel: QuizScreenViewModel = viewModel(), creatorId: String
) {



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



    // Solicitar permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("QuizAccessScreen", "Location permission granted")
            // Obtener la ubicación del usuario
            viewModel.getLocation(fusedLocationProviderClient) { geoPoint ->
                if (geoPoint != null) {
                    userLocation = geoPoint
                    Log.d("QuizAccessScreen", "User location: $geoPoint")
                } else {
                    Log.d("QuizAccessScreen", "Failed to get user location")
                }
            }
        } else {
            Log.d("QuizAccessScreen", "Location permission denied")
            Toast.makeText(context, "Location permission is required to play this quiz.", Toast.LENGTH_LONG).show()
        }
    }

    // Solicitar permisos al abrir la pantalla
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.d("QuizAccessScreen", "Location permission already granted")
            // Obtener la ubicación del usuario
            viewModel.getLocation(fusedLocationProviderClient) { geoPoint ->
                if (geoPoint != null) {
                    userLocation = geoPoint
                    Log.d("QuizAccessScreen", "User location: $geoPoint")
                } else {
                    Log.d("QuizAccessScreen", "Failed to get user location")
                }
            }
        }
    }

    // Iniciar el contador cuando el cuestionario se cargue
    LaunchedEffect(timeRemaining) {
        if (timeRemaining != null && timeRemaining!! > 0) {
            while (timeRemaining!! > 0) {
                delay(1000L) // Esperar 1 segundo
                viewModel.decrementTimeRemaining()
            }
            // Finalizar el cuestionario cuando el tiempo se agote



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



    LaunchedEffect(quizStatus) {
        Log.d("QuizAccessScreen", "Quiz status changed to: $quizStatus")
        if (quizStatus == QuizStatus.LOCKED && quiz?.isAccessControlled == true) {
            Log.d("QuizAccessScreen", "Switching to WaitingScreen")
            currentScreen = Screen.Waiting
        }
    }

    LaunchedEffect(quizId) {
        if (quizId.isNotBlank()) {
            Log.d("QuizAccessScreen", "Observing quiz status for quizId: $quizId")
            viewModel.observeQuizStatus(quizId)
        }
    }



    LaunchedEffect(quizStatus) {
        if (quizStatus == QuizStatus.AVAILABLE && currentScreen == Screen.Waiting) {
            currentScreen = Screen.Presentation
            viewModel.loadQuiz(quizId, context,creatorId)

        }
    }
    LaunchedEffect(quizStatus) {
        if (quizStatus == QuizStatus.FINISHED && currentScreen == Screen.Question) {
            Log.d("QuizAccessScreen", "Quiz finished, saving results...")
            viewModel.saveQuizResult(quizId, creatorId, viewModel.correctAnswers.value, quiz?.questions?.size ?: 0)
            viewModel.removeUserFromPlayingList(quizId, creatorId)
            viewModel.finishQuiz()
            viewModel.updateQuizStatusToFinished(quizId)
            viewModel.removeUserFromWaitingList(quizId, creatorId)
            currentScreen = Screen.Result
        }
    }






    when (currentScreen) {

        Screen.Start -> {
            StartScreen(
                quizId = quizId,
                onQuizIdChange = { quizId = it },

                creatorId = creatorId,
                quizNotFound = errorMessage != null,
                onStartQuiz = {
                    viewModel.hasUserPlayedQuiz(quizId, creatorId) { hasPlayed ->
                        if (hasPlayed) {
                            Toast.makeText(context, "You have already played this quiz.", Toast.LENGTH_LONG).show()
                        } else {
                            Log.d("QuizAccessScreen", "Starting quiz with ID: $quizId")
                            viewModel.getQuizGeolocationRestriction(quizId) { isGeolocationRestricted, quizLocation ->
                                if (isGeolocationRestricted) {
                                    Log.d("QuizAccessScreen", "Quiz has geolocation restriction")
                                    if (userLocation == null) {
                                        Toast.makeText(context, "Unable to fetch your location. Please enable location services.", Toast.LENGTH_LONG).show()
                                        return@getQuizGeolocationRestriction
                                    }
                                    if (quizLocation == null) {
                                        Toast.makeText(context, "Quiz location not found.", Toast.LENGTH_LONG).show()
                                        return@getQuizGeolocationRestriction
                                    }
                                    val distance = viewModel.calculateDistance(
                                        userLocation!!.latitude,
                                        userLocation!!.longitude,
                                        quizLocation.latitude,
                                        quizLocation.longitude
                                    )
                                    Log.d("QuizAccessScreen", "Distance to quiz: $distance km")
                                    if (distance > 20) {
                                        Toast.makeText(context, "You are not within the allowed region (20 km) to play this quiz.", Toast.LENGTH_LONG).show()
                                        return@getQuizGeolocationRestriction
                                    }
                                }
                                if (quizStatus == QuizStatus.LOCKED) {
                                    currentScreen = Screen.Waiting
                                    Log.d("QuizAccessScreen", "Switching to WaitingScreen")
                                } else {
                                    currentScreen = Screen.Presentation
                                    viewModel.loadQuiz(quizId, context, creatorId)
                                    Log.d("QuizAccessScreen", "Loading quiz with ID: $quizId")
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
                creatorId = quiz?.creatorId.toString(), // Reemplaza con la lógica para obtener el nombre del creador
                onStartQuiz = {
                    currentScreen = Screen.Question

                    viewModel.startTimer()
                    viewModel.removeUserFromWaitingList(quizId, creatorId)
                    viewModel.addUserToPlayingList(quizId, creatorId)
                    viewModel.updateQuizStatus(quizId)
                    isQuizLoaded = false

                },
                viewModel = viewModel

            )
        }

        Screen.Question -> {
            QuestionScreen(
                viewModel = viewModel,
                onQuizFinished = { currentScreen = Screen.Result

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
            text = stringResource(R.string.waiting_message),
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
    onStartQuiz: () -> Unit,
    quizNotFound: Boolean // Nuevo parámetro para controlar si el quiz no se encontró
) {
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Comprobaciones de validación
    val isQuizIdValid = quizId.length == 6 && quizId.all { it.isUpperCase() || it.isDigit() }

    // Muestra el mensaje de error si el quiz no fue encontrado
    if (quizNotFound) {
        errorMessage.value = stringResource(R.string.quiz_not_found_error)
    }

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
                text = stringResource(R.string.start_playing),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {
            TextField(
                value = quizId,
                onValueChange = {
                    errorMessage.value = null // Limpiar mensaje de error cuando se cambia el ID
                    onQuizIdChange(it)
                },
                label = { Text(stringResource(R.string.enter_quiz_id)) },
                placeholder = { Text(stringResource(R.string.quiz_id_placeholder)) },
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
                onClick = {
                   onStartQuiz()
                },
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
        errorMessage.value?.let { message ->
            item {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        item {
            Text(
                text = stringResource(R.string.proceed_message),
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
            text = stringResource(R.string.quiz_finished),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el tiempo restante
        if (timeRemaining != null) {
            Text(
                text = stringResource(R.string.time_remaining) + ": ${timeRemaining!! / 60}:${timeRemaining!! % 60}",
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
            Text(stringResource(R.string.check_answers))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExitQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.exit_quiz))
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
                text = errorMessage ?: stringResource(R.string.unknown_error),
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
                    text = stringResource(R.string.time_remaining) + ": ${timeRemaining!! / 60}:${timeRemaining!! % 60}",
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
                        isQuestionAnswered = isQuestionAnswered,
                        viewModel = viewModel

                    )
                }

                QuestionType.P06 -> {
                    P06(
                        question = question!!,
                        isQuestionAnswered = isQuestionAnswered,
                        viewModel = viewModel

                    )
                }

                QuestionType.P07 -> {
                    P07(
                        question = question!!,
                        isQuestionAnswered = isQuestionAnswered,
                        viewModel = viewModel

                    )
                }

                QuestionType.P08 -> {
                    P08(
                        question = question!!,
                        isQuestionAnswered = isQuestionAnswered,
                        viewModel = viewModel

                    )
                }

                else -> {
                    Text(stringResource(R.string.unsupported_question_type))
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
                    Text(stringResource(R.string.finish_quiz))
                }
            }
        }
    }
}



@Composable
fun PresentationScreen(
    quiz: Quiz?,
    creatorId: String,
    onStartQuiz: () -> Unit,
    viewModel: QuizScreenViewModel
) {
    // Estado para almacenar el nombre del creador
    val creatorName = remember { mutableStateOf("Loading...") }

    // Efecto para cargar el nombre del creador
    LaunchedEffect(creatorId) {
        viewModel.getCreatorName(creatorId) { name ->
            if (name != null) {
                creatorName.value = name
            }
        }
    }

    // Colores y estilos (ajustados a azul clarito)
    val backgroundColor = Color(0xFFE3F2FD) // Fondo azul claro
    val cardColor = Color.White // Color de la tarjeta
    val primaryColor = Color(0xFF2196F3) // Azul clarito
    val textColor = Color.Black // Color del texto

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título del quiz
                Text(
                    text = quiz?.title ?: "Quiz Title",
                    style = MaterialTheme.typography.headlineLarge,
                    color = primaryColor,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                // Imagen del quiz
                quiz?.imageUrl?.let { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Quiz Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } ?: run {
                    // Icono predeterminado si no hay imagen
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "No image selected",
                        tint = Color.Gray,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Detalles del quiz
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tiempo límite
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Time Limit",
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Time Limit: ${quiz?.timeLimit} minutes",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                    }

                    // Número de preguntas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Number of Questions",
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Questions: ${quiz?.questions?.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                    }

                    // Creador del quiz
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Creator",
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Created by: ${creatorName.value}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                    }
                }

                // Botón de inicio
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onStartQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Start Quiz",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
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





