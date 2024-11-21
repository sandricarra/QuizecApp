package pt.isec.ams.quizec.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.viewmodel.QuizCreationState
import pt.isec.ams.quizec.viewmodel.QuizCreationViewModel

@Composable
fun QuizCreationScreen(navController: NavController, viewModel: QuizCreationViewModel = viewModel()) {
    val quizTitle by viewModel.quizTitle.collectAsState()
    val quizDescription by viewModel.quizDescription.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()
    val state by viewModel.state.collectAsState()

    var title by remember { mutableStateOf(quizTitle) }
    var description by remember { mutableStateOf(quizDescription) }
    var image by remember { mutableStateOf(imageUrl) }

    // Lista de preguntas agregadas al cuestionario
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Título del cuestionario
        TextField(
            value = title,
            onValueChange = {
                title = it
                viewModel.setQuizTitle(it) // Actualiza el estado del título en el ViewModel
            },
            label = { Text("Título del Cuestionario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción del cuestionario
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                viewModel.setQuizDescription(it) // Actualiza el estado de la descripción en el ViewModel
            },
            label = { Text("Descripción del Cuestionario") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen placeholder
        Image(
            painter = painterResource(id = R.drawable.placeholder_image),
            contentDescription = "Imagen del Cuestionario",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Agregar una pregunta
        Button(
            onClick = {
                // Aquí se abre una pantalla para seleccionar el tipo de pregunta
                // y agregarla a la lista de preguntas
                questions = questions + Question(
                    questionText = "Pregunta de ejemplo",
                    questionType = QuestionType.MULTIPLE_CHOICE,
                    options = listOf("Opción 1", "Opción 2", "Opción 3"),
                    correctAnswers = listOf(0) // Supongamos que la opción 1 es correcta
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Pregunta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar las preguntas agregadas
        questions.forEachIndexed { index, question ->
            Text("Pregunta ${index + 1}: ${question.questionText}")
            // Aquí puedes agregar un componente para mostrar el tipo de pregunta y opciones si es necesario
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar el cuestionario
        Button(
            onClick = {
                // Guardar el cuestionario con las preguntas
                val quiz = viewModel.createQuiz()
                viewModel.saveQuiz(quiz, questions)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Cuestionario")
        }
        Button(
            onClick = {
                // Volver a la pantalla anterior (HomeScreen)
                navController.popBackStack() // Esto vuelve a la pantalla anterior
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a Inicio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Manejo de estado (cargando, éxito, error)
        when (state) {
            is QuizCreationState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is QuizCreationState.Success -> {
                Text("Cuestionario creado con éxito", color = Color.Green)
                // Navegar a otra pantalla si es necesario
            }
            is QuizCreationState.Error -> {
                Text("Error: ${(state as QuizCreationState.Error).message}", color = Color.Red)
            }
            else -> {}
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuizCreationScreenPreview() {
    QuizCreationScreen(navController = rememberNavController())
}
