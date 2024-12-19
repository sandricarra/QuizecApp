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

    // Instancia de Firestore para interactuar con la base de datos
    private val db = FirebaseFirestore.getInstance()

    // Lista de cuestionarios cargados desde Firestore
    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    private val _filteredQuizzes = MutableStateFlow<List<Quiz>>(emptyList())

    // Exposición pública de los cuestionarios filtrados
    val filteredQuizzes: StateFlow<List<Quiz>> get() = _filteredQuizzes

    // Estado seleccionado para filtrar cuestionarios
    var selectedStatus = "All"

    init {
        // Cargar los cuestionarios al inicializar el ViewModel
        loadQuizzes()
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

    // Función para filtrar los cuestionarios según el texto ingresado
    fun filterByQuery(query: String) {
        _filteredQuizzes.value = _quizzes.value.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }

    // Función para filtrar los cuestionarios por estado
    fun filterByStatus(status: String) {
        selectedStatus = status
        _filteredQuizzes.value = if (status == "All") {
            _quizzes.value
        } else {
            _quizzes.value.filter { it.status.name == status.uppercase() }
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

    // Función para duplicar un cuestionario
    fun duplicateQuiz(quiz: Quiz) {
        val newQuiz = quiz.copy(
            id = IdGenerator.generateUniqueQuizId(), // Generar un nuevo ID único
            title = "${quiz.title} (Duplicate)" // Modificar el título
        )
        db.collection("quizzes").document(newQuiz.id)
            .set(newQuiz)
            .addOnSuccessListener {
                // Recargar la lista de cuestionarios tras duplicar
                loadQuizzes()
            }
            .addOnFailureListener { exception ->
                println("Error duplicating quiz: ${exception.message}")
            }
    }
}
