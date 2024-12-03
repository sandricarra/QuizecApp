package pt.isec.ams.quizec.viewmodel

import android.content.Context
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.utils.IdGenerator
import pt.isec.ams.quizec.utils.IdGeneratorQ
import pt.isec.ams.quizec.utils.LocationUtils

class QuizCreationViewModel : ViewModel() {

    // Instancia de Firebase Firestore para interactuar con la base de datos
    private val firestore = FirebaseFirestore.getInstance()

    // Lista mutable de preguntas del cuestionario
    private val _questions = mutableStateListOf<Question>()


    // Exposición de la lista de preguntas como solo lectura
    val questions: List<Question> get() = _questions

    // Estado para isGeolocationRestricted
    private val _isGeolocationRestricted = mutableStateOf(false)
    val isGeolocationRestricted: State<Boolean> get() = _isGeolocationRestricted

    private fun generateUniqueQuizId(): String {
        return IdGenerator.generateUniqueQuizId() // Usa la clase utilitaria
    }
    private fun generateUniqueQId(): String {
        return IdGeneratorQ.generateUniqueQuizCode() // Usa la clase utilitaria
    }

    // Función para actualizar isGeolocationRestricted
    fun setGeolocationRestricted(value: Boolean) {
        _isGeolocationRestricted.value = value
    }

    private val creatorLocation = Location("creator").apply {
        latitude = 40.7128 // Latitud del creador
        longitude = -74.0060 // Longitud del creador
    }

    private val _locationError = mutableStateOf("")
    val locationError: State<String> get() = _locationError

    private val _isLocationValid = mutableStateOf(false)
    val isLocationValid: State<Boolean> get() = _isLocationValid
    // Función para guardar un cuestionario en Firebase Firestore
    fun saveQuiz(
        title: String,  // Título del cuestionario
        description: String,  // Descripción del cuestionario
        //questions: List<String>,  // Lista de preguntas del cuestionario
        imageUrl: String?,  // URL de la imagen asociada al cuestionario
        isGeolocationRestricted: Boolean,  // Si el cuestionario está restringido por geolocalización
        timeLimit: Int,  // Límite de tiempo del cuestionario en minutos
        isAccessControlled: Boolean,  // Si el acceso al cuestionario está controlado
        showResultsImmediately: Boolean,  // Si los resultados se muestran inmediatamente
        creatorId: String,  // ID del creador del cuestionario
        onSuccess: (String) -> Unit,  // Función que se ejecuta al guardar correctamente
        onError: (Exception) -> Unit  // Función que se ejecuta si ocurre un error
    ) {
        viewModelScope.launch {
            // Generar un ID único para el cuestionario
            val quizId = generateUniqueQuizId()

            // Guardar todas las preguntas asociadas al cuestionario
            val questionIds = _questions.map { question ->
                question.copy(quizId = quizId) // Asociar la pregunta al cuestionario
            }.map { question ->
                firestore.collection("questions").document(question.id)
                    .set(question)
                    .addOnFailureListener { e -> println("Error saving question: ${e.message}") }
                question.id
            }

            // Crear el objeto Quiz
            val quiz = Quiz(
                id = quizId,
                creatorId = creatorId,
                title = title,
                description = description,
                questions = questionIds, // Asociar los IDs de las preguntas al cuestionario
                imageUrl = imageUrl,
                isGeolocationRestricted = isGeolocationRestricted,
                timeLimit = timeLimit,
                isAccessControlled = isAccessControlled,
                showResultsImmediately = showResultsImmediately,
                location = if (isGeolocationRestricted) GeoPoint(creatorLocation.latitude, creatorLocation.longitude) else null // Guardar la ubicación del creador si está restringido

            )

            // Guardar el cuestionario en Firestore
            firestore.collection("quizzes")
                .document(quizId)  // Usar el ID único generado para el documento
                .set(quiz)  // Guardar el cuestionario
                .addOnSuccessListener { onSuccess(quizId) }  // Llamar a onSuccess si se guarda correctamente
                .addOnFailureListener { onError(it) }  // Llamar a onError si ocurre un error
        }
    }


    // Función para agregar una nueva pregunta al cuestionario
    fun addQuestion(
        type: QuestionType,  // Tipo de la pregunta (por ejemplo, opción múltiple, verdadero/falso, etc.)
        title: String,  // Título o enunciado de la pregunta
        options: List<String>,  // Opciones de respuesta de la pregunta
        correctAnswers: List<String>,  // Respuestas correctas
        imageUrl: String?  // URL de la imagen asociada a la pregunta (opcional)
    ) {
        // Generar un ID único para la nueva pregunta
        val questionId = generateUniqueQId()

        // Crear el objeto Question con los datos proporcionados
        val newQuestion = Question(
            id = questionId,  // ID único generado
            title = title,  // Título de la pregunta
            type = type,  // Tipo de la pregunta
            options = options,  // Opciones de respuesta
            correctAnswers = correctAnswers,  // Respuestas correctas
            imageUrl = imageUrl  // Imagen asociada (si existe)
        )

        _questions.add(newQuestion) // Agregar a la lista local



    }

    fun updateAccessCode(quizId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        // Genera un código único para el quiz (quizId como código de acceso)
        val newAccessCode = quizId  // Usamos el quizId como código de acceso

        // Actualiza el campo `accessCode` en Firestore para este cuestionario
        firestore.collection("quizzes").document(quizId)
            .update("accessCode", newAccessCode)
            .addOnSuccessListener {
                onSuccess() // Llamar a onSuccess cuando se actualiza correctamente
            }
            .addOnFailureListener { exception ->
                onError(exception) // Llamar a onError si ocurre algún problema
            }
    }
/*
    // Verificar si el estudiante está cerca del creador
    // Función para verificar la ubicación del estudiante
    fun checkLocationPermissionAndDistance(
        context: Context,
        onValidLocation: () -> Unit,
        onError: (String) -> Unit
    ) {
        val fusedLocationClient = LocationUtils(context)
        fusedLocationClient.getUserLocation(
            onLocationReceived = { location ->
                // Obtener la ubicación del creador desde Firestore
                val creatorLocation = Location("creator").apply {
                    latitude = 40.7128  // Latitud del creador
                    longitude = -74.0060 // Longitud del creador
                }
                val distance = calculateDistance(
                    location.latitude,
                    location.longitude,
                    creatorLocation.latitude,
                    creatorLocation.longitude
                )

                if (distance <= 500) {  // Si la distancia es menor o igual a 500 metros
                    onValidLocation()
                    _isLocationValid.value = true
                    _locationError.value = "" // Limpiar el mensaje de error
                } else {
                    onError("You are too far from the creator to access this quiz.")
                    _isLocationValid.value = false
                    _locationError.value = "You are too far from the creator to access this quiz."
                }
            },
            onError = { errorMessage ->
                onError(errorMessage.toString())
                _isLocationValid.value = false
                _locationError.value = errorMessage.toString()
            }
        )
    } --> Creo que no lo necesito REVISAR!!!*/


    // Calcular distancia entre dos ubicaciones geográficas
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val location1 = Location("")
        location1.latitude = lat1
        location1.longitude = lon1

        val location2 = Location("")
        location2.latitude = lat2
        location2.longitude = lon2

        return location1.distanceTo(location2) // Distancia en metros
    }


}


