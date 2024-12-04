package pt.isec.ams.quizec.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.data.models.Question
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
    val quiz by viewModel.quiz
    val question by viewModel.question
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // Obtener el contexto actual
    val context = LocalContext.current

    // Verificar permisos
    val permissionStatus = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Si el permiso no ha sido otorgado, pedirlo
    if (permissionStatus != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Solicitar permiso para acceder a la ubicación
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    // Función para agregar un participante a la lista
    fun addParticipant(quizId: String, userId: String) {
        FirebaseFirestore.getInstance().collection("quizzes").document(quizId)
            .update("participants", FieldValue.arrayUnion(userId))  // Agregar ID sin duplicar
            .addOnSuccessListener {
                Log.d("QuizAccess", "User added to participants list.")
            }
            .addOnFailureListener { exception ->
                Log.e("QuizAccess", "Error adding participant: ${exception.message}")
            }
    }

    // Función para verificar si el acceso es autorizado
    fun checkAccess(quizId: String) {
        FirebaseFirestore.getInstance().collection("quizzes").document(quizId)
            .get()
            .addOnSuccessListener { document ->
                val quiz = document.toObject(Quiz::class.java)
                if (quiz != null) {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    if (currentUserId != null && !quiz.participants.contains(currentUserId)) {
                        addParticipant(quizId, currentUserId)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizAccess", "Error loading quiz: ${exception.message}")
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

    // Comprobar ubicación
    fun checkLocation(quizId: String) {
        val locationUtils = LocationUtils(context)
        locationUtils.getUserLocation(
            onLocationReceived = { userLocation ->
                // Obtener la ubicación del creador desde Firestore
                FirebaseFirestore.getInstance().collection("quizzes").document(quizId)
                    .get()
                    .addOnSuccessListener { document ->
                        val quiz = document.toObject(Quiz::class.java)
                        if (quiz != null && quiz.location != null) {
                            val creatorLocation = quiz.location
                            val distance = calculateDistance(
                                userLocation.latitude,
                                userLocation.longitude,
                                creatorLocation.latitude,
                                creatorLocation.longitude
                            )

                            if (distance <= 500) {
                                isLocationValidState = true
                                locationError = ""
                                isLocationValid()
                            } else {
                                locationError = "You are too far from the creator to access this quiz."
                                isLocationValidState = false
                                onError(locationError)
                            }
                        } else {
                            locationError = "Creator location not available."
                            isLocationValidState = false
                            onError(locationError)
                        }
                    }
                    .addOnFailureListener { exception ->
                        locationError = "Error loading quiz: ${exception.message}"
                        isLocationValidState = false
                        onError(locationError)
                    }
            },
            onError = { error ->
                locationError = error.message ?: "Error obtaining location"
                isLocationValidState = false
                onError(locationError)
            }
        )
    }



    // Función para cargar el cuestionario y agregar un participante
    fun loadQuiz() {
        if (quizId.isNotBlank()) {
            viewModel.loadQuizAndFirstQuestion(quizId)
            // Verificar si la restricción de geolocalización está activada
            if (isGeolocationRestricted) {
                checkLocation(quizId)
            } else {
                isLocationValidState = true
                isLocationValid()
            }
        }
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

        // Mostrar el error de ubicación si ocurre
        if (locationError.isNotEmpty()) {
            Text(text = locationError, color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        // Mostrar mensaje de ubicación válida
        if (isLocationValidState) {
            Text(text = "You are within range to access this quiz.", color = Color.Green)
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
                QuestionType.P01 -> {
                    P01(question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })

                }
                QuestionType.P02 -> {
                    P02(question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                QuestionType.P03 -> {
                    P03(question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                QuestionType.P04 -> {
                    P04 (question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                QuestionType.P05 -> {


                }
                QuestionType.P06 -> {


                }
                QuestionType.P07 -> {


                }
                QuestionType.P08 -> {


                }
                else -> {
                    Text("Unsupported question type.")
                }
            }
        }
    }
}

@Composable
fun P01(question: Question, onNext: () -> Unit, onPrevious: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

        // Mostrar imagen si está disponible
        question.imageUrl?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Question Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Crop
            )
        }

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


@Composable fun P02(question: Question, onNext: () -> Unit, onPrevious: () -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)
        question.options.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                RadioButton(
                    selected = false,
                    onClick = { /* Handle answer selection */ }
                )
                Text(
                    text = option,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onPrevious) {
                    Text("Previous Question")
                }
                Button(onClick = onNext) {
                    Text("Next Question")
                }
            }
        }
    }
}

@Composable fun P03(question: Question, onNext: () -> Unit, onPrevious: () -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)
        question.options.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                RadioButton(
                    selected = false,
                    onClick = { /* Handle answer selection */ }
                )
                Text(
                    text = option,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onPrevious) {
                    Text("Previous Question")
                }
                Button(onClick = onNext) {
                    Text("Next Question")
                }
            }
        }
    }
}

@Composable
fun P04(question: Question, onNext: () -> Unit, onPrevious: () -> Unit) {
    val (selectedPairs, setSelectedPairs) = remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    val (expandedIndex, setExpandedIndex) = remember { mutableStateOf(-1) }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

        // Mostrar imagen si está disponible
        question.imageUrl?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Question Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Dividir las opciones en pares
        val pairs = question.options.map { option ->
            val parts = option.split(" -> ")
            parts[0] to parts[1]
        }

        Text("Match the following:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))

        pairs.forEachIndexed { index, (left, _) ->
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf("") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = left, modifier = Modifier.weight(1f))

                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedOption.isEmpty()) "Select an option" else selectedOption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .background(Color.LightGray)
                            .padding(8.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        pairs.forEach { (_, right) ->
                            DropdownMenuItem(
                                    onClick = {
                                    },
                                    text = { Text(right) }
                                )
                                selectedOption = right
                                expanded = false
                                val updatedPairs = selectedPairs.toMutableList()
                                if (index < updatedPairs.size) {
                                    updatedPairs[index] = left to right
                                } else {
                                    updatedPairs.add(left to right)
                                }
                                setSelectedPairs(updatedPairs)
                            }
                        }
                    }
                }
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





