package pt.isec.ams.quizec.ui.viewmodel

import User
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.utils.IdGenerator
import pt.isec.ams.quizec.utils.IdGeneratorQ
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizHistoryViewModel : ViewModel() {

    // Instancia de Firestore para interactuar con la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Lista de cuestionarios cargados desde Firestore
    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    private val _filteredQuizzes = MutableStateFlow<List<Quiz>>(emptyList())

    // Exposición pública de los cuestionarios filtrados
    val filteredQuizzes: StateFlow<List<Quiz>> get() = _filteredQuizzes

    // Estado seleccionado para filtrar cuestionarios
    var selectedStatus = "All"
    var currentUser: User? = null // Usuario actual

    init {
        // Cargar los cuestionarios al inicializar el ViewModel
        loadQuizzes()
    }

    fun loadUser(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentUser = document.toObject(User::class.java)
                    loadQuizzes()
                }
            }
            .addOnFailureListener {
                println("Error loading user: ${it.message}")
            }
    }

    fun filterByStatus(status: String) {
        selectedStatus = status
        _filteredQuizzes.value = when (status) {
            "All" -> _quizzes.value
            "CreatedQuizzes" -> {
                currentUser?.let { user ->
                    _quizzes.value.filter { it.creatorId == user.id }
                } ?: emptyList()
            }
            "ParticipatedQuizzes" -> {
                currentUser?.let { user ->
                    _quizzes.value.filter { it.participants.contains(user.id) }
                } ?: emptyList()
            }
            else -> _quizzes.value
        }
    }



    // Función para cargar los cuestionarios desde Firestore
    private fun loadQuizzes() {
        db.collection("quizzes")
            .get()
            .addOnSuccessListener { result ->
                // Transformar los documentos obtenidos en objetos `Quiz`
                val quizList = result.documents.mapNotNull { it.toObject(Quiz::class.java) }
                _quizzes.value = quizList
                _filteredQuizzes.value = quizList // Inicialmente no hay filtro
            }
            .addOnFailureListener { exception ->
                // Manejar errores al cargar los cuestionarios
                println("Error loading quizzes: ${exception.message}")
            }
    }

    // Formatear la fecha de creación para mostrarla en la UI
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Función para eliminar un cuestionario y sus preguntas asociadas de Firestore
    fun deleteQuiz(quizId: String) {
        // Obtener el cuestionario
        db.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val quiz = document.toObject(Quiz::class.java)
                    if (quiz != null) {
                        // Eliminar todas las preguntas asociadas
                        val batch = db.batch()
                        quiz.questions.forEach { questionId ->
                            val questionRef = db.collection("questions").document(questionId)
                            batch.delete(questionRef)
                        }
                        // Eliminar el cuestionario
                        val quizRef = db.collection("quizzes").document(quizId)
                        batch.delete(quizRef)

                        // Ejecutar el batch
                        batch.commit()
                            .addOnSuccessListener {
                                // Recargar la lista de cuestionarios tras eliminar
                                loadQuizzes()
                            }
                            .addOnFailureListener { exception ->
                                println("Error deleting quiz and questions: ${exception.message}")
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error fetching quiz: ${exception.message}")
            }
    }

    fun duplicateQuiz(quiz: Quiz) {
        val newQuizId = IdGenerator.generateUniqueQuizId()
        val newQuiz = quiz.copy(
            id = newQuizId,
            title = "${quiz.title} (Duplicate)",
            questions = listOf()
        )

        db.collection("quizzes").document(newQuizId)
            .set(newQuiz)
            .addOnSuccessListener {
                duplicateQuestions(quiz.questions, newQuizId) { newQuestionIds ->
                    updateQuizWithNewQuestions(newQuizId, newQuestionIds)
                }
            }
            .addOnFailureListener { exception ->
                println("Error duplicating quiz: ${exception.message}")
            }
    }

    private fun duplicateQuestions(questionIds: List<String>, newQuizId: String, onComplete: (List<String>) -> Unit) {
        val newQuestionIds = mutableListOf<String>()
        questionIds.forEach { questionId ->
            db.collection("questions").document(questionId).get()
                .addOnSuccessListener { document ->
                    val question = document.toObject(Question::class.java)
                    if (question != null) {
                        val newQuestionId = IdGeneratorQ.generateUniqueQuizCode()
                        val newQuestion = question.copy(
                            id = newQuestionId,
                            quizId = newQuizId
                        )
                        db.collection("questions").document(newQuestionId)
                            .set(newQuestion)
                            .addOnSuccessListener {
                                newQuestionIds.add(newQuestionId)
                                if (newQuestionIds.size == questionIds.size) {
                                    onComplete(newQuestionIds)
                                }
                            }
                            .addOnFailureListener { exception ->
                                println("Error duplicating question: ${exception.message}")
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error fetching question: ${exception.message}")
                }
        }
    }



    private fun updateQuizWithNewQuestions(newQuizId: String, newQuestionIds: List<String>) {
        db.collection("quizzes").document(newQuizId)
            .update("questions", newQuestionIds)
            .addOnSuccessListener {
                loadQuizzes()
            }
            .addOnFailureListener { exception ->
                println("Error updating quiz with new questions: ${exception.message}")
            }
    }
}
