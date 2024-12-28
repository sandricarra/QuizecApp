package pt.isec.ams.quizec.ui.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.data.models.QuizResult
import pt.isec.ams.quizec.data.models.QuizStatus
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class QuizScreenViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message
    private val _quizNotFound = MutableLiveData<Boolean>(false)
    val quizNotFound: LiveData<Boolean> get() = _quizNotFound




    // Estado para almacenar el índice de la pregunta actual
    private val _currentQuestionIndex = mutableStateOf(0) // Usa MutableState
    val currentQuestionIndex: State<Int> = _currentQuestionIndex




    private val _userLocation = mutableStateOf<Pair<Double, Double>?>(null)
    val userLocation: State<Pair<Double, Double>?> = _userLocation

    private val initialPlayingUsers = mutableSetOf<String>()

    private var timerJob: Job? = null
    private val _quiz = MutableStateFlow<Quiz?>(null)
    val quiz: StateFlow<Quiz?> = _quiz

    private val _question = MutableStateFlow<Question?>(null)
    val question: StateFlow<Question?> = _question

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished

    private val _timeRemaining = MutableStateFlow<Long?>(null)
    val timeRemaining: StateFlow<Long?> = _timeRemaining

    private val _correctAnswers = MutableStateFlow(0)
    val correctAnswers: StateFlow<Int> = _correctAnswers

    private val _quizStatus = MutableStateFlow(QuizStatus.AVAILABLE)
    val quizStatus: StateFlow<QuizStatus> = _quizStatus

    private val _isQuestionAnswered = mutableStateOf(false) // Estado para controlar si la pregunta ha sido respondida
    val isQuestionAnswered: State<Boolean> = _isQuestionAnswered

    private val _answeredQuestions = mutableSetOf<String>()
    @SuppressLint("MissingPermission")
    fun getLocation(fusedLocationProviderClient: FusedLocationProviderClient, onLocationRetrieved: (GeoPoint?) -> Unit) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationRetrieved(GeoPoint(location.latitude, location.longitude))
                } else {
                    onLocationRetrieved(null)
                }
            }
            .addOnFailureListener {
                onLocationRetrieved(null)
            }
    }

    // Función para obtener la restricción de geolocalización y las coordenadas del quiz
    fun getQuizGeolocationRestriction(quizId: String, callback: (Boolean, GeoPoint?) -> Unit) {
        viewModelScope.launch {
            try {
                val quizSnapshot = firestore.collection("quizzes").document(quizId).get().await()
                val isGeolocationRestricted = quizSnapshot.getBoolean("isGeolocationRestricted") ?: false
                val geoPoint = quizSnapshot.get("location") as? GeoPoint
                callback(isGeolocationRestricted, geoPoint)
            } catch (e: Exception) {
                callback(false, null)
            }
        }
    }

    // Función para calcular la distancia entre dos puntos geográficos
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radio de la Tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    // Función para verificar si el usuario está dentro del rango permitido
    fun isUserWithinRange(userLocation: GeoPoint?, quizLocation: GeoPoint?): Boolean {
        if (userLocation == null || quizLocation == null) return false
        val distance = calculateDistance(
            userLocation.latitude,
            userLocation.longitude,
            quizLocation.latitude,
            quizLocation.longitude
        )
        return distance <= 20 // 20 km
    }







    fun loadQuiz(quizId: String, context: Context,creatorId : String) {
        _isLoading.value = true
        _errorMessage.value = null

        hasUserPlayedQuiz(quizId, creatorId) { hasPlayed ->
            if (hasPlayed) {
                Toast.makeText(context, "You have already played this quiz", Toast.LENGTH_LONG).show()
                _isLoading.value = false
            } else {
                getQuizGeolocationRestriction(quizId) { isGeolocationRestricted ->
                    if (isGeolocationRestricted) {
                        if (_userLocation.value == null) {
                            Toast.makeText(
                                context,
                                "Unable to fetch your location. Please enable location services.",
                                Toast.LENGTH_LONG
                            ).show()
                            _isLoading.value = false
                            return@getQuizGeolocationRestriction
                        }

                        getQuizLocation(quizId) { quizLocation ->
                            if (quizLocation == null) {
                                Toast.makeText(context, "Quiz location not found.", Toast.LENGTH_LONG).show()
                                _isLoading.value = false
                                return@getQuizLocation
                            }

                            val distance = FloatArray(1)
                            Location.distanceBetween(
                                _userLocation.value!!.first,
                                _userLocation.value!!.second,
                                quizLocation.first,
                                quizLocation.second,
                                distance
                            )

                            if (distance[0] > 20000) {
                                Toast.makeText(
                                    context,
                                    "You are not within the allowed region (20 km) to play this quiz.",
                                    Toast.LENGTH_LONG
                                ).show()
                                _isLoading.value = false
                                return@getQuizLocation
                            }

                            loadQuizData(quizId)
                        }
                    } else {
                        loadQuizData(quizId)
                    }
                }
            }
        }
    }
    private fun loadQuizData(quizId: String) {
        firestore.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val quiz = document.toObject(Quiz::class.java)
                    _quiz.value = quiz
                    quiz?.questions?.firstOrNull()?.let { loadQuestionById(it) }
                    _timeRemaining.value = quiz?.timeLimit?.toLong()?.times(60)
                    _quizNotFound.value = false // Quiz found
                } else {
                    handleError("Quiz not found")
                    _quizNotFound.value = true // Quiz not found
                }
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                handleError(exception.message ?: "Error loading quiz")
                _isLoading.value = false
                _quizNotFound.value = true
            }
    }





    fun observeQuizStatus(quizId: String) {
        firestore.collection("quizzes").document(quizId)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    // Manejar el error si es necesario
                    return@addSnapshotListener
                }

                documentSnapshot?.let { document ->
                    val status = document.getString("status")?.let {
                        QuizStatus.valueOf(it)
                    } ?: QuizStatus.LOCKED

                    _quizStatus.value = status
                }
            }
    }

    fun startTimer() {
        timerJob?.cancel() // Cancelar cualquier temporizador existente
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value != null && _timeRemaining.value!! > 0) {
                delay(1000L) // Esperar 1 segundo
                _timeRemaining.value = _timeRemaining.value?.minus(1)
            }
            if (_timeRemaining.value == 0L) {
                finishQuiz()
            }
        }
    }
    fun stopTimer() {
        timerJob?.cancel()
    }

    fun checkQuizStatus(quizId: String) {
        firestore.collection("quizzes")
            .document(quizId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val accessControlled = document.getBoolean("accessControlled") ?: true
                    val status = document.getString("status") ?: "LOCKED"

                    if (!accessControlled) {
                        _quizStatus.value = QuizStatus.AVAILABLE
                    } else {
                        _quizStatus.value = QuizStatus.valueOf(status)
                    }
                } else {
                    handleError("Quiz not found")
                }
            }
            .addOnFailureListener { exception ->
                handleError(exception.message ?: "Error checking quiz status")
            }
    }


    private fun loadQuestionById(questionId: String) {
        _isLoading.value = true // Inicia el estado de carga
        firestore.collection("questions").document(questionId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val question = document.toObject(Question::class.java)
                    if (question != null) {
                        _question.value = question
                        updateQuestionIndex(questionId)
                        Log.d("QuestionScreen", "Image URL: ${question.imageUrl}")

                    } else {
                        handleError("Question data is null")
                    }
                } else {
                    handleError("Question with ID $questionId not found")
                }
                _isLoading.value = false // Termina el estado de carga
            }
            .addOnFailureListener { exception ->
                handleError("Error loading question: ${exception.message}")
                _isLoading.value = false // Termina el estado de carga
            }
    }

    private fun updateQuestionIndex(questionId: String) {
        val index = _quiz.value?.questions?.indexOf(questionId) ?: -1
        if (index != -1) {
            _currentQuestionIndex.value = index
        } else {
            handleError("Question ID $questionId not found in the quiz")
        }
    }

    private fun handleError(message: String) {
        _errorMessage.value = message
        _isLoading.value = false
    }

    fun loadNextQuestion() {
        val totalQuestions = _quiz.value?.questions?.size ?: 0
        if (_currentQuestionIndex.value < totalQuestions - 1) {
            _currentQuestionIndex.value += 1
            _isQuestionAnswered.value = false // Reiniciar el estado al cambiar de pregunta
            loadQuestionById(_quiz.value?.questions?.get(_currentQuestionIndex.value) ?: return)
        }
    }

    fun loadPreviousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
            _isQuestionAnswered.value = false // Reiniciar el estado al cambiar de pregunta
            loadQuestionById(_quiz.value?.questions?.get(_currentQuestionIndex.value) ?: return)
        }
    }

    fun isQuestionAnswered(questionId: String): Boolean {
        return questionId in _answeredQuestions // Verificar si la pregunta ya ha sido respondida
    }

    fun markQuestionAsAnswered(questionId: String) {
        _answeredQuestions.add(questionId) // Marcar la pregunta como respondida
    }
    private fun navigateToQuestion(step: Int) {
        val currentIndex = currentQuestionIndex.value
        val questions = quiz.value?.questions ?: return

        val newIndex = currentIndex + step
        if (newIndex in questions.indices) {
            loadQuestionById(questions[newIndex])
        } else if (step > 0) {
            _isQuizFinished.value = true
        }
    }

    fun finishQuiz() {
        _isQuizFinished.value = true
        _isLoading.value = false
        stopTimer() // Detener cualquier indicador de carga
    }

    fun decrementTimeRemaining() {
        _timeRemaining.value = _timeRemaining.value?.minus(1)?.coerceAtLeast(0)
        if (_timeRemaining.value == 0L) {
            finishQuiz()
        }
    }

    // Función para registrar una respuesta correcta
    fun registerCorrectAnswer() {
        _correctAnswers.value += 1
    }
    fun getQuizGeolocationRestriction(quizId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val quizSnapshot = FirebaseFirestore.getInstance()
                    .collection("quizzes")
                    .document(quizId)
                    .get()
                    .await()

                val isGeolocationRestricted = quizSnapshot.getBoolean("isGeolocationRestricted") ?: false
                callback(isGeolocationRestricted)
            } catch (e: Exception) {
                _message.value = "Failed to fetch quiz geolocation restriction: ${e.message}"
                callback(false)
            }
        }
    }
    fun getQuizLocation(quizId: String, callback: (Pair<Double, Double>?) -> Unit) {
        viewModelScope.launch {
            try {
                val quizSnapshot = FirebaseFirestore.getInstance()
                    .collection("quizzes")
                    .document(quizId)
                    .get()
                    .await()

                // Obtener la ubicación como un objeto GeoPoint
                val geoPoint = quizSnapshot.get("location") as? com.google.firebase.firestore.GeoPoint
                if (geoPoint != null) {
                    callback(Pair(geoPoint.latitude, geoPoint.longitude))
                } else {
                    callback(null)
                }
            } catch (e: Exception) {
                _message.value = "Failed to fetch quiz location: ${e.message}"
                callback(null)
            }
        }
    }



    fun addUserToWaitingList(quizId: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val quizRef = db.collection("quizzes").document(quizId)

        quizRef.update("participants", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                println("User $userId added to waiting list for quiz $quizId.")
            }
            .addOnFailureListener { exception ->
                println("Failed to add user to waiting list: ${exception.message}")
            }
    }
    fun removeUserFromWaitingList(quizId: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val quizRef = db.collection("quizzes").document(quizId)

        quizRef.update("participants", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                println("User $userId removed from waiting list for quiz $quizId.")
            }
            .addOnFailureListener { exception ->
                println("Failed to remove user from waiting list: ${exception.message}")
            }
    }
    fun addUserToPlayingList(quizId: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val quizRef = db.collection("quizzes").document(quizId)

        quizRef.update("playingUsers", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                println("User $userId added to players list for quiz $quizId.")
            }
            .addOnFailureListener { exception ->
                println("Failed to add user to player list: ${exception.message}")
            }
    }
    fun removeUserFromPlayingList(quizId: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val quizRef = db.collection("quizzes").document(quizId)

        quizRef.update("playingUsers", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                println("User $userId removed from player list for quiz $quizId.")
            }
            .addOnFailureListener { exception ->
                println("Failed to remove user from player list: ${exception.message}")
            }
    }
    fun updateQuizStatus(quizId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val quizRef = db.collection("quizzes").document(quizId)
            val quizSnapshot = quizRef.get().await()

            if (!quizSnapshot.exists()) {
                _message.value = "Quiz not found."
                return@launch
            }

            val playingUsers = quizSnapshot.get("playingUsers") as? List<String> ?: emptyList()

            // Cambiar el estado a IN_PROGRESS si hay jugadores
            if (playingUsers.isNotEmpty() && _quizStatus.value != QuizStatus.IN_PROGRESS) {
                quizRef.update("status", QuizStatus.IN_PROGRESS.name).await()
                _quizStatus.value = QuizStatus.IN_PROGRESS
                initialPlayingUsers.clear()
                initialPlayingUsers.addAll(playingUsers)
            }

            // Cambiar el estado a FINISHED solo si todos los jugadores iniciales han salido
            if (initialPlayingUsers.isNotEmpty() && initialPlayingUsers.all { it !in playingUsers }) {
                quizRef.update("status", QuizStatus.FINISHED.name).await()
                _quizStatus.value = QuizStatus.FINISHED
                _timeRemaining.value = 0L // Establecer el tiempo restante a cero
                initialPlayingUsers.clear() // Limpiar la lista inicial
            }

            _message.value = "Quiz status updated successfully!"
        }
    }

    fun updateQuizStatusToFinished(quizId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val quizRef = db.collection("quizzes").document(quizId)
                val quizSnapshot = quizRef.get().await()

                if (!quizSnapshot.exists()) {
                    _errorMessage.value = "Quiz not found."
                    return@launch
                }

                val playingUsers = quizSnapshot.get("playingUsers") as? List<String> ?: emptyList()

                // Cambiar el estado a FINISHED solo si todos los jugadores iniciales han salido
                if (initialPlayingUsers.isNotEmpty() && initialPlayingUsers.all { it !in playingUsers }) {
                    quizRef.update("status", QuizStatus.FINISHED.name).await()

                    _message.value = "Quiz status updated to FINISHED successfully!"
                    _timeRemaining.value = 0L // Establecer el tiempo restante a cero
                    initialPlayingUsers.clear() // Limpiar la lista inicial
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update quiz status: ${e.message}"
            }
        }
    }

    fun saveQuizResult(quizId: String, userId: String, correctAnswers: Int, totalQuestions: Int) {
        // Consultar el número de intentos anteriores
        firestore.collection("results")
            .whereEqualTo("quizId", quizId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val attemptNumber = querySnapshot.size() + 1 // Incrementar el contador

                // Crear el objeto QuizResult
                val quizResult = QuizResult(
                    quizId = quizId,
                    userId = userId,
                    correctAnswers = correctAnswers,
                    totalQuestions = totalQuestions,
                    attemptNumber = attemptNumber
                )

                // Guardar los resultados en Firebase
                firestore.collection("results")
                    .add(quizResult)
                    .addOnSuccessListener { documentReference ->
                        Log.d("QuizScreenViewModel", "Quiz result saved with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("QuizScreenViewModel", "Error saving quiz result: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizScreenViewModel", "Error querying previous attempts: ${exception.message}")
            }
    }
    fun hasUserPlayedQuiz(quizId: String, userId: String, onResult: (Boolean) -> Unit) {
        firestore.collection("results")
            .whereEqualTo("quizId", quizId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(querySnapshot.documents.isNotEmpty())
            }
            .addOnFailureListener { exception ->
                // Manejar el error si es necesario
                onResult(false)
            }
    }
    fun updateQuizStatusToFinished2(quizId: String) {
        viewModelScope.launch {
            try {
                val quizRef = firestore.collection("quizzes").document(quizId)
                quizRef.update("status", QuizStatus.FINISHED.name)
                    .addOnSuccessListener {
                        _quizStatus.value = QuizStatus.FINISHED
                        Log.d("QuizScreenViewModel", "Quiz status updated to FINISHED")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("QuizScreenViewModel", "Failed to update quiz status: ${exception.message}")
                    }
            } catch (e: Exception) {
                Log.e("QuizScreenViewModel", "Error updating quiz status: ${e.message}")
            }
        }
    }
}

















