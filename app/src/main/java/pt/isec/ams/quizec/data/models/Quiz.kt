package pt.isec.ams.quizec.data.models

class Quiz(
    val quizId: String,
    val title: String,
    val description: String,
    val questions: List<Question>,
    val creatorId: String
)