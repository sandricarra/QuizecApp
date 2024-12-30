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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.data.models.QuestionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P01Question(
    questionTitle: String, // Estado compartido para el título de la pregunta
    onTitleChange: (String) -> Unit, // Callback para actualizar el título

    selectedAnswer: String?, // Respuesta seleccionada
    onAnswerChange: (String) -> Unit, // Callback para actualizar la respuesta
    imageUrl: String?, // URL de la imagen
    onImageChange: (String?) -> Unit // Callback para actualizar la URL de la imagen

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
            label = { Text(stringResource(R.string.question_title_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD)
            ),
        )

        // Mostrar la imagen seleccionada
        if (imageUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onImageChange(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove image", tint = Color.Red)
                }
            }
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription =   stringResource(R.string.select_image),
                    tint = Color.Gray,
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = stringResource(R.string.no_image_selected),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P02Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    options: List<String>, // Lista de opciones disponibles
    onOptionsChange: (List<String>) -> Unit, // Callback para actualizar las opciones
    selectedOption: String?, // Opción seleccionada como respuesta correcta
    onSelectedOptionChange: (String) -> Unit, // Callback para actualizar la opción correcta
    imageUrl: String?,
    onImageChange: (String?) -> Unit
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
            label = { Text(stringResource(R.string.question_title_label)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD)
            )
        )

        // Imagen asociada a la pregunta
        if (imageUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onImageChange(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove image", tint = Color.Red)
                }
            }
        }
        else {
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
                    onClick = { onSelectedOptionChange(option) } // Actualizar la opción seleccionada
                )
                TextField(
                    value = option,
                    onValueChange = { newOption ->
                        val updatedOptions = options.toMutableList()
                        updatedOptions[index] = newOption
                        onOptionsChange(updatedOptions)
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    label = { Text(stringResource(R.string.option_label) + " ${index + 1}") }
                )
                IconButton(
                    onClick = {
                        val updatedOptions = options.toMutableList()
                        updatedOptions.removeAt(index)
                        onOptionsChange(updatedOptions)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {


                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_option))
                }
            }
        }

        // Botón para añadir una nueva opción
        Button(
            onClick = { onOptionsChange(options + "") }, // Añadir una opción vacía
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.add_option))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P03Question(
    questionTitle: String, // Título de la pregunta
    onTitleChange: (String) -> Unit, // Callback para actualizar el título

    options: List<String>, // Opciones dinámicas para la pregunta
    onOptionsChange: (List<String>) -> Unit, // Callback para actualizar las opciones

    selectedOptions: List<String>, // Respuestas seleccionadas (varias respuestas correctas)
    onSelectedOptionsChange: (List<String>) -> Unit, // Callback para actualizar las respuestas seleccionadas

    imageUrl: String?, // URL de la imagen
    onImageChange: (String?) -> Unit // Callback para actualizar la URL de la imagen
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
            label = { Text(stringResource(R.string.question_title_label)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD)
            )
        )

        // Imagen asociada a la pregunta
        if (imageUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onImageChange(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove image", tint = Color.Red)
                }
            }
        }
        else {
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
            Text(stringResource(R.string.select_image))
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
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
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
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_option))
                }
            }
        }

        // Botón para añadir una nueva opción
        Button(
            onClick = { onOptionsChange(options + "") }, // Añadir una opción vacía
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.add_option))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P04Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    pairs: List<Pair<String, String>>, // Lista de pares de opciones
    onPairsChange: (List<Pair<String, String>>) -> Unit, // Callback para actualizar los pares
    imageUrl: String?,
    onImageChange: (String?) -> Unit
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
            label = { Text(stringResource(R.string.question_title_label)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD)
            )
        )

        // Imagen asociada a la pregunta
        if (imageUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onImageChange(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove image", tint = Color.Red)
                }
            }
        }
        else {
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
            Text(stringResource(R.string.select_image))
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
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
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
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    label = { Text("Column B ${index + 1}") }
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Botón para eliminar un par
                IconButton(onClick = {
                    val updatedPairs = pairs.toMutableList()
                    updatedPairs.removeAt(index)
                    onPairsChange(updatedPairs)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_pair))
                }
            }
        }

        // Botón para añadir un nuevo par
        Button(
            onClick = { onPairsChange(pairs + ("" to "")) }, // Añadir un par vacío
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.add_pair))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P05Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    items: List<String>,
    onItemsChange: (List<String>) -> Unit,
    imageUrl: String?,
    onImageChange: (String?) -> Unit
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
            label = { Text(stringResource(R.string.question_title_label)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD)
            ),
        )

        // Imagen asociada a la pregunta
        if (imageUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onImageChange(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove image", tint = Color.Red)
                }
            }
        }
        else {
            Text(
                stringResource(R.string.no_image_selected),
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
            stringResource(R.string.items_to_sort),
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
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                )
                IconButton(
                    onClick = {
                        val updatedItems = items.toMutableList()
                        updatedItems.removeAt(index)
                        onItemsChange(updatedItems)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_item))
                }
            }
        }

        // Botón para añadir un nuevo elemento
        Button(
            onClick = { onItemsChange(items + "") }, // Añade un elemento vacío
            modifier = Modifier.padding(vertical = 8.dp),
            enabled = items.size < 6 // Máximo 6 elementos
        ) {
            Text(stringResource(R.string.add_item))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P06Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    options: List<String>,
    onOptionsChange: (List<String>) -> Unit,
    correctAnswers: List<String>,
    onCorrectAnswersChange: (List<String>) -> Unit,
    imageUrl: String?,
    onImageChange: (String?) -> Unit
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
            label = { Text(stringResource(R.string.question_tittle_label_black)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            placeholder = { Text("Example: The {} is {}.") }
        )

        // Imagen asociada a la pregunta
        if (imageUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onImageChange(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove image", tint = Color.Red)
                }
            }
        }
        else {
            Text(
                stringResource(R.string.no_image_selected),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.select_image))
        }

        // Opciones para completar los espacios en blanco
        Text(
            stringResource(R.string.options),
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
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    label = { Text(stringResource(R.string.option_label) + " ${index + 1}") }
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
            Text(stringResource(R.string.add_option))
        }

        // Selección de respuestas correctas para cada espacio en blanco
        Text(
            stringResource(R.string.correct_answers),
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
                        Text(answer.ifEmpty { stringResource(R.string.select_answer) })
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

        // Botón para añadir nuevos espacios en blanco
        Button(
            onClick = {
                if (correctAnswers.size < options.size) { // Evitar más espacios en blanco que opciones
                    onCorrectAnswersChange(correctAnswers + "")
                    dropdownExpandedStates.add(false) // Añadir un nuevo estado para el DropdownMenu
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.add_blank))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P07Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    associations: SnapshotStateList<Pair<String, String>>, // Lista reactiva de asociaciones
    onAssociationsChange: (SnapshotStateList<Pair<String, String>>) -> Unit,
    imageUrl: String?,
    onImageChange: (String?) -> Unit
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
            label = { Text(stringResource(R.string.question_title_label)) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Imagen asociada a la pregunta
        if (imageUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onImageChange(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove image", tint = Color.Red)
                }
            }
        }
        else {
            Text(
                stringResource(R.string.no_image_selected),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.select_image))
        }

        // Asociaciones de imagen o concepto
        Text(
            stringResource(R.string.associations),
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
                    label = { Text(stringResource(R.string.concept_or_item)) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    modifier = Modifier.weight(1f)
                )
                // Descripción o definición
                TextField(
                    value = association.second,
                    onValueChange = { updatedDescription ->
                        // Actualizamos el segundo valor del par
                        associations[index] = association.first to updatedDescription
                    },
                    label = { Text(stringResource(R.string.description_or_definition)) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P08Question(
    questionTitle: String,
    onTitleChange: (String) -> Unit,
    onOptionsChange: (List<String>) -> Unit,
    answers: List<String>,
    onAnswersChange: (List<String>) -> Unit,
    imageUrl: String?,
    onImageChange: (String?) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { (it.toString()) } }
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        // Campo de texto para el título de la pregunta
        TextField(
            value = questionTitle,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.question_tittle_label_black)) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            placeholder = { Text("Example: The {} is {}.") }
        )

        // Imagen asociada a la pregunta
        if (imageUrl != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onImageChange(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove image", tint = Color.Red)
                }
            }
        }
        else {
            Text(
                stringResource(R.string.no_image_selected),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.select_image))
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
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    label = { Text(stringResource(R.string.answer_label) + " ${index + 1}") }
                )
            }
        }

        // Botón para añadir nuevos espacios en blanco
        Button(
            onClick = { onAnswersChange(answers + "") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.add_blank))
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
    onUpdate: () -> Unit,
    onAddQuestion: () -> Unit // Nueva función para agregar la pregunta
) {
    Button(
        onClick = {
            if (questionTitle.isNotBlank() && correctAnswers.isNotEmpty()) {
                onAddQuestion() // Llama a la función para agregar la pregunta
                onUpdate() // Limpia los campos
            }
        },
        enabled = questionTitle.isNotBlank() && correctAnswers.isNotEmpty(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.add_question))
    }
}
