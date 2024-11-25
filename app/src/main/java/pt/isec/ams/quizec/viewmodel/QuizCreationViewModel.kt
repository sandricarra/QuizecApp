package pt.isec.ams.quizec.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.model.Quiz

class QuizCreationViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // Declarar 'questions' como un MutableState
    var questions by mutableStateOf<List<Question>>(emptyList())
        private set

    // Generar un ID único alfanumérico de 6 caracteres
    private fun generateUniqueQuizId(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..6).map { chars.random() }.joinToString("")
    }

    private fun generateUniqueQuestionId(): String {
        return "q${System.currentTimeMillis()}"  // Usamos el tiempo actual como ID único
    }


    // Función para guardar el cuestionario en Firebase Firestore
    fun saveQuiz(
        title: String,
        description: String,
        questions: List<String>,
        imageUrl: String?,
        isGeolocationRestricted: Boolean,
        startTime: Long,
        endTime: Long,
        resultVisibility: Boolean,
        creatorId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val quizId = generateUniqueQuizId()
            val quiz = Quiz(
                id = quizId,
                creatorId = creatorId,
                title = title,
                description = description,
                questions = questions,
                imageUrl = imageUrl,
                isGeolocationRestricted = isGeolocationRestricted,
                startTime = startTime,
                endTime = endTime,
                resultVisibility = resultVisibility
            )

            firestore.collection("quizzes")
                .document(quizId)
                .set(quiz)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        }
    }
    // Función para agregar una pregunta
    fun addQuestion(type: QuestionType, title: String, options: List<String>, correctAnswers: List<String>) {
        val questionId = generateUniqueQuestionId()  // Generamos un ID único para la pregunta
        val newQuestion = Question(
            id = questionId,  // ID único generado
            title = title,    // El título de la pregunta
            type = type,      // El tipo de la pregunta (P01, P02, etc.)
            options = options, // Opciones de respuesta
            correctAnswers = correctAnswers // Respuestas correctas
        )

        // Guardar la pregunta en Firestore
        firestore.collection("questions")
            .document(questionId)
            .set(newQuestion)
            .addOnSuccessListener {
                // Si la pregunta se guarda correctamente, actualizamos la lista local de preguntas
                questions = questions + newQuestion
            }
            .addOnFailureListener { e ->
                // Si hay un error al guardar, puedes manejar el error aquí
                e.printStackTrace()
            }
    }
}


