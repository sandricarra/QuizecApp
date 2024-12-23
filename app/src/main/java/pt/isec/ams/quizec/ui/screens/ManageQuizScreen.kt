

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
    import androidx.compose.ui.text.AnnotatedString
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import kotlinx.coroutines.delay
    import pt.isec.ams.quizec.viewmodel.ManageQuizViewModel

    @Composable
    fun ManageQuizScreen(navController: NavController, creatorId: String, viewModel: ManageQuizViewModel) {
        val message by viewModel.message.collectAsState()
        val newStatus by viewModel.newStatus.collectAsState()
        val quizzes by viewModel.quizzes.collectAsState()

        LaunchedEffect(Unit) {
            // Cargar cuestionarios creados por el creador
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
                    Text(text = it)
                }
            }

            // Nuevo estado
            newStatus?.let {
                item {
                    Text(text = "New Quiz Status: ${it.name}")
                }
            }
            if (quizzes.isEmpty()) {
                item {
                    Text(
                        text = AnnotatedString("You don't have any quizzes created."),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center)
            }
        } else {

            // Lista de cuestionarios
            items(quizzes) { quiz ->
                // Obtener los participantes para este quiz de manera independiente
                val waitingParticipants by viewModel.getParticipantsForQuiz(quiz.id).collectAsState()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = quiz.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Status: ${quiz.status.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Geolocation Restricted: ${quiz.isGeolocationRestricted}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Show Results Immediately: ${quiz.showResultsImmediately}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Time Limit: ${quiz.timeLimit ?: "No limit"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Button(
                            onClick = { viewModel.toggleQuizStatus(quiz.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Toggle Quiz Status")
                        }

                        // Título de los participantes
                        Text(
                            text = "Waiting Participants:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Lista de participantes
                        if (waitingParticipants.isNotEmpty()) {
                            waitingParticipants.forEach { user ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
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
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        }
    }
