package pt.isec.ams.quizec.data.models

data class Response(
    val userId: String = "",
    val quizId: String = "",
    val answers: Map<String, List<String>> = mapOf(), // Mapa con el ID de la pregunta como clave y la respuesta (o respuestas) como valor
    val submissionTime: Long = 0L // Hora en la que se envió la respuesta
)