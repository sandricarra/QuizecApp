package pt.isec.ams.quizec.data.models

import pt.isec.ams.quizec.data.models.QuestionType

// Clase para representar una pregunta
data class Question(
    val questionText: String, // Texto de la pregunta
    val questionType: QuestionType, // Tipo de la pregunta (P01, P02, etc.)
    val options: List<String> = emptyList(), // Opciones para preguntas tipo opción múltiple
    val correctAnswers: List<String> = emptyList() // Respuestas correctas, puede ser una lista de índices o respuestas en texto
) {
    // Método adicional para manejar las respuestas correctas como índices en preguntas tipo opción múltiple
    fun getCorrectAnswerIndices(): List<Int> {
        return options.mapIndexedNotNull { index, option ->
            if (correctAnswers.contains(option)) index else null
        }
    }

    // Método adicional para verificar si una respuesta es correcta
    fun isCorrectAnswer(answer: String): Boolean {
        return correctAnswers.contains(answer)
    }
}
