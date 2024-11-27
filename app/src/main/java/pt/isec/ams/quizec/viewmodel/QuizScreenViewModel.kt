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

    // Estado para almacenar las preguntas del cuestionario
    private val _questions = mutableStateOf<List<Question>>(emptyList())
    val questions: State<List<Question>> = _questions

    // Estado de carga
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Estado de error
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Función para cargar el cuestionario y sus preguntas
    fun loadQuiz(quizId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        firestore.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val quiz = document.toObject(Quiz::class.java)
                    _quiz.value = quiz

                    // Cargar preguntas asociadas
                    quiz?.questions?.let { questionIds ->
                        loadQuestions(questionIds)
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

    private fun loadQuestions(questionIds: List<String>) {
        if (questionIds.isEmpty()) {
            _isLoading.value = false
            return
        }

        firestore.collection("questions").whereIn("id", questionIds).get()
            .addOnSuccessListener { snapshot ->
                val loadedQuestions = snapshot.documents.mapNotNull { it.toObject(Question::class.java) }
                _questions.value = loadedQuestions
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
                _isLoading.value = false
            }
    }
}


