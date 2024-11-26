package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.isec.ams.quizec.data.models.Quiz
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizHistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> get() = _quizzes

    init {
        loadQuizzes()
    }

    private fun loadQuizzes() {
        db.collection("quizzes")
            .get()
            .addOnSuccessListener { result ->
                val quizList = result.documents.mapNotNull { it.toObject(Quiz::class.java) }
                _quizzes.value = quizList
            }
            .addOnFailureListener { exception ->
                // Manejar errores de carga
                println("Error al cargar el historial: ${exception.message}")
            }
    }

    // Formatear la fecha de creación del cuestionario
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

