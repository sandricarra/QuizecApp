package pt.isec.ams.quizec.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.Quiz

class QuizScreenViewModel : ViewModel() {

    // Estado para isGeolocationRestricted
    private val _isGeolocationRestricted = MutableStateFlow(false) // Valor inicial
    val isGeolocationRestricted: StateFlow<Boolean> get() = _isGeolocationRestricted


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

    // Función para cargar el cuestionario y la primera pregunta
    fun loadQuizAndFirstQuestion(quizId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        firestore.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val quiz = document.toObject(Quiz::class.java)
                    _quiz.value = quiz

                    // Cargar la primera pregunta a partir de los IDs de las preguntas en el quiz
                    quiz?.questions?.firstOrNull()?.let { questionId ->
                        loadQuestionById(questionId)
                    } ?: run {
                        _isLoading.value = false
                    }
                } else {
                    _errorMessage.value = "Quiz not found"
                    _isLoading.value = false
                }
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
            }
    }

    // Función para cargar una pregunta usando el ID de la pregunta
    private fun loadQuestionById(questionId: String) {
        firestore.collection("questions").document(questionId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val question = document.toObject(Question::class.java)
                    _question.value = question

                    // Actualizar el índice de la pregunta actual
                    _quiz.value?.questions?.let { questions ->
                        _currentQuestionIndex.value = questions.indexOf(questionId)
                    } ?: run {
                        _currentQuestionIndex.value = -1
                    }
                } else {
                    _errorMessage.value = "Question not found"
                }
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
            }
    }

    // Función para registrar una respuesta correcta
    fun registerCorrectAnswer() {
        _correctAnswers.value += 1
    }


    fun loadNextQuestion() {
        val currentIndex = currentQuestionIndex.value
        val quiz = quiz.value // Accedemos al quiz aquí para asegurarnos de que no sea null
        if (currentIndex != -1 && quiz != null) {  // Verificamos que quiz no sea null
            val nextIndex = currentIndex + 1
            val nextQuestionId =
                quiz.questions.getOrNull(nextIndex) // Accedemos a la lista de IDs de preguntas
            if (nextQuestionId != null) {
                loadQuestionById(nextQuestionId)
            } else {
                _isQuizFinished.value = true
            }
        }
    }

    // Función para cargar la pregunta anterior
    fun loadPreviousQuestion() {
        val currentIndex = currentQuestionIndex.value
        val quiz = quiz.value // Accedemos al quiz aquí para asegurarnos de que no sea null
        if (currentIndex > 0 && quiz != null) {  // Verificamos que quiz no sea null
            val previousIndex = currentIndex - 1
            val previousQuestionId =
                quiz.questions.getOrNull(previousIndex) // Accedemos a la lista de IDs de preguntas
            if (previousQuestionId != null) {
                loadQuestionById(previousQuestionId)
            }
        } else {
            _isQuizFinished.value = true
        }
    }
}


