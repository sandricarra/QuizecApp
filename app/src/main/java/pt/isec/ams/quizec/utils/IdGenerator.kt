package pt.isec.ams.quizec.utils

object IdGenerator {
    // Función para generar un ID único alfanumérico de 6 caracteres
    fun generateUniqueQuizId(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }
}


