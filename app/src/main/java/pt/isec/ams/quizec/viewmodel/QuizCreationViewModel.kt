package pt.isec.ams.quizec.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.utils.IdGenerator
import pt.isec.ams.quizec.utils.IdGeneratorQ

class QuizCreationViewModel : ViewModel() {

    // Instancia de Firebase Firestore para interactuar con la base de datos
    private val firestore = FirebaseFirestore.getInstance()

    // Lista mutable de preguntas del cuestionario
    private val _questions = mutableStateListOf<Question>()


    // Exposición de la lista de preguntas como solo lectura
    val questions: List<Question> get() = _questions

    private fun generateUniqueQuizId(): String {
        return IdGenerator.generateUniqueQuizId() // Usa la clase utilitaria
    }
    private fun generateUniqueQId(): String {
        return IdGeneratorQ.generateUniqueQuizCode() // Usa la clase utilitaria
    }


    // Función para guardar un cuestionario en Firebase Firestore
    fun saveQuiz(
        title: String,  // Título del cuestionario
        description: String,  // Descripción del cuestionario
        //questions: List<String>,  // Lista de preguntas del cuestionario
        imageUrl: String?,  // URL de la imagen asociada al cuestionario
        isGeolocationRestricted: Boolean,  // Si el cuestionario está restringido por geolocalización
        timeLimit: Int,  // Límite de tiempo del cuestionario en minutos
        isAccessControlled: Boolean,  // Si el acceso al cuestionario está controlado
        showResultsImmediately: Boolean,  // Si los resultados se muestran inmediatamente
        creatorId: String,  // ID del creador del cuestionario
        onSuccess: (String) -> Unit,  // Función que se ejecuta al guardar correctamente
        onError: (Exception) -> Unit  // Función que se ejecuta si ocurre un error
    ) {
        viewModelScope.launch {
            // Generar un ID único para el cuestionario
            val quizId = generateUniqueQuizId()

            // Guardar todas las preguntas asociadas al cuestionario
            val questionIds = _questions.map { question ->
                question.copy(quizId = quizId) // Asociar la pregunta al cuestionario
            }.map { question ->
                firestore.collection("questions").document(question.id)
                    .set(question)
                    .addOnFailureListener { e -> println("Error saving question: ${e.message}") }
                question.id
            }

            // Crear el objeto Quiz
            val quiz = Quiz(
                id = quizId,
                creatorId = creatorId,
                title = title,
                description = description,
                questions = questionIds, // Asociar los IDs de las preguntas al cuestionario
                imageUrl = imageUrl,
                isGeolocationRestricted = isGeolocationRestricted,
                timeLimit = timeLimit,
                isAccessControlled = isAccessControlled,
                showResultsImmediately = showResultsImmediately,
            )

            // Guardar el cuestionario en Firestore
            firestore.collection("quizzes")
                .document(quizId)  // Usar el ID único generado para el documento
                .set(quiz)  // Guardar el cuestionario
                .addOnSuccessListener { onSuccess(quizId) }  // Llamar a onSuccess si se guarda correctamente
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
        val questionId = generateUniqueQId()

        // Crear el objeto Question con los datos proporcionados
        val newQuestion = Question(
            id = questionId,  // ID único generado
            title = title,  // Título de la pregunta
            type = type,  // Tipo de la pregunta
            options = options,  // Opciones de respuesta
            correctAnswers = correctAnswers,  // Respuestas correctas
            imageUrl = imageUrl  // Imagen asociada (si existe)
        )

        _questions.add(newQuestion) // Agregar a la lista local
    }

}


