package pt.isec.ams.quizec.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.QuestionType

import pt.isec.ams.quizec.viewmodel.QuizCreationViewModel

@Composable
fun QuizCreationScreen(
    creatorId: String, // ID del creador
    onQuizSaved: () -> Unit, // Acción al guardar exitosamente
    viewModel: QuizCreationViewModel = viewModel() // Usamos el ViewModel
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    var questionType by remember { mutableStateOf<QuestionType?>(null) }
    var questionTitle by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("True", "False")) }
    var correctAnswers by remember { mutableStateOf(listOf("True")) }

    var isDropdownOpen by remember { mutableStateOf(false) }

    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    val updateQuestions = { updatedQuestion: Question ->
        questions = questions + updatedQuestion
    }

    // Launcher para seleccionar una imagen del dispositivo
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )


    // Composable principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Create Quiz", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para el título
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Quiz Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para la descripción
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { isDropdownOpen = !isDropdownOpen },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Question Type")
        }
        // Selección del tipo de pregunta
        DropdownMenu(
            expanded = isDropdownOpen, // Usamos el estado isDropdownOpen para controlar la visibilidad
            onDismissRequest = { isDropdownOpen = false } // Cerramos el menú al hacer clic fuera
        ) {
            QuestionType.values().forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        questionType = type // Guardamos el tipo de pregunta seleccionado
                        isDropdownOpen = false // Cerramos el menú
                    }
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la UI correspondiente al tipo de pregunta
        when (questionType) {
            QuestionType.P01 -> {
                P01Question(
                    onUpdate = {
                        // Actualizar o limpiar el formulario después de agregar la pregunta
                        questionTitle = "" // Resetear el título
                        options = listOf("True", "False") // Resetear opciones
                        correctAnswers = listOf("True") // Resetear respuestas correctas
                    }
                )
            }
            // Otros tipos de preguntas (P02, P03, etc.)
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para añadir la pregunta
        AddQuestionButton(
            questionType = questionType,
            questionTitle = questionTitle,
            options = options,
            correctAnswers = correctAnswers,
            onUpdate = {
                // Callback para actualizar la UI
                questionTitle = ""  // Limpiar el título después de agregar
                options = listOf("True", "False") // Limpiar las opciones
                correctAnswers = listOf("True") // Limpiar las respuestas correctas
            },
            viewModel = viewModel
        )

        Button(
            onClick = {
                isLoading = true
                viewModel.saveQuiz(
                    title = title.text,
                    description = description.text,
                    questions = viewModel.questions.map { it.id }, // Solo los IDs de las preguntas
                    imageUrl = imageUri?.toString(), // Convertimos el URI a String
                    isGeolocationRestricted = false,
                    startTime = System.currentTimeMillis(),
                    endTime = System.currentTimeMillis() + 3600000, // Por defecto, dura 1 hora
                    resultVisibility = true,
                    creatorId = creatorId,
                    onSuccess = {
                        isLoading = false
                        onQuizSaved()
                    },
                    onError = {
                        isLoading = false
                        // Mostrar un error en la UI
                    }
                )
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Save Quiz")
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Lista de preguntas añadidas
        LazyColumn {
            items(viewModel.questions) { question ->
                Text(text = "Question ID: ${question.id}, Type: ${question.type}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen seleccionada
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "No image selected",
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Botón para seleccionar una imagen
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Image")
        }

    }
}


@Composable
fun P01Question(
    onUpdate: (Question) -> Unit // Callback para actualizar la pregunta
) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var questionTitle by remember { mutableStateOf("") } // Para guardar el título de la pregunta

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it }, // Actualizar el título
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // Opciones de respuesta: True/False
        Text("True or False Question", modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "True",
                onClick = {
                    selectedAnswer = "True"
                    // Actualizar la pregunta con la respuesta correcta
                    onUpdate(Question(
                        id = "q1", // Aquí debería pasarse un ID único real
                        title = questionTitle, // Usamos el título ingresado por el usuario
                        type = QuestionType.P01,
                        options = listOf("True", "False"),
                        correctAnswers = listOf("True") // Respuesta correcta
                    ))
                }
            )
            Text("True", modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "False",
                onClick = {
                    selectedAnswer = "False"
                    // Actualizar la pregunta con la respuesta correcta
                    onUpdate(Question(
                        id = "q1", // Aquí debería pasarse un ID único real
                        title = questionTitle, // Usamos el título ingresado por el usuario
                        type = QuestionType.P01,
                        options = listOf("True", "False"),
                        correctAnswers = listOf("False") // Respuesta correcta
                    ))
                }
            )
            Text("False", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
@Composable
fun AddQuestionButton(
    questionType: QuestionType?,
    questionTitle: String,
    options: List<String>,
    correctAnswers: List<String>,
    onUpdate: () -> Unit, // Callback opcional para actualizar la UI después de agregar la pregunta
    viewModel: QuizCreationViewModel
) {
    Button(
        onClick = {
            // Asegúrate de que hay un tipo de pregunta y que los campos necesarios están completos
            questionType?.let {
                // Llamamos al método addQuestion del ViewModel con los parámetros necesarios
                viewModel.addQuestion(
                    type = it,
                    title = questionTitle,  // El título de la pregunta
                    options = options,      // Las opciones de respuesta
                    correctAnswers = correctAnswers // Las respuestas correctas
                )
            }

            // Limpiar o actualizar otros campos de la UI si es necesario
            onUpdate()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Add Question")
    }
}







