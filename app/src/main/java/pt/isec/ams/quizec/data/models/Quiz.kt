package pt.isec.ams.quizec.model

data class Quiz(
    val title: String,
    val description: String,
    val imageUrl: String? = null
)
