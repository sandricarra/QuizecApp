package pt.isec.ams.quizec.model

data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null
)
