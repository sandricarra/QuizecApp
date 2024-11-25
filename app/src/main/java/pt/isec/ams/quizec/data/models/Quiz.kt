package pt.isec.ams.quizec.model

data class Quiz(
    val id: String = "",
    val creatorId: String = "", // ID del creador del cuestionario
    val title: String = "",
    val description: String = "",
    val questions: List<String> = listOf(), // Lista de IDs de preguntas asociadas
    val imageUrl: String? = null, // Imagen opcional
    val isGeolocationRestricted: Boolean = false, // Si la geolocalización está activada
    val startTime: Long = 0L, // Hora de inicio
    val endTime: Long = 0L, // Hora de finalización
    val resultVisibility: Boolean = false // Si los resultados son visibles inmediatamente después de responder
)

