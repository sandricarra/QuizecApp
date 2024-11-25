package pt.isec.ams.quizec.data.models

data class QuizRoom(
    val roomId: String = "", // ID único de la sala
    val quizId: String = "", // ID del cuestionario seleccionado
    val creatorId: String = "", // ID del creador
    val startTime: Long = 0L, // Hora de inicio de la sala
    val isActive: Boolean = true // Indica si la sala está activa
) {
}