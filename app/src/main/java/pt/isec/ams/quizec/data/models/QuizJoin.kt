package pt.isec.ams.quizec.data.models


data class QuizJoin(
    val roomId: String = "", // ID de la sala
    val userId: String = "", // ID del jugador que se une
    val joinTime: Long = System.currentTimeMillis() // Hora en la que el jugador se une
)
