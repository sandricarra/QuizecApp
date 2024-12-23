package pt.isec.ams.quizec.data.models

import User
import com.google.firebase.firestore.GeoPoint



data class Quiz(
    val id: String = "",
    val title: String = "", // Título del cuestionario
    val description: String = "", // Descripción del cuestionario
    val creatorId: String = "", // ID del creador del cuestionario
    val questions: List<String> = listOf(), // Lista de IDs de preguntas asociadas
    val createdAt: Long = System.currentTimeMillis(), // Timestamp de creación
    val timeLimit: Int? = null, // Límite de tiempo del cuestionario
    val imageUrl: String? = null, // Imagen opcional
    val isGeolocationRestricted: Boolean = false,
    val location: GeoPoint? = null,// Si la geolocalización está activada
    val isAccessControlled: Boolean = false, // Si la adicionais está activa, para esperar a que esten todos listos o no
    val showResultsImmediately: Boolean =true, // Si se muestran los resultados inmediatamente al terminar
    val participants: List<String> = listOf(), // Lista de IDs de participantes, no se como usarlo aun
    val status: QuizStatus = QuizStatus.AVAILABLE, // Estado del cuestionario (disponible, en progreso, finalizado)
    val answers: Map<String, List<Answer>>  = mapOf() // Mapa de respuestas por ID de pregunta y lista de respuestas tampoco se como usarlo aun
)

