package pt.isec.ams.quizec.data.models

import pt.isec.ams.quizec.data.models.Answer
import pt.isec.ams.quizec.data.models.QuizStatus

data class Quiz(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val creatorId: String = "",
    val questions: List<String> = listOf(),
    val timeLimit: Int = 0,// Lista de IDs de preguntas asociadas
    val imageUrl: String? = null, // Imagen opcional
    val isGeolocationRestricted: Boolean = false, // Si la geolocalización está activada
    val isAccessControlled: Boolean = false,
    val showResultsImmediately: Boolean =true,
    val participants: List<String> = listOf(),
    val status: QuizStatus = QuizStatus.AVAILABLE,
    val answers: Map<String, List<Answer>>  = mapOf()


)

