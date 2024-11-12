package pt.isec.ams.quizec.data.models

class Question (
    val questionId: String,
    val title: String,
    val type: String,  // Tipo de pregunta (P01, P02, etc.)
    val options: List<String> = listOf(),
    val correctAnswers: List<String> = listOf()
)