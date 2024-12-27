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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.ui.viewmodel.QuizCreationViewModel

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

        // Opciones para la pregunta
        Text(
            "Options:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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

        // Opciones para la pregunta
        Text(
            "Options:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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

        // Pares de elementos para emparejar
        Text(
            "Matching Pairs:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
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

        // Lista de elementos a ordenar
        Text(
            "Order the following items:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        items.forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${index + 1}.",
                    modifier = Modifier.padding(end = 8.dp)
                ) // Número del elemento
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
    val dropdownExpandedStates = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(correctAnswers.size) { add(false) }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange,
            label = { Text("Enter Question Title: Enter Sentence with Blanks (use {} for blanks)") },
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

        // Asociaciones de imagen o concepto
        Text(
            "Associations:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

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
fun P08Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    onOptionsChange: (List<String>) -> Unit,
    answers: List<String>,
    onAnswersChange: (List<String>) -> Unit,
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
            label = { Text("Enter Question Title: Enter Sentence with Blanks (use {} for blanks)") },
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
                        onAnswersChange(updatedAnswers)
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Enter Answer ${index + 1}") }
                )
            }
        }

        // Botón para añadir nuevos espacios en blanco
        Button(
            onClick = { onAnswersChange(answers + "") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Blank")
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