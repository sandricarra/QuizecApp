package pt.isec.ams.quizec.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.data.models.Quiz

class QuizCreationViewModel : ViewModel() {

    // Instancia de Firebase Firestore para interactuar con la base de datos
    private val firestore = FirebaseFirestore.getInstance()

    // Lista mutable de preguntas del cuestionario
    private val _questions = mutableStateListOf<Question>()

    // Exposición de la lista de preguntas como solo lectura
    val questions: List<Question> get() = _questions

    // Función privada para generar un ID único para el cuestionario
    private fun generateUniqueQuizId(): String {
        // Define los caracteres disponibles para el ID
        val chars = ('A'..'Z') + ('0'..'9')
        // Genera un ID de 6 caracteres al azar
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }

    // Función privada para generar un ID único para cada pregunta
    private fun generateUniqueQuestionId(): String {
        // Utiliza el tiempo actual en milisegundos para generar un ID único
        return "q${System.currentTimeMillis()}"
    }

    // Función para guardar un cuestionario en Firebase Firestore
    fun saveQuiz(
        title: String,  // Título del cuestionario
        description: String,  // Descripción del cuestionario
        questions: List<String>,  // Lista de preguntas del cuestionario
        imageUrl: String?,  // URL de la imagen asociada al cuestionario
        isGeolocationRestricted: Boolean,  // Si el cuestionario está restringido por geolocalización
        timeLimit: Int,  // Límite de tiempo del cuestionario en minutos
        isAccessControlled: Boolean,  // Si el acceso al cuestionario está controlado
        showResultsImmediately: Boolean,  // Si los resultados se muestran inmediatamente
        creatorId: String,  // ID del creador del cuestionario
        onSuccess: () -> Unit,  // Función que se ejecuta al guardar correctamente
        onError: (Exception) -> Unit  // Función que se ejecuta si ocurre un error
    ) {
        viewModelScope.launch {
            // Generar un ID único para el cuestionario
            val quizId = generateUniqueQuizId()

            // Crear el objeto Quiz con los datos proporcionados
            val quiz = Quiz(
                id = quizId,
                creatorId = creatorId,
                title = title,
                description = description,
                questions = questions,
                imageUrl = imageUrl,
                isGeolocationRestricted = isGeolocationRestricted,
                timeLimit = timeLimit,
                isAccessControlled = isAccessControlled,
                showResultsImmediately = showResultsImmediately
            )

            // Guardar el cuestionario en Firestore
            firestore.collection("quizzes")
                .document(quizId)  // Usar el ID único generado para el documento
                .set(quiz)  // Guardar el cuestionario
                .addOnSuccessListener { onSuccess() }  // Llamar a onSuccess si se guarda correctamente
                .addOnFailureListener { onError(it) }  // Llamar a onError si ocurre un error
        }
    }

    // Función para agregar una nueva pregunta al cuestionario
    fun addQuestion(
        type: QuestionType,  // Tipo de la pregunta (por ejemplo, opción múltiple, verdadero/falso, etc.)
        title: String,  // Título o enunciado de la pregunta
        options: List<String>,  // Opciones de respuesta de la pregunta
        correctAnswers: List<String>,  // Respuestas correctas
        imageUrl: String?  // URL de la imagen asociada a la pregunta (opcional)
    ) {
        // Generar un ID único para la nueva pregunta
        val questionId = generateUniqueQuestionId()

        // Crear el objeto Question con los datos proporcionados
        val newQuestion = Question(
            id = questionId,  // ID único generado
            title = title,  // Título de la pregunta
            type = type,  // Tipo de la pregunta
            options = options,  // Opciones de respuesta
            correctAnswers = correctAnswers,  // Respuestas correctas
            imageUrl = imageUrl  // Imagen asociada (si existe)
        )

        // Guardar la pregunta en Firestore
        firestore.collection("questions")
            .document(questionId)  // Usar el ID único para el documento
            .set(newQuestion)  // Guardar la pregunta
            .addOnSuccessListener {
                // Si la pregunta se guarda correctamente, agregarla a la lista local de preguntas
                _questions.add(newQuestion)
            }
            .addOnFailureListener { e ->
                // Si ocurre un error, se maneja aquí (por ejemplo, imprimiendo el error)
                e.printStackTrace()
            }
    }
}
