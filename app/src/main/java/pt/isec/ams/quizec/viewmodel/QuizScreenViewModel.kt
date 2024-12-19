package pt.isec.ams.quizec.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.Quiz
class QuizScreenViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // Estado para almacenar el cuestionario
    private val _quiz = mutableStateOf<Quiz?>(null)
    val quiz: State<Quiz?> = _quiz

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
        _isLoading.value = false // Detener cualquier indicador de carga
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
}




