package pt.isec.ams.quizec.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import pt.isec.ams.quizec.ui.viewmodel.QuestionHistoryViewModel
@Composable
fun EditP01Question(
    questionId: String,
    initialTitle: String,
    initialAnswer: String?,
    initialImageUrl: String?,
    viewModel: QuestionHistoryViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    // Guardar los cambios en el estado
    var questionTitle by remember { mutableStateOf(initialTitle) }
    var selectedAnswer by remember { mutableStateOf(initialAnswer ?: "") }
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUrl = it.toString() } }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it },
            label = { Text("Enter Question Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Mostrar y permitir cambio de la imagen
        if (imageUrl.isNotEmpty()) {
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

        // Selección de respuesta (True/False)
        Text("True or False Question", modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "True",
                onClick = { selectedAnswer = "True" }
            )
            Text("True", modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "False",
                onClick = { selectedAnswer = "False" }
            )
            Text("False", modifier = Modifier.padding(start = 8.dp))
        }

        // Botón de guardar cambios
        Button(
            onClick = {
                // Guardar cambios en Firestore
                viewModel.updateQuestionTitle(questionId, questionTitle)
                viewModel.updateQuestionAnswer(questionId, listOf(selectedAnswer))
                viewModel.updateQuestionImage(questionId, imageUrl)

                // Llamar a la función onSaveClick después de guardar
                onSaveClick()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditP02Question(
    questionId: String,
    initialTitle: String,
    initialOptions: List<String>,
    initialCorrectAnswer: String?,
    initialImageUrl: String?,
    viewModel: QuestionHistoryViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    var questionTitle by remember { mutableStateOf(initialTitle) }
    var options by remember { mutableStateOf(initialOptions) }
    var selectedAnswer by remember { mutableStateOf(initialCorrectAnswer ?: "") }
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUrl = it.toString() } }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it },
            label = { Text("Enter Question Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Mostrar y permitir cambio de la imagen
        if (imageUrl.isNotEmpty()) {
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

        // Opciones de la pregunta
        Text("Options:", modifier = Modifier.padding(8.dp))
        options.forEachIndexed { index, option ->
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedAnswer == option,
                    onClick = { selectedAnswer = option }
                )
                TextField(
                    value = option,
                    onValueChange = { newOption ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = newOption
                        options = updatedOptions
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    label = { Text("Option ${index + 1}") }
                )
            }
        }

        // Botón para añadir una nueva opción
        Button(
            onClick = { options = options + "" }, // Añadir una opción vacía
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Option")
        }

        // Botón de guardar cambios
        Button(
            onClick = {
                // Guardar cambios en Firestore
                viewModel.updateQuestionTitle(questionId, questionTitle)
                viewModel.updateQuestionOptions(questionId, options)
                viewModel.updateQuestionAnswer(questionId, listOf(selectedAnswer))
                viewModel.updateQuestionImage(questionId, imageUrl)

                // Llamar a la función onSaveClick después de guardar
                onSaveClick()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditP03Question(
    questionId: String,
    initialTitle: String,
    initialOptions: List<String>,
    initialSelectedAnswers: List<String>,
    initialImageUrl: String?,
    viewModel: QuestionHistoryViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    var questionTitle by remember { mutableStateOf(initialTitle) }
    var options by remember { mutableStateOf(initialOptions) }
    var selectedAnswers by remember { mutableStateOf(initialSelectedAnswers) }
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUrl = it.toString() } }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it },
            label = { Text("Enter Question Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Mostrar y permitir cambio de la imagen
        if (imageUrl.isNotEmpty()) {
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

        // Opciones de la pregunta
        Text("Options:", modifier = Modifier.padding(8.dp))
        options.forEachIndexed { index, option ->
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selectedAnswers.contains(option),
                    onCheckedChange = { isChecked ->
                        val updatedAnswers = if (isChecked) {
                            selectedAnswers + option
                        } else {
                            selectedAnswers - option
                        }
                        selectedAnswers = updatedAnswers
                    }
                )
                TextField(
                    value = option,
                    onValueChange = { newOption ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = newOption
                        options = updatedOptions
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    label = { Text("Option ${index + 1}") }
                )
            }
        }

        // Botón para añadir una nueva opción
        Button(
            onClick = { options = options + "" }, // Añadir una opción vacía
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Option")
        }

        // Botón de guardar cambios
        Button(
            onClick = {
                // Guardar cambios en Firestore
                viewModel.updateQuestionTitle(questionId, questionTitle)
                viewModel.updateQuestionOptions(questionId, options)
                viewModel.updateQuestionAnswer(questionId, selectedAnswers)
                viewModel.updateQuestionImage(questionId, imageUrl)

                // Llamar a la función onSaveClick después de guardar
                onSaveClick()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditP04Question(
    questionId: String,
    initialTitle: String,
    initialOptions: List<String>,  // Cambiado a List<String> en lugar de List<Pair<String, String>>
    initialImageUrl: String?,
    viewModel: QuestionHistoryViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    var questionTitle by remember { mutableStateOf(initialTitle) }
    var options by remember { mutableStateOf(initialOptions) }
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUrl = it.toString() } }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it },
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Mostrar y permitir cambio de la imagen
        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Image")
        }

        // Opciones mostradas como pares
        Text(
            "Matching Pairs:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        options.forEachIndexed { index, option ->
            val (left, right) = option.split("->").map { it.trim() }.let {
                it.firstOrNull().orEmpty() to it.getOrNull(1).orEmpty()
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                // Campo de texto para la columna izquierda
                TextField(
                    value = left,
                    onValueChange = { newLeft ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = "$newLeft -> $right"
                        options = updatedOptions
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Column A ${index + 1}") }
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Campo de texto para la columna derecha
                TextField(
                    value = right,
                    onValueChange = { newRight ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = "$left -> $newRight"
                        options = updatedOptions
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Column B ${index + 1}") }
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Botón para eliminar una opción
                IconButton(onClick = {
                    val updatedOptions = options.toMutableList()
                    updatedOptions.removeAt(index)
                    options = updatedOptions
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove option")
                }
            }
        }

        // Botón para añadir una nueva opción vacía
        Button(
            onClick = {
                options = options + " -> " // Añadir una nueva opción vacía
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Option")
        }

        // Botón para guardar los cambios
        Button(
            onClick = {
                // Guardar cambios en Firestore
                viewModel.updateQuestionTitle(questionId, questionTitle)
                viewModel.updateQuestionOptions(questionId, options)
                viewModel.updateQuestionAnswer(questionId, options)
                viewModel.updateQuestionImage(questionId, imageUrl)

                // Llamar a la función onSaveClick después de guardar
                onSaveClick()
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditP05Question(
    questionId: String,
    initialTitle: String,
    initialItems: List<String>,
    initialImageUrl: String?,
    viewModel: QuestionHistoryViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    var questionTitle by remember { mutableStateOf(initialTitle) }
    var items by remember { mutableStateOf(initialItems) }
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUrl = it.toString() } }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it },
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        // Mostrar y permitir cambio de la imagen
        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Image")
        }

        // Lista de elementos a ordenar
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${index + 1}.",
                    modifier = Modifier.padding(end = 8.dp)
                )
                TextField(
                    value = item,
                    onValueChange = { newItem ->
                        val updatedItems = items.toMutableList()
                        updatedItems[index] = newItem
                        items = updatedItems
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Item ${index + 1}") }
                )
                IconButton(onClick = {
                    val updatedItems = items.toMutableList()
                    updatedItems.removeAt(index)
                    items = updatedItems
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove item")
                }
            }
        }

        Button(
            onClick = {
                items = items + ""
            },
            enabled = items.size < 6
        ) {
            Text("Add Item")
        }

        Button(
            onClick = {
                // Guardar cambios en Firestore
                viewModel.updateQuestionTitle(questionId, questionTitle)
                viewModel.updateQuestionOptions(questionId, items)
                viewModel.updateQuestionAnswer(questionId, items)
                viewModel.updateQuestionImage(questionId, imageUrl)

                // Llamar a la función onSaveClick después de guardar
                onSaveClick()
            }
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditP06Question(
    questionId: String,
    initialTitle: String,
    initialOptions: List<String>,
    initialCorrectAnswers: List<String>,
    initialImageUrl: String?,
    viewModel: QuestionHistoryViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    var questionTitle by remember { mutableStateOf(initialTitle) }
    var options by remember { mutableStateOf(initialOptions) }
    var correctAnswers by remember { mutableStateOf(initialCorrectAnswers) }
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUrl = it.toString() } }
    )

    // Control del estado de expansión de los DropdownMenus
    val dropdownExpandedStates = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(correctAnswers.size) { add(false) }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it },
            label = { Text("Enter Question Title: Enter Sentence with Blanks (use {} for blanks)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            placeholder = { Text("Example: The {} is {}.") }
        )

        // Imagen asociada a la pregunta
        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                "No image selected",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Select Image")
        }

        // Opciones para completar los espacios en blanco
        Text(
            "Options:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
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
                        options = updatedOptions
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Option ${index + 1}") }
                )
                IconButton(onClick = {
                    val updatedOptions = options.toMutableList()
                    updatedOptions.removeAt(index)
                    options = updatedOptions
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove option")
                }
            }
        }

        // Botón para añadir nuevas opciones
        Button(
            onClick = { options = options + "" },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Option")
        }

        // Selección de respuestas correctas para cada espacio en blanco
        Text(
            "Correct Answers:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
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
                                    correctAnswers = updatedAnswers
                                    dropdownExpandedStates[index] = false
                                },
                                text = { Text(option) }
                            )
                        }
                    }
                }
            }
        }

        // Botón para guardar los cambios
        Button(
            onClick = {
                // Guardar cambios en Firestore
                viewModel.updateQuestionTitle(questionId, questionTitle)
                viewModel.updateQuestionOptions(questionId, options)
                viewModel.updateQuestionAnswer(questionId, correctAnswers)
                viewModel.updateQuestionImage(questionId, imageUrl)

                // Llamar a la función onSaveClick después de guardar
                onSaveClick()
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditP07Question(
    questionId: String,
    initialTitle: String,
    initialOptions: List<String>, // Representa los conceptos (columna A)
    initialCorrectAnswers: List<String>, // Representa las definiciones (columna B)
    initialImageUrl: String?,
    viewModel: QuestionHistoryViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    var questionTitle by remember { mutableStateOf(initialTitle) }
    var options by remember { mutableStateOf(initialOptions) } // Columna A
    var correctAnswers by remember { mutableStateOf(initialCorrectAnswers) } // Columna B
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUrl = it.toString() } }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it },
            label = { Text("Enter Question Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Imagen asociada a la pregunta
        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                "No image selected",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Select Image")
        }

        // Tabla de conceptos y definiciones
        Text(
            "Associations (Concept -> Definition):",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                // Campo de texto para el concepto (columna A)
                TextField(
                    value = option,
                    onValueChange = { newOption ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = newOption
                        options = updatedOptions
                    },
                    label = { Text("Concept ${index + 1}") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                // Campo de texto para la definición (columna B)
                TextField(
                    value = correctAnswers.getOrNull(index) ?: "",
                    onValueChange = { newAnswer ->
                        val updatedAnswers = correctAnswers.toMutableList()
                        if (index < updatedAnswers.size) {
                            updatedAnswers[index] = newAnswer
                        } else {
                            updatedAnswers.add(newAnswer)
                        }
                        correctAnswers = updatedAnswers
                    },
                    label = { Text("Definition ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                // Botón para eliminar la fila
                IconButton(onClick = {
                    val updatedOptions = options.toMutableList()
                    val updatedAnswers = correctAnswers.toMutableList()
                    updatedOptions.removeAt(index)
                    if (index < updatedAnswers.size) {
                        updatedAnswers.removeAt(index)
                    }
                    options = updatedOptions
                    correctAnswers = updatedAnswers
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Association")
                }
            }
        }

        // Botón para añadir nuevas asociaciones
        Button(
            onClick = {
                options = options + ""
                correctAnswers = correctAnswers + ""
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Association")
        }

        // Botón para guardar los cambios
        Button(
            onClick = {
                viewModel.updateQuestionTitle(questionId, questionTitle)
                viewModel.updateQuestionOptions(questionId, options)
                viewModel.updateQuestionAnswer(questionId, correctAnswers)
                viewModel.updateQuestionImage(questionId, imageUrl)
                onSaveClick()
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditP08Question(
    questionId: String,
    initialTitle: String,
    initialAnswers: List<String>,
    initialImageUrl: String?,
    viewModel: QuestionHistoryViewModel = viewModel(),
    onSaveClick: () -> Unit
) {
    var questionTitle by remember { mutableStateOf(initialTitle) }
    var answers by remember { mutableStateOf(initialAnswers) }
    var imageUrl by remember { mutableStateOf(initialImageUrl ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imageUrl = it.toString() } }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = { questionTitle = it },
            label = { Text("Enter Question Title: Enter Sentence with Blanks (use {} for blanks)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            placeholder = { Text("Example: The {} is {}.") }
        )

        // Imagen asociada a la pregunta
        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                "No image selected",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Select Image")
        }

        // Respuestas para los espacios en blanco
        Text(
            "Answers:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        answers.forEachIndexed { index, answer ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text("Blank ${index + 1}:", modifier = Modifier.padding(end = 8.dp))
                TextField(
                    value = answer,
                    onValueChange = { newAnswer ->
                        val updatedAnswers = answers.toMutableList()
                        updatedAnswers[index] = newAnswer
                        answers = updatedAnswers
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Enter Answer ${index + 1}") }
                )
                IconButton(onClick = {
                    val updatedAnswers = answers.toMutableList()
                    updatedAnswers.removeAt(index)
                    answers = updatedAnswers
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Blank")
                }
            }
        }

        // Botón para añadir nuevos espacios en blanco
        Button(
            onClick = { answers = answers + "" },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Blank")
        }

        // Botón para guardar los cambios
        Button(
            onClick = {
                viewModel.updateQuestionTitle(questionId, questionTitle)
                viewModel.updateQuestionAnswer(questionId, answers)
                viewModel.updateQuestionImage(questionId, imageUrl)
                onSaveClick()
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Save Changes")
        }
    }
}

