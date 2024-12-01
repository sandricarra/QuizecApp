package pt.isec.ams.quizec.viewmodel

import android.content.Context
import android.location.Location
import android.util.Log
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
                // Si no hay más preguntas, puedes manejar la finalización del cuestionario aquí
                _errorMessage.value = "No more questions."
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
            // Si no hay pregunta anterior, puedes manejar esto aquí si lo deseas
            _errorMessage.value = "No previous question."
        }
    }



}



