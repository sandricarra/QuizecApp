package pt.isec.ams.quizec.data.models



// Clase para representar una pregunta
data class Question(
    val id: String = "",
    val title: String = "",
    val type: QuestionType = QuestionType.P01, // Tipo de pregunta (P01, P02, P03, etc.)
    val options: List<String> = listOf(), // Opciones de respuesta
    val correctAnswers: List<String> = listOf(), // Respuestas correctas (para preguntas múltiples)
    val imageUrl: String? = null // Imagen opcional
)



