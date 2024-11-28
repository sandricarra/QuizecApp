package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.utils.IdGeneratorQ

class QuestionHistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Lista de preguntas del cuestionario
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> get() = _questions

    // Cargar las preguntas asociadas a un cuestionario específico
   /* fun loadQuestions(quizId: String) {
        // Primero cargamos el cuestionario para obtener sus IDs de preguntas
        db.collection("quizzes").document(quizId)
            .get()
            .addOnSuccessListener { document ->
                val quiz = document.toObject(Quiz::class.java)
                if (quiz != null) {
                    loadQuestionsByIds(quiz.questions) // Cargar preguntas usando los IDs
                } else {
                    println("Quiz not found")
                }
            }
            .addOnFailureListener { exception ->
                println("Error loading quiz: ${exception.message}")
            }
    }*/

    // Cargar preguntas asociadas a un cuestionario
    fun loadQuestions(quizId: String) {
        db.collection("questions")
            .whereEqualTo("quizId", quizId) // Filtrar preguntas por `quizId`
            .get()
            .addOnSuccessListener { result ->
                val questionList = result.documents.mapNotNull { document ->
                    document.toObject(Question::class.java)?.copy(id = document.id)
                }
                _questions.value = questionList
            }
            .addOnFailureListener { exception ->
                println("Error loading questions: ${exception.message}")
            }
    }

    // Cargar preguntas específicas por sus IDs
    /*
    private fun loadQuestionsByIds(questionIds: List<String>) { // Corregido a List<String>
        if (questionIds.isEmpty()) {
            _questions.value = emptyList()
            return
        }

        db.collection("questions")
            .whereIn("id", questionIds) // Filtrar por IDs en la lista
            .get()
            .addOnSuccessListener { result ->
                val questionList = result.documents.mapNotNull { document ->
                    try {
                        document.toObject(Question::class.java)?.copy(id = document.id)
                    } catch (e: Exception) {
                        println("Error mapping question: ${e.message}")
                        null
                    }
                }
                _questions.value = questionList
            }
            .addOnFailureListener { exception ->
                println("Error loading questions: ${exception.message}")
            }
    }*/
// Duplicar una pregunta
    fun duplicateQuestion(question: Question, quizId: String) {
        // Generar un nuevo ID único para la pregunta duplicada
        val newQuestion = question.copy(
            id = IdGeneratorQ.generateUniqueQuizCode(), // Nuevo ID único
            title = "${question.title} (Duplicate)" // Modificar el título para indicar que es un duplicado
        )

        // Guardar la pregunta duplicada en Firestore
        db.collection("questions").document(newQuestion.id)
            .set(newQuestion)
            .addOnSuccessListener {
                // Recargar las preguntas tras duplicar
                loadQuestions(quizId)
            }
            .addOnFailureListener { exception ->
                println("Error duplicating question: ${exception.message}")
            }
    }

    // Eliminar una pregunta
    fun deleteQuestion(questionId: String, quizId: String) {
        db.collection("questions").document(questionId)
            .delete()
            .addOnSuccessListener {
                // Recargar las preguntas tras eliminar
                loadQuestions(quizId)
            }
            .addOnFailureListener { exception ->
                println("Error deleting question: ${exception.message}")
            }
    }
}




