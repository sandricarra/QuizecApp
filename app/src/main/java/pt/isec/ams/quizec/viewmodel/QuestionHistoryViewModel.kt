package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.utils.IdGeneratorQ

class QuestionHistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Lista de preguntas del cuestionario
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> get() = _questions

    private val _creatorId = MutableStateFlow<String?>(null)
    val creatorId: StateFlow<String?> = _creatorId

    // Obtener una pregunta específica por su ID y almacenarla en una lista (incluso si solo es una)
    suspend fun getQuestionById(questionId: String) {
        try {
            val document = db.collection("questions").document(questionId).get().await()
            val question = document.toObject(Question::class.java)?.copy(id = document.id)
            _questions.value = if (question != null) listOf(question) else emptyList()
        } catch (e: Exception) {
            println("Error getting question: ${e.message}")
            _questions.value = emptyList() // Si hay error, dejamos la lista vacía
        }
    }

    // Cargar preguntas asociadas a un cuestionario
    fun loadQuestions(quizId: String) {
        // Primero, cargar el cuestionario para obtener el creatorId
        db.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                val quiz = document.toObject(Quiz::class.java)
                _creatorId.value = quiz?.creatorId

                // Luego, cargar las preguntas asociadas al cuestionario
                db.collection("questions")
                    .whereEqualTo("quizId", quizId)
                    .get()
                    .addOnSuccessListener { result ->
                        val questionList = result.documents.mapNotNull { doc ->
                            doc.toObject(Question::class.java)?.copy(id = doc.id)
                        }
                        _questions.value = questionList
                    }
                    .addOnFailureListener { exception ->
                        println("Error loading questions: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                println("Error loading quiz: ${exception.message}")
            }
    }


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
    // Actualizar el título de una pregunta
    fun updateQuestionTitle(questionId: String, newTitle: String) {
        db.collection("questions").document(questionId)
            .update("title", newTitle)
            .addOnFailureListener { exception ->
                println("Error updating question title: ${exception.message}")
            }
    }

    // Actualizar las respuestas de una pregunta
    fun updateQuestionAnswer(questionId: String, newAnswers: List<String>) {
        db.collection("questions").document(questionId)
            .update("correctAnswers", newAnswers)
            .addOnFailureListener { exception ->
                println("Error updating question answers: ${exception.message}")
            }
    }

    // Actualizar la URL de la imagen de una pregunta
    fun updateQuestionImage(questionId: String, newImageUrl: String) {
        db.collection("questions").document(questionId)
            .update("imageUrl", newImageUrl)
            .addOnFailureListener { exception ->
                println("Error updating question image: ${exception.message}")
            }
    }

    // Actualizar las opciones de una pregunta
    fun updateQuestionOptions(questionId: String, newOptions: List<String>) {
        db.collection("questions").document(questionId)
            .update("options", newOptions)
            .addOnSuccessListener {
                // Si la actualización es exitosa
                println("Question options updated successfully.")
            }
            .addOnFailureListener { exception ->
                // Manejar errores
                println("Error updating question options: ${exception.message}")
            }
    }


}




