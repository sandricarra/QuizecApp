package pt.isec.ams.quizec.data.models

data class User(
    val id: String = "",
    val name: String = "",
    val participatedQuizzes: List<String> = listOf() // Lista de IDs de los cuestionarios en los que ha participado
)