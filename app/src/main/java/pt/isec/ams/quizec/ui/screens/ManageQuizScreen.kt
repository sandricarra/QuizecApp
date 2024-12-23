import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.ams.quizec.viewmodel.ManageQuizViewModel

@Composable
fun ManageQuizScreen(navController: NavController, creatorId: String, viewModel: ManageQuizViewModel) {
    val message by viewModel.message.collectAsState()
    val newStatus by viewModel.newStatus.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()

    // Cargar los cuestionarios al entrar en la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadQuizzesByCreatorId(creatorId) // Usa el creatorId pasado como parámetro
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar mensaje de retroalimentación
        if (message != null) {
            Text(text = message!!)
        }

        // Mostrar el nuevo estado de los cuestionarios
        if (newStatus != null) {
            Text(text = "New Quiz Status: ${newStatus!!.name}")
        }

        // Mostrar la lista de cuestionarios
        LazyColumn {
            items(quizzes) { quiz ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = quiz.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Status: ${quiz.status.name}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Geolocation Restricted: ${quiz.isGeolocationRestricted}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Show Results Immediately: ${quiz.showResultsImmediately}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Time Limit: ${quiz.timeLimit ?: "No limit"}", style = MaterialTheme.typography.bodyMedium)

                        // Mostrar usuarios en la waiting screen
                        if (quiz.participants.isNotEmpty()) {
                            Text(
                                text = "Waiting Users:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            quiz.participants.forEach { userId ->
                                Text(text = "- $userId", style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            Text(
                                text = "No users are waiting.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleQuizStatus(quiz.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Toggle Quiz Status")
                        }
                    }
                }
            }
        }

    }
}
