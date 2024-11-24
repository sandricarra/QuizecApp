package pt.isec.ams.quizec.data.models

import pt.isec.ams.quizec.data.models.QuestionType

// Clase para representar una pregunta
data class Question(
    val questionText: String, // Texto de la pregunta
    val questionType: QuestionType, // Tipo de pregunta
    val options: List<String> = emptyList(), // Opciones para la respuesta (si aplica)
    val correctAnswers: List<Int> = emptyList(), // Índices de las respuestas correctas
    val additionalInfo: Any? = null // Información adicional (por ejemplo, imágenes o relaciones entre columnas)
){



}
