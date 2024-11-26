package pt.isec.ams.quizec.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.data.models.QuestionType


import pt.isec.ams.quizec.viewmodel.QuizCreationViewModel

@Composable
fun QuizCreationScreen(
    creatorId: String, // ID del creador del cuestionario
    onQuizSaved: () -> Unit, // Acción que se ejecuta cuando el cuestionario se guarda exitosamente
    viewModel: QuizCreationViewModel = viewModel() // Usamos el ViewModel para gestionar la lógica de creación
) {
    // Estado mutable para cada campo de entrada y configuraciones del cuestionario
    var title by remember { mutableStateOf(TextFieldValue("")) } // Título del cuestionario
    var description by remember { mutableStateOf(TextFieldValue("")) } // Descripción del cuestionario
    var isLoading by remember { mutableStateOf(false) } // Estado de carga mientras se guarda el cuestionario
    var imageUrl by remember { mutableStateOf<String?>(null) } // URL de la imagen seleccionada
    var imageUri by remember { mutableStateOf<Uri?>(null) } // URI de la imagen seleccionada
    var selectedAnswer by remember { mutableStateOf<String?>(null) } // Respuesta seleccionada
    var timeLimit by remember { mutableStateOf(0L) } // Límite de tiempo del cuestionario
    var isGeolocationRestricted by remember { mutableStateOf(false) } // Restricción por geolocalización
    var isAccessControlled by remember { mutableStateOf(false) } // Control de acceso (cuestionario empieza cuando el creador lo desea)
    var showResultsImmediately by remember { mutableStateOf(false) } // Mostrar resultados inmediatamente después de terminar

    // Variables relacionadas con el tipo de pregunta
    var questionType by remember { mutableStateOf<QuestionType?>(null) }
    var questionTitle by remember { mutableStateOf("") }
    var questionOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var questionCorrectAnswers by remember { mutableStateOf<List<String>>(emptyList()) }

    // Opciones predeterminadas para preguntas de tipo booleano
    var options by remember { mutableStateOf(listOf("True", "False")) }
    var correctAnswers by remember { mutableStateOf(listOf("True")) }

    // Estado para gestionar la visibilidad del menú desplegable
    var isDropdownOpen by remember { mutableStateOf(false) }

    // Lista de preguntas añadidas
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }

    // Función para limpiar los campos después de añadir una pregunta
    val onUpdate: () -> Unit = {
        questionTitle = ""
        selectedAnswer = null
    }

    // Launcher para seleccionar una imagen desde el dispositivo
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    // LazyColumn para organizar la UI del cuestionario
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mostrar la imagen seleccionada (si existe)
        item {
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Mostrar un ícono predeterminado si no se ha seleccionado imagen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle, // Ícono predeterminado
                        contentDescription = "No image selected",
                        tint = Color.Gray,
                        modifier = Modifier.size(100.dp) // Tamaño del ícono
                    )
                    Text(
                        text = "No image selected",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Título principal del cuestionario
        item {
            Text("Create Quiz", style = MaterialTheme.typography.headlineMedium)
        }

        // Campo para el título del cuestionario
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Quiz Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Campo para la descripción del cuestionario
        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )
        }

        // Campo para el límite de tiempo del cuestionario
        item {
            OutlinedTextField(
                value = timeLimit.toString(),
                onValueChange = {
                    // Validar que el valor ingresado sea un número
                    timeLimit =
                        (it.toIntOrNull()
                            ?: timeLimit).toLong() // Si no es un número, mantener el valor anterior
                },
                label = { Text("Time Limit (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                maxLines = 1
            )
        }

        // Configuración para restringir el cuestionario por geolocalización
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Restrict by Geolocation",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isGeolocationRestricted,
                    onCheckedChange = { isGeolocationRestricted = it }
                )
            }
        }

        // Configuración para control de acceso (cuando empieza el cuestionario)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Access Controlled (Quiz start when the creator wants)",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isAccessControlled,
                    onCheckedChange = { isAccessControlled = it }
                )
            }
        }

        // Configuración para mostrar resultados inmediatamente
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show Results Immediately (Show results after finishing)",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = showResultsImmediately,
                    onCheckedChange = { showResultsImmediately = it }
                )
            }
        }

        // Botón para seleccionar una imagen
        item {
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image")
            }
        }

        // Botón y menú desplegable para seleccionar el tipo de pregunta
        item {
            var selectedQuestionTypeText by remember { mutableStateOf("Select Question Type") }

            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { isDropdownOpen = !isDropdownOpen },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedQuestionTypeText)
                }

                DropdownMenu(
                    expanded = isDropdownOpen,
                    onDismissRequest = { isDropdownOpen = false },
                    modifier = Modifier
                        .fillMaxWidth() // Asegura que el menú ocupe todo el ancho
                ) {
                    // Itera sobre los tipos de preguntas y permite seleccionar uno
                    QuestionType.values().forEach { type ->
                        DropdownMenuItem(
                            onClick = {
                                questionType = type
                                selectedQuestionTypeText = type.name // Actualiza el texto del botón
                                isDropdownOpen = false // Cierra el menú
                            },
                            text = {
                                Text(
                                    text = type.name,
                                    modifier = Modifier.fillMaxWidth(), // Asegura que el texto ocupe todo el ancho
                                    textAlign = TextAlign.Center // Centra el texto horizontalmente
                                )
                            }
                        )
                    }
                }
            }
        }

        // Mostrar UI de la pregunta basada en el tipo seleccionado
        item {
            when (questionType) {
                QuestionType.P01 -> {
                    // Mostrar la UI para el tipo de pregunta P01
                    P01Question(
                        questionTitle = questionTitle,
                        onTitleChange = { questionTitle = it },
                        selectedAnswer = selectedAnswer,
                        onAnswerChange = { selectedAnswer = it },
                        imageUrl = imageUrl?.toString(),
                        onImageChange = { imageUrl = it }
                    )
                }

                else -> {}
            }
        }

        // Botón para añadir la pregunta al cuestionario
        item {
            AddQuestionButton(
                questionType = questionType,
                questionTitle = questionTitle,
                options = options,
                correctAnswers = correctAnswers,
                imageUrl = imageUrl,
                onUpdate = onUpdate,
                viewModel = viewModel
            )
        }

        // Botón para guardar el cuestionario
        item {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.saveQuiz(
                        title = title.text,
                        description = description.text,
                        questions = viewModel.questions.map { it.id },
                        imageUrl = imageUri?.toString(),
                        timeLimit = timeLimit.toInt(),
                        creatorId = creatorId,
                        isGeolocationRestricted = isGeolocationRestricted,
                        isAccessControlled = isAccessControlled,
                        showResultsImmediately = showResultsImmediately,

                        onSuccess = {
                            isLoading = false
                            onQuizSaved()
                        },
                        onError = {
                            isLoading = false
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
        }


        // Agregar preguntas añadidas como otro ítem en el LazyColumn
        item {
            if (viewModel.questions.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Added Questions",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        viewModel.questions.forEach { question ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                        MaterialTheme.shapes.small
                                    ) // Agrega borde sutil
                                    .padding(16.dp) // Padding interno para separación
                            ) {
                                // Título de la pregunta
                                Text(
                                    text = "Title: ${question.title}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                // Tipo de pregunta
                                Text(
                                    text = "Type: ${question.type}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                // Respuestas correctas
                                Text(
                                    text = "Correct Answers: ${question.correctAnswers}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No questions added yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }

        }
    }
}
@Composable
fun P01Question(
    questionTitle: String, // Estado compartido para el título de la pregunta
    onTitleChange: (String) -> Unit, // Callback para actualizar el título
    selectedAnswer: String?, // Respuesta seleccionada
    onAnswerChange: (String) -> Unit, // Callback para actualizar la respuesta
    imageUrl: String?, // URL de la imagen
    onImageChange: (String) -> Unit // Callback para actualizar la URL de la imagen
)





{
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                onImageChange(it.toString()) // Actualizamos la URL de la imagen usando el callback
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange, // Actualizar el título usando el estado compartido
            label = { Text("Enter Question Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) // Mostrar la imagen seleccionada
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "No image selected",
                    tint = Color.Gray,
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = "No image selected",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Image")
        }

        // Opciones de respuesta: True/False
        Text("True or False Question", modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "True",
                onClick = { onAnswerChange("True") }
            )
            Text("True", modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "False",
                onClick = { onAnswerChange("False") }
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
    imageUrl: String?,
    onUpdate: () -> Unit, // Callback opcional para actualizar la UI después de agregar la pregunta
    viewModel: QuizCreationViewModel
) {
    Button(
        onClick = {
            // Asegúrate de que hay un tipo de pregunta y que los campos necesarios están completos
            questionType?.let {
                viewModel.addQuestion(
                    type = it,
                    title = questionTitle,  // El título de la pregunta
                    options = options,      // Las opciones de respuesta
                    correctAnswers = correctAnswers, // Las respuestas correctas
                    imageUrl = imageUrl // La URL de la imagen
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



