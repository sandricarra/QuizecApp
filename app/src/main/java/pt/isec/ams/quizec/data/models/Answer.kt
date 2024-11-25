package pt.isec.ams.quizec.data.models

data class Answer(
    val questionId: String,
    val selectedOptions: List<String>,
    val studentId: String
)