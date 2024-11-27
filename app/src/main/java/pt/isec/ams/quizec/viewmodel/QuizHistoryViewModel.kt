package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.utils.IdGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizHistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    private val _filteredQuizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val filteredQuizzes: StateFlow<List<Quiz>> get() = _filteredQuizzes

    var selectedStatus = "All"

    init {
        loadQuizzes()
    }

    private fun loadQuizzes() {
        db.collection("quizzes")
            .get()
            .addOnSuccessListener { result ->
                val quizList = result.documents.mapNotNull { it.toObject(Quiz::class.java) }
                _quizzes.value = quizList
                _filteredQuizzes.value = quizList
            }
            .addOnFailureListener { exception ->
                // Manejar errores de carga
                println("Error al cargar el historial: ${exception.message}")
            }
    }
    fun filterByQuery(query: String) {
        _filteredQuizzes.value = _quizzes.value.filter { it.title.contains(query, ignoreCase = true) }
    }

    fun filterByStatus(status: String) {
        selectedStatus = status
        _filteredQuizzes.value = if (status == "All") {
            _quizzes.value
        } else {
            _quizzes.value.filter { it.status.name == status.uppercase() }
        }
    }

    // Formatear la fecha de creación del cuestionario
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    fun deleteQuiz(quizId: String) {
        db.collection("quizzes").document(quizId)
            .delete()
            .addOnSuccessListener {
                loadQuizzes()
            }
            .addOnFailureListener { exception ->
                println("Error al eliminar el cuestionario: ${exception.message}")
            }
    }

    fun duplicateQuiz(quiz: Quiz) {
        val newQuiz = quiz.copy(
            id = IdGenerator.generateUniqueQuizId(), // Usa la clase utilitaria
            title = "${quiz.title} (Duplicado)"
        )
        db.collection("quizzes").document(newQuiz.id)
            .set(newQuiz)
            .addOnSuccessListener {
                loadQuizzes()
            }
            .addOnFailureListener { exception ->
                println("Error al duplicar el cuestionario: ${exception.message}")
            }
    }

}

