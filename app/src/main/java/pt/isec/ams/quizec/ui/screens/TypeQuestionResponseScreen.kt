package pt.isec.ams.quizec.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.ui.viewmodel.QuizScreenViewModel



@Composable
fun P01(
    question: Question,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    viewModel: QuizScreenViewModel,
    context: Context,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf(false) }
    val showResults by viewModel.quiz.collectAsState()
    val shouldShowResults = showResults?.showResultsImmediately ?: false

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar imagen si está disponible
        question.imageUrl?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Question Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Crop
            )
        }


        // Opciones de respuesta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAnswer == "True",
                        onClick = { if (!isQuestionAnswered) selectedAnswer = "True" },
                        enabled = !isQuestionAnswered
                    )
                    Text(
                        text = "True",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAnswer == "False",
                        onClick = { if (!isQuestionAnswered) selectedAnswer = "False" },
                        enabled = !isQuestionAnswered
                    )
                    Text(
                        text = "False",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Mostrar resultado si la pregunta ya ha sido respondida
            if (isQuestionAnswered && shouldShowResults) {
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la respuesta
            Button(
                onClick = {
                    isCorrect = selectedAnswer == question.correctAnswers.firstOrNull()
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Registrar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""),
                        "questionType" to "P01",
                        "selectedAnswer" to (selectedAnswer ?: "None"),
                        "correctAnswer" to (question.correctAnswers.firstOrNull() ?: "None"),
                        "isCorrect" to isCorrect,
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("responses").add(response)
                        .addOnSuccessListener { Log.d("Firebase", "Response saved successfully!") }
                        .addOnFailureListener { e -> Log.e("Firebase", "Error saving response", e) }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuestionAnswered) {
                        if (isCorrect) Color.Green else Color.Red
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                ),
                enabled = !isQuestionAnswered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        }
    }




@Composable
fun P02(
    question: Question,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    viewModel: QuizScreenViewModel,
    context: Context,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf(false) }
    val showResults by viewModel.quiz.collectAsState()
    val shouldShowResults = showResults?.showResultsImmediately ?: false

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar imagen si está disponible
            question.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Opciones de respuesta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                question.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAnswer == option,
                            onClick = { if (!isQuestionAnswered) selectedAnswer = option },
                            enabled = !isQuestionAnswered
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Mostrar resultado si la pregunta ya ha sido respondida
            if (isQuestionAnswered && shouldShowResults) {
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la respuesta
            Button(
                onClick = {
                    isCorrect = selectedAnswer == question.correctAnswers.firstOrNull()
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Registrar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""),
                        "questionType" to "P02",
                        "selectedAnswer" to (selectedAnswer ?: "None"),
                        "correctAnswer" to (question.correctAnswers.firstOrNull() ?: "None"),
                        "isCorrect" to isCorrect,
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("responses").add(response)
                        .addOnSuccessListener { Log.d("Firebase", "Response saved successfully!") }
                        .addOnFailureListener { e -> Log.e("Firebase", "Error saving response", e) }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuestionAnswered) {
                        if (isCorrect) Color.Green else Color.Red
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                ),
                enabled = !isQuestionAnswered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        }
    }







@Composable
fun P03(
    question: Question,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    viewModel: QuizScreenViewModel,
    context: Context,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    var selectedAnswers by remember { mutableStateOf(setOf<String>()) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar imagen si está disponible
            question.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Opciones de respuesta como checkboxes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                question.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedAnswers.contains(option),
                            onCheckedChange = { isChecked ->
                                if (!isQuestionAnswered) {
                                    selectedAnswers = if (isChecked) {
                                        selectedAnswers + option
                                    } else {
                                        selectedAnswers - option
                                    }
                                }
                            },
                            enabled = !isQuestionAnswered
                        )
                        Text(
                            text = option,
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Mostrar resultado si la pregunta ya ha sido respondida
            if (isQuestionAnswered) {
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la respuesta
            Button(
                onClick = {
                    isCorrect = selectedAnswers == question.correctAnswers.toSet()
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Guardar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""),
                        "questionType" to "P03",
                        "selectedAnswers" to selectedAnswers.toList(),
                        "correctAnswers" to question.correctAnswers,
                        "isCorrect" to isCorrect,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("responses").add(response)
                        .addOnSuccessListener { Log.d("Firebase", "Response saved successfully!") }
                        .addOnFailureListener { e -> Log.e("Firebase", "Error saving response", e) }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuestionAnswered) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                enabled = !isQuestionAnswered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        }
    }





@Composable
fun P04(
    question: Question,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    viewModel: QuizScreenViewModel,
    context: Context,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    val (selectedPairs, setSelectedPairs) = remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var isCorrect by remember { mutableStateOf(false) }

    // Dividir las opciones en pares
    val pairs = question.options.map { option ->
        val parts = option.split(" -> ")
        parts[0] to parts[1]
    }

    // Mezclar solo la columna B
    val shuffledRightOptions = remember { pairs.map { it.second }.shuffled() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar imagen si está disponible
            question.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = "Match the following:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Mostrar pares de opciones
            pairs.forEachIndexed { index, (left, _) ->
                var expanded by remember { mutableStateOf(false) }
                var selectedOption by remember { mutableStateOf("") }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = left,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedOption.isEmpty()) "Select an option" else selectedOption,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isQuestionAnswered) { expanded = true }
                                .background(Color.LightGray.copy(alpha = 0.2f))
                                .padding(12.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            shuffledRightOptions.forEach { right ->
                                DropdownMenuItem(
                                    onClick = {
                                        if (!isQuestionAnswered) {
                                            selectedOption = right
                                            expanded = false
                                            val updatedPairs = selectedPairs.toMutableList()
                                            if (index < updatedPairs.size) {
                                                updatedPairs[index] = left to right
                                            } else {
                                                updatedPairs.add(left to right)
                                            }
                                            setSelectedPairs(updatedPairs)
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = right,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar resultado si la pregunta ya ha sido respondida
            if (isQuestionAnswered) {
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la respuesta
            Button(
                onClick = {
                    isCorrect = selectedPairs.toSet() == pairs.toSet()
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Registrar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""),
                        "questionType" to "P04",
                        "selectedAnswers" to selectedPairs.toList(),
                        "correctAnswers" to pairs.toList(),
                        "isCorrect" to isCorrect,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("responses").add(response)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Response saved successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error saving response", e)
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuestionAnswered) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                enabled = !isQuestionAnswered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        }
    }




@Composable
fun P05(
    question: Question,
    viewModel: QuizScreenViewModel,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    val items = remember { mutableStateOf(question.options.shuffled().toMutableList()) }
    var isCorrect by remember { mutableStateOf(false) }

   Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar imagen si está disponible
            question.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = "Order the following items:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Mostrar elementos ordenables
            items.value.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (index > 0) {
                                val updatedItems = items.value.toMutableList()
                                val temp = updatedItems[index]
                                updatedItems[index] = updatedItems[index - 1]
                                updatedItems[index - 1] = temp
                                items.value = updatedItems
                            }
                        },
                        enabled = !isQuestionAnswered
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move up",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            if (index < items.value.size - 1) {
                                val updatedItems = items.value.toMutableList()
                                val temp = updatedItems[index]
                                updatedItems[index] = updatedItems[index + 1]
                                updatedItems[index + 1] = temp
                                items.value = updatedItems
                            }
                        },
                        enabled = !isQuestionAnswered
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Move down",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar resultado si la pregunta ya ha sido respondida
            if (isQuestionAnswered) {
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la respuesta
            Button(
                onClick = {
                    isCorrect = items.value == question.correctAnswers
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Registrar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""),
                        "questionType" to "P05",
                        "selectedAnswers" to items.value.toList(),
                        "correctAnswers" to question.correctAnswers,
                        "isCorrect" to isCorrect,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("responses").add(response)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Response saved successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error saving response", e)
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuestionAnswered) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                enabled = !isQuestionAnswered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P06(
    question: Question,
    viewModel: QuizScreenViewModel,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    val userAnswers = remember { mutableStateOf(List(question.correctAnswers.size) { "" }) }
    var isCorrect by remember { mutableStateOf(false) }

    // Construir el texto dinámico con las respuestas del usuario
    val dynamicText = buildString {
        val parts = question.title.split("{}")
        parts.forEachIndexed { index, part ->
            append(part)
            if (index < userAnswers.value.size) {
                append(userAnswers.value[index].ifEmpty { "______" }) // Mostrar "______" si no hay respuesta
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar imagen si está disponible
            question.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Mostrar el texto dinámico con las respuestas del usuario
            Text(
                text = dynamicText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Campos de texto para las respuestas
            userAnswers.value.forEachIndexed { index, answer ->
                TextField(
                    value = answer,
                    onValueChange = { newAnswer ->
                        if (!isQuestionAnswered) {
                            val updatedAnswers = userAnswers.value.toMutableList()
                            updatedAnswers[index] = newAnswer
                            userAnswers.value = updatedAnswers
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFE3F2FD), // Fondo claro
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    label = { Text("Enter Answer ${index + 1}") },
                    enabled = !isQuestionAnswered,
                    shape = MaterialTheme.shapes.medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar resultado si la pregunta ya ha sido respondida
            if (isQuestionAnswered) {
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la respuesta
            Button(
                onClick = {
                    isCorrect = userAnswers.value == question.correctAnswers
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Registrar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""),
                        "questionType" to "P06",
                        "selectedAnswers" to userAnswers.value,
                        "correctAnswers" to question.correctAnswers,
                        "isCorrect" to isCorrect,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("responses").add(response)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Response saved successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error saving response", e)
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuestionAnswered) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                enabled = !isQuestionAnswered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        }
    }







@Composable
fun P07(
    question: Question,
    viewModel: QuizScreenViewModel,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    val selectedAssociations = remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var isCorrect by remember { mutableStateOf(false) }
    val imagePainter = rememberAsyncImagePainter(question.imageUrl)
    // Conceptos y definiciones
    val concepts = question.options
    val definitions = remember { question.correctAnswers.shuffled() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar imagen si está disponible
        question.imageUrl?.let { imageUrl ->
            Log.d("ImageDebug", "Loading image: $imageUrl")
            Image(
                painter = imagePainter,

                contentDescription = "Question Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Log.d("ImageDebug", "Image URL is null or empty")
        }

            Text(
                text = "Match the following:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Mostrar conceptos y definiciones
            concepts.forEachIndexed { index, concept ->
                var expanded by remember { mutableStateOf(false) }
                var selectedDefinition by remember { mutableStateOf("") }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = concept,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedDefinition.isEmpty()) "Select a definition" else selectedDefinition,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isQuestionAnswered) { expanded = true }
                                .background(Color.LightGray.copy(alpha = 0.2f))
                                .padding(12.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            definitions.forEach { definition ->
                                DropdownMenuItem(
                                    onClick = {
                                        if (!isQuestionAnswered) {
                                            selectedDefinition = definition
                                            expanded = false
                                            val updatedAssociations = selectedAssociations.value.toMutableList()
                                            if (index < updatedAssociations.size) {
                                                updatedAssociations[index] = concept to definition
                                            } else {
                                                updatedAssociations.add(concept to definition)
                                            }
                                            selectedAssociations.value = updatedAssociations
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = definition,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar resultado si la pregunta ya ha sido respondida
            if (isQuestionAnswered) {
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la respuesta
            Button(
                onClick = {
                    isCorrect =
                        selectedAssociations.value.toSet() == concepts.zip(question.correctAnswers).toSet()
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Registrar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""),
                        "questionType" to "P07",
                        "selectedAnswers" to selectedAssociations.value,
                        "correctAnswers" to concepts.zip(question.correctAnswers),
                        "isCorrect" to isCorrect,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("responses").add(response)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Response saved successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error saving response", e)
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuestionAnswered) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                enabled = !isQuestionAnswered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        }
    }







@Composable
fun P08(
    question: Question,
    viewModel: QuizScreenViewModel,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    val userAnswers = remember { mutableStateOf(List(question.correctAnswers.size) { "" }) }
    var isCorrect by remember { mutableStateOf(false) }

    // Construir el texto dinámico con las respuestas del usuario
    val dynamicText = buildString {
        val parts = question.title.split("{}")
        parts.forEachIndexed { index, part ->
            append(part)
            if (index < userAnswers.value.size) {
                append(userAnswers.value[index].ifEmpty { "______" }) // Mostrar "______" si no hay respuesta
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Mostrar imagen si está disponible
            question.imageUrl?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Question Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Mostrar el texto dinámico con las respuestas del usuario
            Text(
                text = dynamicText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Desplegables para las respuestas
            userAnswers.value.forEachIndexed { index, answer ->
                var expanded by remember { mutableStateOf(false) }
                var selectedOption by remember { mutableStateOf(answer) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = if (selectedOption.isEmpty()) "Select an option" else selectedOption,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isQuestionAnswered) { expanded = true }
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            .padding(12.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = MaterialTheme.shapes.small
                            )
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        question.correctAnswers.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    if (!isQuestionAnswered) {
                                        selectedOption = option
                                        expanded = false
                                        val updatedAnswers = userAnswers.value.toMutableList()
                                        updatedAnswers[index] = option
                                        userAnswers.value = updatedAnswers
                                    }
                                },
                                text = {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar resultado si la pregunta ya ha sido respondida
            if (isQuestionAnswered) {
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar la respuesta
            Button(
                onClick = {
                    isCorrect = userAnswers.value == question.correctAnswers
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Registrar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""),
                        "questionType" to "P08",
                        "selectedAnswers" to userAnswers.value,
                        "correctAnswers" to question.correctAnswers,
                        "isCorrect" to isCorrect,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("responses").add(response)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Response saved successfully!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error saving response", e)
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuestionAnswered) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                enabled = !isQuestionAnswered,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        }
    }


