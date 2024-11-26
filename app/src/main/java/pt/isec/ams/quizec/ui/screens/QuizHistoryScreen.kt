package pt.isec.ams.quizec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.viewmodel.QuizHistoryViewModel

@Composable
fun QuizHistoryScreen(navController: NavController, viewModel: QuizHistoryViewModel = viewModel()) {

    // Observar la lista de cuestionarios desde el ViewModel
    val quizzes = viewModel.quizzes.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Text(text = "My Quiz History", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(quizzes.value.size) { index ->
                val quiz = quizzes.value[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = {
                        coroutineScope.launch {
                            navController.navigate("quizScreen/${quiz.id}")
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = quiz.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Creado el: ${viewModel.formatDate(quiz.createdAt)}", // Formatear la fecha
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
