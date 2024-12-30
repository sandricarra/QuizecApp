import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.ui.theme.BackgroundImage
import pt.isec.ams.quizec.ui.viewmodel.ManageQuizViewModel

@Composable
fun ManageQuizScreen(navController: NavController, creatorId: String, viewModel: ManageQuizViewModel) {
    val message by viewModel.message.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()

    // Cargar los cuestionarios al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadQuizzesByCreatorId(creatorId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        BackgroundImage()

        // Contenido
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
                        text = AnnotatedString(stringResource(R.string.no_quizzes_message)),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Lista de cuestionarios
                items(quizzes) { quiz ->
                    val waitingParticipants by viewModel.getParticipantsForQuiz(quiz.id).collectAsState()
                    val geolocationRestricted by viewModel.getGeolocationRestricted(quiz.id).collectAsState()
                    val showResultsImmediately by viewModel.getShowResultsImmediately(quiz.id).collectAsState()
                    val playingUsers by viewModel.getPlayingUsersForQuiz(quiz.id).collectAsState()
                    val quizStatus by viewModel.getQuizStatus(quiz.id).collectAsState()
                    val anonymousResults by viewModel.getAnonymousResultsForQuiz(quiz.id).collectAsState()

                    LaunchedEffect(geolocationRestricted, showResultsImmediately) {
                        viewModel.getGeolocationRestricted(quiz.id)
                        viewModel.getShowResultsImmediately(quiz.id)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFBFDEFF).copy(alpha = 0.9f)), // Ajusta la opacidad de la tarjeta
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
                                text = stringResource(R.string.quiz_status) + "${quizStatus?.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Restricción geolocalizada
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.geolocation_restricted),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (geolocationRestricted) stringResource(R.string.yes) else stringResource(R.string.no),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (geolocationRestricted) Color.Green else Color.Red
                                )
                            }

                            // Mostrar resultados inmediatamente
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.show_results_immediately),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (showResultsImmediately) stringResource(R.string.yes) else stringResource(R.string.no),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (showResultsImmediately) Color.Green else Color.Red
                                )
                            }

                            // Mostrar tiempo límite
                            Text(
                                text = stringResource(R.string.time_limit) + "${quiz.timeLimit ?: stringResource(R.string.no_time_limit)}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Botones de acción
                            Spacer(modifier = Modifier.height(12.dp))

                            // Botón para alternar el estado del cuestionario
                            Button(
                                onClick = { viewModel.toggleQuizStatus(quiz.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(stringResource(R.string.toggle_quiz_status))
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.toggleGeolocationRestriction(quiz.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(stringResource(R.string.toggle_geolocation_restriction))
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.toggleShowResultsImmediately(quiz.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(stringResource(R.string.toggle_show_results_immediately))
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.forceFinishQuiz(quiz.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text(stringResource(R.string.force_finish_quiz))
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            // Participantes esperando
                            Text(
                                text = stringResource(R.string.waiting_participants),
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
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFBFDEFF).copy(alpha = 0.9f))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(text = stringResource(R.string.user) + " ${user.name}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.no_participants_yet),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            // Usuarios jugando
                            Text(
                                text = stringResource(R.string.playing_users),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Lista de usuarios jugando
                            if (playingUsers.isNotEmpty()) {
                                playingUsers.forEach { user ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFBFDEFF).copy(alpha = 0.9f))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(text = stringResource(R.string.user) + " ${user.name}", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.no_users_playing_yet),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            // Resultados anónimos
                            Text(
                                text = stringResource(R.string.anonymous_results),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Lista de resultados anónimos
                            if (anonymousResults.isNotEmpty()) {
                                anonymousResults.forEach { result ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFBFDEFF).copy(alpha = 0.9f))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(text = stringResource(R.string.result) + " $result", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.no_results_yet),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.clearResultsForQuiz(quiz.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text(stringResource(R.string.clear_results))
                            }
                        }
                    }
                }
            }
        }
    }
}







