package pt.isec.ams.quizec.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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
                    P05 (question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                QuestionType.P06 -> {
                    P06 (question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                QuestionType.P07 -> {
                    P07 (question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                QuestionType.P08 -> {
                    P08 (question = question!!, onNext = { nextQuestion() }, onPrevious = { previousQuestion() })
                }
                else -> {
                    Text("Unsupported question type.")
                }
            }
        }
    }
}











