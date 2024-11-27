package pt.isec.ams.quizec.data.models

data class Answer(
    val id: String = "",              // Identificador único de la respuesta
    val userId: String,               // Identificador del usuario que responde
    val quizId: String,               // Identificador del cuestionario
    val questionId: String,           // Identificador de la pregunta a la que se responde
    val answer: String,               // Respuesta del usuario (puede ser un texto, opción seleccionada, etc.)
    val timestamp: Long               // Timestamp de cuando se respondió
)