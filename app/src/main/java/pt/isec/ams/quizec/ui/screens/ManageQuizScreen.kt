import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.ams.quizec.viewmodel.ManageQuizViewModel

@Composable
fun ManageQuizScreen(navController: NavController, creatorId: String, viewModel: ManageQuizViewModel) {
    val message by viewModel.message.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadQuizzesByCreatorId(creatorId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mensaje de retroalimentación
        message?.let {
            item {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Si no hay cuestionarios, mostrar mensaje
        if (quizzes.isEmpty()) {
            item {
                Text(
                    text = AnnotatedString("You don't have any quizzes created."),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Lista de cuestionarios
            items(quizzes) { quiz ->
                val waitingParticipants by viewModel.getParticipantsForQuiz(quiz.id).collectAsState()
                val showResultsImmediately by viewModel.getShowResultsImmediately(quiz.id).collectAsState()
                val geolocationRestricted by viewModel.getGeolocationRestricted(quiz.id).collectAsState()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Título del cuestionario
                        Text(
                            text = quiz.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Estado del cuestionario
                        Text(
                            text = "Status: ${quiz.status.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Restricción geolocalizada
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Geolocation Restricted: ",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (geolocationRestricted) "Yes" else "No",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (geolocationRestricted) Color.Green else Color.Red
                            )
                        }

                        // Mostrar resultados inmediatamente
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Show Results Immediately: ",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (showResultsImmediately) "Yes" else "No",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (showResultsImmediately) Color.Green else Color.Red
                            )
                        }

                        // Mostrar tiempo límite
                        Text(
                            text = "Time Limit: ${quiz.timeLimit ?: "No limit"}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Botones de acción
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.toggleQuizStatus(quiz.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Toggle Quiz Status")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.toggleGeolocationRestriction(quiz.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Toggle Geolocation Restriction")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.toggleShowResultsImmediately(quiz.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Text("Toggle Show Results Immediately")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.forceFinishQuiz(quiz.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Force Finish Quiz")
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Participantes esperando
                        Text(
                            text = "Waiting Participants:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Lista de participantes
                        if (waitingParticipants.isNotEmpty()) {
                            waitingParticipants.forEach { user ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = "User: ${user.name}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "No participants yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
