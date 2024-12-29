package pt.isec.ams.quizec.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
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

class QuizCreationViewModel(
    private val savedStateHandle: SavedStateHandle // Añade SavedStateHandle
) : ViewModel() {


    private val firestore = FirebaseFirestore.getInstance()

    // Lista mutable de preguntas del cuestionario
    private val _questions = mutableStateListOf<Question>()
    val questions: List<Question> get() = _questions
    // Título del cuestionario
    // Usa mutableStateOf para las propiedades
    var title: MutableState<String> = mutableStateOf(savedStateHandle.get<String>("title") ?: "")
        private set

    var description: MutableState<String> = mutableStateOf(savedStateHandle.get<String>("description") ?: "")
        private set

    var imageUrl: MutableState<String?> = mutableStateOf(savedStateHandle.get<String>("imageUrl"))
        private set

    var timeLimit: MutableState<Long?> = mutableStateOf(savedStateHandle.get<Long>("timeLimit"))
        private set

    var isGeolocationRestricted: MutableState<Boolean> = mutableStateOf(savedStateHandle.get<Boolean>("isGeolocationRestricted") ?: false)
        private set

    var creatorLocation: MutableState<GeoPoint?> = mutableStateOf(
        savedStateHandle.get<Pair<Double, Double>>("creatorLocation")?.toGeoPoint()
    )
        private set


    var isAccessControlled: MutableState<Boolean> = mutableStateOf(savedStateHandle.get<Boolean>("isAccessControlled") ?: false)
        private set

    var showResultsImmediately: MutableState<Boolean> = mutableStateOf(savedStateHandle.get<Boolean>("showResultsImmediately") ?: false)
        private set

    var questionType: MutableState<QuestionType?> = mutableStateOf(savedStateHandle.get<QuestionType>("questionType"))
        private set

    var questionTitle: MutableState<String> = mutableStateOf(savedStateHandle.get<String>("questionTitle") ?: "")
        private set
    var imageUri: MutableState<String?> = mutableStateOf(savedStateHandle.get<String>("imageUrl"))
        private set
    // Función para guardar los datos en SavedStateHandle
    private fun saveState() {
        savedStateHandle["title"] = title.value
        savedStateHandle["description"] = description.value
        savedStateHandle["imageUrl"] = imageUrl.value
        savedStateHandle["timeLimit"] = timeLimit.value
        savedStateHandle["isGeolocationRestricted"] = isGeolocationRestricted.value
        savedStateHandle["creatorLocation"] = creatorLocation.value?.toPair()
        savedStateHandle["isAccessControlled"] = isAccessControlled.value
        savedStateHandle["showResultsImmediately"] = showResultsImmediately.value
        savedStateHandle["questionType"] = questionType.value
        savedStateHandle["questionTitle"] = questionTitle.value
        savedStateHandle["imageUri"] = imageUri.value
    }

    // Función para actualizar el título
    fun updateTitle(newTitle: String) {
        title.value = newTitle
        saveState()
    }
    fun updateImageUri(newImageUri: String?) {
        imageUri.value = newImageUri
        saveState()
    }

    // Función para actualizar la descripción
    fun updateDescription(newDescription: String) {
        description.value = newDescription
        saveState()
    }
    fun updateImageUrl(newImageUrl: String?) {
        imageUrl.value = newImageUrl
        saveState()
    }
    fun updateTimeLimit(newTimeLimit: Long?) {
        timeLimit.value = newTimeLimit
        saveState()
    }
    fun updateIsGeolocationRestricted(newIsGeolocationRestricted: Boolean) {
        isGeolocationRestricted.value = newIsGeolocationRestricted
        saveState()
    }
    fun updateCreatorLocation(location: GeoPoint?) {
        creatorLocation.value = location
        saveState()
    }

    fun updateIsAccessControlled(newIsAccessControlled: Boolean) {
        isAccessControlled.value = newIsAccessControlled
        saveState()
    }
    fun updateShowResultsImmediately(newShowResultsImmediately: Boolean) {
        showResultsImmediately.value = newShowResultsImmediately
        saveState()
    }
    fun updateQuestionType(newQuestionType: QuestionType?) {
        questionType.value= newQuestionType
        saveState()
    }
    fun updateQuestionTitle(newQuestionTitle: String) {
        questionTitle.value = newQuestionTitle
        saveState()
    }
    fun updateQuestions(newQuestions: List<Question>) {
        _questions.clear()
        _questions.addAll(newQuestions)
        saveState()
    }

    fun GeoPoint.toPair(): Pair<Double, Double> {
        return Pair(this.latitude, this.longitude)
    }

    // Función para convertir Pair<Double, Double> a GeoPoint
    fun Pair<Double, Double>.toGeoPoint(): GeoPoint {
        return GeoPoint(this.first, this.second)
    }

    // Lista temporal de preguntas
    private val _temporaryQuestions = mutableStateListOf<Question>()
    val temporaryQuestions: List<Question> get() = _temporaryQuestions

    fun removeQuestion(question: Question) {
        _temporaryQuestions.remove(question)
    }

    // Función para agregar una pregunta temporalmente
    fun addTemporaryQuestion(
        type: QuestionType,
        title: String,
        options: List<String>,
        correctAnswers: List<String>,
        imageUrl: String?
    ) {
        val questionId = generateUniqueQId()
        val newQuestion = Question(
            id = questionId,
            title = title,
            type = type,
            options = options,
            correctAnswers = correctAnswers,
            imageUrl = imageUrl
        )
        _temporaryQuestions.add(newQuestion)
    }








    // Instancia de Firebase Firestore para interactuar con la base de datos



    // Exposición de la lista de preguntas como solo lectura


    private fun generateUniqueQuizId(): String {
        return IdGenerator.generateUniqueQuizId() // Usa la clase utilitaria
    }

    private fun generateUniqueQId(): String {
        return IdGeneratorQ.generateUniqueQuizCode() // Usa la clase utilitaria
    }


    // Función para guardar un cuestionario en Firebase Firestore
    // Función para guardar el cuestionario y enviar las preguntas a Firestore
    fun saveQuiz(
        title: String,
        description: String,
        imageUrl: String?,
        isGeolocationRestricted: Boolean,
        location: GeoPoint?,
        timeLimit: Int?,
        isAccessControlled: Boolean,
        showResultsImmediately: Boolean,
        creatorId: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val quizId = generateUniqueQuizId()

            // Guardar todas las preguntas temporales en Firestore
            val questionIds = _temporaryQuestions.map { question ->
                question.copy(quizId = quizId)
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
                questions = questionIds,
                imageUrl = imageUrl,
                isGeolocationRestricted = isGeolocationRestricted,
                location = location,
                timeLimit = timeLimit,
                isAccessControlled = isAccessControlled,
                showResultsImmediately = showResultsImmediately
            )

            // Guardar el cuestionario en Firestore
            firestore.collection("quizzes")
                .document(quizId)
                .set(quiz)
                .addOnSuccessListener { onSuccess(quizId) }
                .addOnFailureListener { onError(it) }
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



