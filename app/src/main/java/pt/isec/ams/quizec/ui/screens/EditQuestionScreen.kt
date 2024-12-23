package pt.isec.ams.quizec.ui.screens

import androidx.compose.runtime.Composable
import pt.isec.ams.quizec.viewmodel.QuestionHistoryViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.isec.ams.quizec.data.models.QuestionType


@Composable
fun EditQuestionScreen(
    questionId: String,
    navController: NavController,
    viewModel: QuestionHistoryViewModel = viewModel()
) {
    // Obtener las preguntas desde el ViewModel
    val questions by viewModel.questions.collectAsState()

    // Si no hay preguntas cargadas (la lista está vacía), mostramos un mensaje de error
    if (questions.isEmpty()) {
        Text("No question found.")
    } else {
        val question = questions.first() // Tomamos la primera pregunta de la lista
        when (question.type) {
            QuestionType.P01 -> EditP01Question(
                questionId = question.id,
                initialTitle = question.title,
                initialAnswer = question.correctAnswers.firstOrNull(),
                initialImageUrl = question.imageUrl,
                onSaveClick = {
                    navController.popBackStack()
                }
            )
            QuestionType.P02 -> EditP02Question(
                questionId = question.id,
                initialTitle = question.title,
                initialOptions = question.options,
                initialCorrectAnswer = question.correctAnswers.firstOrNull(),
                initialImageUrl = question.imageUrl,
                onSaveClick = {
                    navController.popBackStack() // Navegar hacia atrás
                }
            )
            QuestionType.P03 -> EditP03Question(
                questionId = question.id,
                initialTitle = question.title,
                initialOptions = question.options,
                initialSelectedAnswers = question.correctAnswers,
                initialImageUrl = question.imageUrl,
                onSaveClick = {
                    navController.popBackStack() // Navegar hacia atrás
                }
            )
            QuestionType.P04 -> EditP04Question(
                questionId = question.id,
                initialTitle = question.title,
                initialOptions = question.options,
                initialImageUrl = question.imageUrl,
                onSaveClick = {
                    navController.popBackStack() // Navegar hacia atrás
                }
            )
            QuestionType.P05 -> EditP05Question(
                questionId = question.id,
                initialTitle = question.title,
                initialItems = question.options,
                initialImageUrl = question.imageUrl,
                onSaveClick = {
                    navController.popBackStack() // Navegar hacia atrás
                }
            )
            QuestionType.P06 -> EditP06Question(
                questionId = question.id,
                initialTitle = question.title,
                initialOptions = question.options,
                initialImageUrl = question.imageUrl,
                initialCorrectAnswers = question.correctAnswers,
                onSaveClick = {
                    navController.popBackStack() // Navegar hacia atrás
                }
            )
            QuestionType.P07 -> EditP07Question(
                questionId = question.id,
                initialTitle = question.title,
                initialOptions = question.options,
                initialImageUrl = question.imageUrl,
                initialCorrectAnswers = question.correctAnswers,
                onSaveClick = {
                    navController.popBackStack() // Navegar hacia atrás
                }
            )
            QuestionType.P08 -> EditP08Question(
                questionId = question.id,
                initialTitle = question.title,
                initialImageUrl = question.imageUrl,
                initialAnswers = question.correctAnswers,
                onSaveClick = {
                    navController.popBackStack() // Navegar hacia atrás
                }
            )
        }
    }
    // Llamar a `getQuestionById` cuando el `Composable` se cargue (lanzar coroutine)
    LaunchedEffect(questionId) {
        viewModel.getQuestionById(questionId)
    }
}








