package pt.isec.ams.quizec.viewmodel

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

class QuizScreenViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Estado para almacenar el cuestionario
    private val _quiz = mutableStateOf<Quiz?>(null)
    val quiz: State<Quiz?> = _quiz
    private val _quizStatus = mutableStateOf(QuizStatus.LOCKED)
    val quizStatus: State<QuizStatus> = _quizStatus

    // Estado para almacenar la pregunta actual
    private val _question = mutableStateOf<Question?>(null)
    val question: State<Question?> = _question

    // Estado de carga
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Estado de error
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Estado para almacenar el índice de la pregunta actual
    private val _currentQuestionIndex = mutableStateOf<Int>(-1)
    val currentQuestionIndex: State<Int> = _currentQuestionIndex

    // Estado para almacenar las respuestas correctas
    private val _correctAnswers = mutableStateOf(0)
    val correctAnswers: State<Int> = _correctAnswers

    // Estado para indicar si el cuestionario ha terminado
    private val _isQuizFinished = mutableStateOf(false)
    val isQuizFinished: State<Boolean> = _isQuizFinished

    // Estado para el tiempo restante
    private val _timeRemaining = mutableStateOf<Long?>(null)
    val timeRemaining: State<Long?> = _timeRemaining

    private val initialPlayingUsers = mutableSetOf<String>()

    private var timerJob: Job? = null

    fun observeQuizStatus(quizId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("quizzes").document(quizId)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    _errorMessage.value = "Failed to load quiz status: ${exception.message}"
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
    // Función para cargar el cuestionario y la primera pregunta
    fun loadQuizAndFirstQuestion(quizId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        firestore.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val quiz = document.toObject(Quiz::class.java)
                    _quiz.value = quiz
                    quiz?.questions?.firstOrNull()?.let { loadQuestionById(it) }
                    _timeRemaining.value = quiz?.timeLimit?.toLong()?.times(60) // minutes to seconds
                } else {
                    handleError("Quiz not found")
                }
            }
            .addOnFailureListener { exception ->
                handleError(exception.message ?: "Error loading quiz")
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
        navigateToQuestion(1)
    }

    fun loadPreviousQuestion() {
        navigateToQuestion(-1)
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
        val db = FirebaseFirestore.getInstance()
        db.collection("results")
            .whereEqualTo("quizId", quizId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Llamar al callback con el resultado
                onResult(querySnapshot.documents.isNotEmpty())
            }
            .addOnFailureListener { exception ->
                // En caso de error, se puede manejar aquí
                onResult(false) // O manejar el error de otra manera
            }
    }



}















