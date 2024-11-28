package pt.isec.ams.quizec.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter

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

    var timeLimit by remember { mutableLongStateOf(0L) } // Límite de tiempo del cuestionario
    var isGeolocationRestricted by remember { mutableStateOf(false) } // Restricción por geolocalización
    var isAccessControlled by remember { mutableStateOf(false) } // Control de acceso (cuestionario empieza cuando el creador lo desea)
    var showResultsImmediately by remember { mutableStateOf(false) } // Mostrar resultados inmediatamente después de terminar


    // Variables relacionadas con el tipo de pregunta
    var questionType by remember { mutableStateOf<QuestionType?>(null) }
    var questionTitle by remember { mutableStateOf("") }
    val optionsP01 by remember { mutableStateOf(listOf("True", "False")) }
    var optionsP02 by remember { mutableStateOf(listOf<String>()) }
    var optionsP03 by remember { mutableStateOf(listOf<String>()) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var selectedAnswerP02 by remember { mutableStateOf<String?>(null) }
    var selectedAnswerP03 by remember { mutableStateOf<List<String>>(emptyList()) }
    var pairsP04 by remember { mutableStateOf(listOf("" to "")) }

    var itemsP05 by remember { mutableStateOf(listOf("Item 1", "Item 2")) }

    // Texto base con espacios en blanco representados por un marcador (por ejemplo, "[...]")
    var baseTextP06 by remember { mutableStateOf("Complete the sentence: The [...] is shining.") }

    // Lista de opciones posibles para completar los espacios en blanco
    var optionsP06 by remember { mutableStateOf(listOf("sun", "moon", "star")) }

    // Respuestas correctas asociadas a los espacios en blanco
    var correctAnswersP06 by remember { mutableStateOf(listOf("sun")) }

    var associationsP07 = remember { mutableStateListOf<Pair<String, String>>() }
    val imageUrlP07 by remember { mutableStateOf<String?>(null) }








    // Estado para gestionar la visibilidad del menú desplegable
    var isDropdownOpen by remember { mutableStateOf(false) }



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
                        imageUrl = imageUrl,
                        onImageChange = { imageUrl = it }
                    )
                }

                QuestionType.P02 -> {
                    P02Question(
                        imageUrl = imageUrl,
                        onImageChange = { imageUrl = it },
                        questionTitle = questionTitle,
                        onTitleChange = { questionTitle = it },
                        options = optionsP02,
                        onOptionsChange = { optionsP02 = it },
                        onSelectedOptionChange = { selectedAnswerP02 = it },
                        selectedOption = selectedAnswerP02

                    )

                }
                QuestionType.P03 -> {
                    P03Question(
                        imageUrl = imageUrl,
                        onImageChange = { imageUrl = it },
                        questionTitle = questionTitle,
                        onTitleChange = { questionTitle = it },
                        options = optionsP03,
                        onOptionsChange = { optionsP03 = it },
                        onSelectedOptionsChange = { selectedAnswerP03 = it },
                        selectedOptions = selectedAnswerP03


                    )

                }

                QuestionType.P04 -> {

                    P04Question(
                        questionTitle = questionTitle,
                        onTitleChange = { questionTitle = it },
                        pairs = pairsP04,
                        onPairsChange = { pairsP04 = it },
                        imageUrl = imageUrl,
                        onImageChange = { imageUrl = it }
                    )
                }

                QuestionType.P05 -> {
                P05Question(
                    questionTitle = questionTitle,
                    onTitleChange = { questionTitle = it },
                    items = itemsP05,
                    onItemsChange = { itemsP05 = it },
                    imageUrl = imageUrl,
                    onImageChange = { imageUrl = it }
                )}



                QuestionType.P06 -> {
                    P06Question(
                        baseTextP06 = baseTextP06,
                        onBaseTextChange = { baseTextP06 = it },
                        options = optionsP06,
                        onOptionsChange = { optionsP06 = it },
                        correctAnswers = correctAnswersP06,
                        onCorrectAnswersChange = { correctAnswersP06 = it },
                        imageUrl = imageUrl,
                        onImageChange = { imageUrl = it },
                        onTitleChange = { questionTitle = it },
                        questionTitle = questionTitle

                    )
                }
                QuestionType.P07 -> {
                    // Mostrar la UI para el tipo de pregunta P07
                    P07Question(
                        questionTitle = questionTitle,
                        onTitleChange = { questionTitle = it },
                        associations = associationsP07,
                        onAssociationsChange = { associationsP07 = it },
                        imageUrl = imageUrl?.toString(),
                        onImageChange = { imageUrl = it }
                    )
                }
                else -> {}
            }
        }

        // Botón para añadir la pregunta al cuestionario
        item {
            when (questionType) {
                QuestionType.P01 -> {
                    // Cuando el questionType es P01, pasamos las opciones y otros parámetros específicos de este tipo
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle,
                        options = optionsP01,
                        correctAnswers = listOf(selectedAnswer ?: ""),
                        imageUrl = imageUrl,
                        onUpdate = onUpdate,
                        viewModel = viewModel
                    )
                }
                QuestionType.P02 -> {
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle,
                        options =optionsP02,
                        correctAnswers = listOf(selectedAnswerP02 ?: "") ,
                        imageUrl = imageUrl,
                        onUpdate = onUpdate,
                        viewModel = viewModel
                    )
                }
                QuestionType.P03 -> {
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle,
                        options =optionsP03,
                        correctAnswers = selectedAnswerP03 ,
                        imageUrl = imageUrl,
                        onUpdate = onUpdate,
                        viewModel = viewModel
                    )
                }
                QuestionType.P04 -> {
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle,
                        options = pairsP04.map { "${it.first} -> ${it.second}" }, // Combina pares como "A -> B"
                        correctAnswers = pairsP04.map { "${it.first} -> ${it.second}" }, // Los pares se consideran las respuestas correctas
                        imageUrl = imageUrl,
                        onUpdate = onUpdate,
                        viewModel = viewModel
                    )
                }
                QuestionType.P05 -> {
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle,
                        options = itemsP05, // Los elementos a ordenar
                        correctAnswers = itemsP05, // El orden correcto se define al crearlos
                        imageUrl = imageUrl,
                        onUpdate = onUpdate,
                        viewModel = viewModel

                    )
                }
                QuestionType.P06 -> {
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle, // Usamos el texto base como título de la pregunta
                        options = optionsP06,
                        correctAnswers = correctAnswersP06,
                        imageUrl = null, // Las preguntas de completar no requieren imagen (puedes cambiar esto)
                        onUpdate = onUpdate,
                        viewModel = viewModel
                    )
                }
                QuestionType.P07 -> {
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle,
                        options = associationsP07.map { it.first }, // Lista de conceptos
                        correctAnswers = associationsP07.map { it.second }, // Lista de asociaciones
                        imageUrl = imageUrlP07,
                        onUpdate = onUpdate,
                        viewModel = viewModel
                    )
                }












                // Agregar más tipos de preguntas si es necesario
                else -> {

                }
            }
        }

        // Botón para guardar el cuestionario
        item {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.saveQuiz(
                        title = title.text,
                        description = description.text,
                        // questions = viewModel.questions.map { it.id },
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
) {

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { onImageChange(it.toString()) } }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange, // Actualizar el título usando el estado compartido
            label = { Text("Enter Question Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Mostrar la imagen seleccionada
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

        // Manejo de selección de respuestas
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "True",
                onClick = { onAnswerChange("True") } // Cambia el valor seleccionado
            )
            Text("True", modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "False",
                onClick = { onAnswerChange("False") } // Cambia el valor seleccionado
            )
            Text("False", modifier = Modifier.padding(start = 8.dp))
        }
    }
}


@Composable
fun P02Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    options: List<String>, // Lista de opciones disponibles
    onOptionsChange: (List<String>) -> Unit, // Callback para actualizar las opciones
    selectedOption: String?, // Opción seleccionada como respuesta correcta
    onSelectedOptionChange: (String) -> Unit, // Callback para actualizar la opción correcta
    imageUrl: String?,
    onImageChange: (String) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { onImageChange(it.toString()) }
        }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange,
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Imagen asociada a la pregunta
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
            Text("No image selected", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Select Image")
        }

        // Opciones para la pregunta
        Text("Options:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onSelectedOptionChange(option) }
                )
                TextField(
                    value = option,
                    onValueChange = { newOption ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = newOption
                        onOptionsChange(updatedOptions)
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    label = { Text("Option ${index + 1}") }
                )
                IconButton(
                    onClick = {
                        val updatedOptions = options.toMutableList()
                        updatedOptions.removeAt(index)
                        onOptionsChange(updatedOptions)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove option")
                }
            }
        }

        // Botón para añadir una nueva opción
        Button(
            onClick = { onOptionsChange(options + "") }, // Añadir una opción vacía
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Option")
        }
    }
}

@Composable
fun P03Question(
    questionTitle: String, // Título de la pregunta
    onTitleChange: (String) -> Unit, // Callback para actualizar el título

    options: List<String>, // Opciones dinámicas para la pregunta
    onOptionsChange: (List<String>) -> Unit, // Callback para actualizar las opciones

    selectedOptions: List<String>, // Respuestas seleccionadas (varias respuestas correctas)
    onSelectedOptionsChange: (List<String>) -> Unit, // Callback para actualizar las respuestas seleccionadas

    imageUrl: String?, // URL de la imagen
    onImageChange: (String) -> Unit // Callback para actualizar la URL de la imagen
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { onImageChange(it.toString()) } }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange,
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Imagen asociada a la pregunta
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
            Text("No image selected", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Select Image")
        }

        // Opciones para la pregunta
        Text("Options:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = selectedOptions.contains(option),
                    onCheckedChange = { isChecked ->
                        val updatedOptions = if (isChecked) {
                            selectedOptions + option // Añadir la opción seleccionada
                        } else {
                            selectedOptions - option // Eliminar la opción seleccionada
                        }
                        onSelectedOptionsChange(updatedOptions)
                    }
                )
                TextField(
                    value = option,
                    onValueChange = { newOption ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = newOption
                        onOptionsChange(updatedOptions)
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    label = { Text("Option ${index + 1}") }
                )
                IconButton(
                    onClick = {
                        val updatedOptions = options.toMutableList()
                        updatedOptions.removeAt(index)
                        onOptionsChange(updatedOptions)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove option")
                }
            }
        }

        // Botón para añadir una nueva opción
        Button(
            onClick = { onOptionsChange(options + "") }, // Añadir una opción vacía
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Option")
        }
    }
}

@Composable
fun P04Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    pairs: List<Pair<String, String>>, // Lista de pares de opciones
    onPairsChange: (List<Pair<String, String>>) -> Unit, // Callback para actualizar los pares
    imageUrl: String?,
    onImageChange: (String) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { onImageChange(it.toString()) }
        }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange,
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Imagen asociada a la pregunta
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
            Text("No image selected", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Select Image")
        }

        // Pares de elementos para emparejar
        Text("Matching Pairs:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        pairs.forEachIndexed { index, (left, right) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                // Campo de texto para la columna izquierda
                TextField(
                    value = left,
                    onValueChange = { newLeft ->
                        val updatedPairs = pairs.toMutableList()
                        updatedPairs[index] = newLeft to right
                        onPairsChange(updatedPairs)
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Column A ${index + 1}") }
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Campo de texto para la columna derecha
                TextField(
                    value = right,
                    onValueChange = { newRight ->
                        val updatedPairs = pairs.toMutableList()
                        updatedPairs[index] = left to newRight
                        onPairsChange(updatedPairs)
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Column B ${index + 1}") }
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Botón para eliminar un par
                IconButton(onClick = {
                    val updatedPairs = pairs.toMutableList()
                    updatedPairs.removeAt(index)
                    onPairsChange(updatedPairs)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove pair")
                }
            }
        }

        // Botón para añadir un nuevo par
        Button(
            onClick = { onPairsChange(pairs + ("" to "")) }, // Añadir un par vacío
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Pair")
        }
    }
}


@Composable
fun P05Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    items: List<String>,
    onItemsChange: (List<String>) -> Unit,
    imageUrl: String?,
    onImageChange: (String) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { onImageChange(it.toString()) } }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange,
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Imagen asociada a la pregunta
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
            Text("No image selected", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Select Image")
        }

        // Lista de elementos a ordenar
        Text("Order the following items:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        items.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("${index + 1}.", modifier = Modifier.padding(end = 8.dp)) // Número del elemento
                TextField(
                    value = item,
                    onValueChange = { newItem ->
                        val updatedItems = items.toMutableList()
                        updatedItems[index] = newItem
                        onItemsChange(updatedItems)
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    label = { Text("Item ${index + 1}") }
                )
                IconButton(
                    onClick = {
                        val updatedItems = items.toMutableList()
                        updatedItems.removeAt(index)
                        onItemsChange(updatedItems)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove item")
                }
            }
        }

        // Botón para añadir un nuevo elemento
        Button(
            onClick = { onItemsChange(items + "") }, // Añade un elemento vacío
            modifier = Modifier.padding(vertical = 8.dp),
            enabled = items.size < 6 // Máximo 6 elementos
        ) {
            Text("Add Item")
        }
    }
}


@Composable
fun P06Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    baseTextP06: String,
    onBaseTextChange: (String) -> Unit,
    options: List<String>,
    onOptionsChange: (List<String>) -> Unit,
    correctAnswers: List<String>,
    onCorrectAnswersChange: (List<String>) -> Unit,
    imageUrl: String?,
    onImageChange: (String) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { onImageChange(it.toString()) } }
    )

    // Control del estado de expansión de los DropdownMenus
    val dropdownExpandedStates = remember { mutableStateListOf<Boolean>().apply {
        repeat(correctAnswers.size) { add(false) }
    }}

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange,
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Campo de texto para el texto base con espacios en blanco
        TextField(
            value = baseTextP06,
            onValueChange = onBaseTextChange,
            label = { Text("Enter Sentence with Blanks (use {} for blanks)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            placeholder = { Text("Example: The {} is {}.") }
        )

        // Imagen asociada a la pregunta
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
            Text("No image selected", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Select Image")
        }

        // Opciones para completar los espacios en blanco
        Text("Options:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                TextField(
                    value = option,
                    onValueChange = { newOption ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = newOption
                        onOptionsChange(updatedOptions)
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Option ${index + 1}") }
                )
                IconButton(onClick = {
                    val updatedOptions = options.toMutableList()
                    updatedOptions.removeAt(index)
                    onOptionsChange(updatedOptions)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove option")
                }
            }
        }

        // Botón para añadir nuevas opciones
        Button(
            onClick = { onOptionsChange(options + "") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Option")
        }

        // Selección de respuestas correctas para cada espacio en blanco
        Text("Correct Answers:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
        correctAnswers.forEachIndexed { index, answer ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text("Blank ${index + 1}:", modifier = Modifier.padding(end = 8.dp))
                Box {
                    Button(
                        onClick = { dropdownExpandedStates[index] = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(answer.ifEmpty { "Select Answer" })
                    }
                    DropdownMenu(
                        expanded = dropdownExpandedStates[index],
                        onDismissRequest = { dropdownExpandedStates[index] = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    val updatedAnswers = correctAnswers.toMutableList()
                                    updatedAnswers[index] = option
                                    onCorrectAnswersChange(updatedAnswers)
                                    dropdownExpandedStates[index] = false
                                },
                                text = { Text(option) }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun P07Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    associations: SnapshotStateList<Pair<String, String>>, // Lista reactiva de asociaciones
    onAssociationsChange: (SnapshotStateList<Pair<String, String>>) -> Unit,
    imageUrl: String?,
    onImageChange: (String) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { onImageChange(it.toString()) } }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange,
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Imagen asociada a la pregunta
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
            Text("No image selected", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Select Image")
        }

        // Asociaciones de imagen o concepto
        Text("Associations:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))

        // Mostrar los pares de asociación
        associations.forEachIndexed { index, association ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                // Concepto o ítem
                TextField(
                    value = association.first,
                    onValueChange = { updatedItem ->
                        // Actualizamos el primer valor del par
                        associations[index] = updatedItem to association.second
                    },
                    label = { Text("Concept or Item") },
                    modifier = Modifier.weight(1f)
                )
                // Descripción o definición
                TextField(
                    value = association.second,
                    onValueChange = { updatedDescription ->
                        // Actualizamos el segundo valor del par
                        associations[index] = association.first to updatedDescription
                    },
                    label = { Text("Description or Definition") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Botón para añadir nuevas asociaciones
        Button(
            onClick = {
                // Añadimos una nueva asociación vacía a la lista
                associations.add("" to "")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Association")
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



