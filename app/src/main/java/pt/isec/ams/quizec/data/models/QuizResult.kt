package pt.isec.ams.quizec.data.models

data class QuizResult(
    val quizId: String, // ID del quiz
    val userId: String, // ID del usuario
    val correctAnswers: Int, // Número de respuestas correctas
    val totalQuestions: Int,
    val attemptNumber: Int // Número de intento
)