package pt.isec.ams.quizec.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.isec.ams.quizec.model.Quiz
import pt.isec.ams.quizec.ui.screens.Question
import java.util.UUID


// Data class para representar un cuestionario


// Enum para representar los posibles estados de la operación
sealed class QuizCreationState {
    object Idle : QuizCreationState()
    object Loading : QuizCreationState()
    data class Success(val quizId: String) : QuizCreationState()
    data class Error(val message: String) : QuizCreationState()
}


class QuizCreationViewModel : ViewModel() {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val user = hashMapOf(
        "first" to "Ada",
        "last" to "Lovelace",
        "born" to 1815
    )

 fun main(){
     val user = hashMapOf(
         "first" to "Ada",
         "last" to "Lovelace",
         "born" to 1815
     )

     db.collection("users")
         .add(user)
         .addOnSuccessListener { documentReference ->
             Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
         }
         .addOnFailureListener { e ->
             Log.w(TAG, "Error adding document", e)
         }

 }


    val database = FirebaseDatabase.getInstance()
    val myref = database.getReference("Quiz")

    // Estado de la operación (cargando, éxito, error)
    private val _state = MutableStateFlow<QuizCreationState>(QuizCreationState.Idle)
    val state: StateFlow<QuizCreationState> = _state

    // Estado para almacenar los datos del cuestionario
    private val _quizTitle = MutableStateFlow("")
    val quizTitle: StateFlow<String> = _quizTitle

    private val _quizDescription = MutableStateFlow("")
    val quizDescription: StateFlow<String> = _quizDescription

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl

    private val firestore = FirebaseFirestore.getInstance()

    // Método para actualizar el título
    fun setQuizTitle(title: String) {
        _quizTitle.value = title
    }

    // Método para actualizar la descripción
    fun setQuizDescription(description: String) {
        _quizDescription.value = description
    }

    // Método para actualizar la URL de la imagen
    fun setImageUrl(url: String?) {
        _imageUrl.value = url
    }

    // Método para generar un identificador único para el cuestionario
    private fun generateQuizId(): String {
        return UUID.randomUUID().toString().substring(0, 6) // Genera un ID de 6 caracteres
    }

    // Método para crear un nuevo cuestionario
    fun createQuiz(): Quiz {
        val id = generateQuizId()
        return Quiz(
            id = id,
            title = quizTitle.value,
            description = quizDescription.value,
            imageUrl = imageUrl.value
        )
    }


    fun saveQuiz(quiz: Quiz, questions: List<Question>) {
        val quizData = hashMapOf(
            "title" to quiz.title,
            "description" to quiz.description,
            "imageUrl" to quiz.imageUrl,
            "questions" to questions.map { question ->
                hashMapOf(
                    "questionText" to question.questionText,
                    "questionType" to question.questionType.name,
                    "options" to question.options,
                    "correctAnswers" to question.correctAnswers
                )
            }
        )

        firestore.collection("quizzes")
            .add(quizData)
            .addOnSuccessListener { documentReference ->
                // El cuestionario se ha guardado exitosamente
                // Aquí puedes agregar lógica adicional si es necesario
            }
            .addOnFailureListener { e ->
                // Error al guardar el cuestionario
                // Mostrar mensaje de error o manejarlo de alguna manera
            }
    }

}