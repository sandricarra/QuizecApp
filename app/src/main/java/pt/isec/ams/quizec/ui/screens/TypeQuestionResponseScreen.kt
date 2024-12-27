package pt.isec.ams.quizec.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

fun P01(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel, context: Context, isQuestionAnswered: Boolean) {
    val db = FirebaseFirestore.getInstance()
    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    var isAnswerChecked by remember { mutableStateOf(false) }

    var isCorrect by remember { mutableStateOf(false) }

    val showResults by viewModel.quiz.collectAsState()

    val shouldShowResults = showResults?.showResultsImmediately ?: false





    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)


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

            Text(text = "True", modifier = Modifier.padding(start = 8.dp))



            Spacer(modifier = Modifier.width(16.dp))



            RadioButton(

                selected = selectedAnswer == "False",

                onClick = { if (!isQuestionAnswered) selectedAnswer = "False" },

                enabled = !isQuestionAnswered

            )

            Text(text = "False", modifier = Modifier.padding(start = 8.dp))

        }



        Spacer(modifier = Modifier.height(16.dp))



        if (isQuestionAnswered && shouldShowResults) {

            Text(

                text = if (isCorrect) "Correct!" else "Incorrect!",

                color = if (isCorrect) Color.Green else Color.Red,

                style = MaterialTheme.typography.bodyLarge,

                modifier = Modifier.padding(vertical = 8.dp)

            )

        }



        Spacer(modifier = Modifier.height(16.dp))


        // Distribución de los botones

        Row(

            modifier = Modifier

                .fillMaxWidth()

                .padding(top = 8.dp),

            horizontalArrangement = Arrangement.SpaceEvenly

        ) {



            Button(
                onClick = {
                    // Verificar si la respuesta está correcta
                    isCorrect = selectedAnswer == question.correctAnswers.firstOrNull()
                    viewModel.markQuestionAsAnswered(question.id)

                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }

                    // Registrar respuesta en Firebase
                    val response = hashMapOf(
                        "questionId" to (question.id ?: ""), // Asegúrate de que no sea nulo
                        "questionType" to "P01", // Tipo de pregunta como String
                        "selectedAnswer" to (selectedAnswer
                            ?: "None"), // Respuesta seleccionada como String
                        "correctAnswer" to (question.correctAnswers.firstOrNull()
                            ?: "None"), // Respuesta correcta como String
                        "isCorrect" to isCorrect, // Boolean para la validación
                        "timestamp" to System.currentTimeMillis() // Long para la marca de tiempo
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
                enabled = !isQuestionAnswered, // Deshabilitar el botón después de pulsarlo
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
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

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

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

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
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
                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isQuestionAnswered && shouldShowResults) {
            Text(
                text = if (isCorrect) "Correct!" else "Incorrect!",
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Distribución de los botones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = {
                    // Verificar si la respuesta está correcta
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
                enabled = !isQuestionAnswered, // Deshabilitar el botón después de pulsarlo
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Submit")
            }

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

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

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

        // Opciones como checkboxes
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
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
                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el resultado si ya se ha respondido
        if (isQuestionAnswered) {
            Text(
                text = if (isCorrect) "Correct!" else "Incorrect!",
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Distribución de botones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Botón para la pregunta anterior


            // Botón para enviar respuesta
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
                enabled = !isQuestionAnswered, // Deshabilitar si ya está respondida
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Submit")
            }



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

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

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
            "Match the following:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        pairs.forEachIndexed { index, (left, _) ->
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf("") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = left, modifier = Modifier.weight(1f))

                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedOption.isEmpty()) "Select an option" else selectedOption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isQuestionAnswered) { expanded = true }
                            .background(Color.LightGray)
                            .padding(8.dp)
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
                                text = { Text(right) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isQuestionAnswered) {
            Text(
                text = if (isCorrect) "Correct!" else "Incorrect!",
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Distribución de los botones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

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
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Submit")
            }

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

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

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
            "Order the following items:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        items.value.forEachIndexed { index, item ->

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = "${index + 1}.", modifier = Modifier.padding(end = 8.dp))

                Text(text = item, modifier = Modifier.weight(1f).padding(start = 8.dp))

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
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move up")
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
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move down")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isQuestionAnswered) {
            Text(
                text = if (isCorrect) "Correct!" else "Incorrect!",
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para verificar respuesta
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        ) {
            Text("Submit")
        }
    }
}



@Composable
fun P06(
    question: Question,
    viewModel: QuizScreenViewModel,
    isQuestionAnswered: Boolean
) {
    val db = FirebaseFirestore.getInstance()
    val userAnswers = remember { mutableStateOf(List(question.correctAnswers.size) { "" }) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

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

        // Texto base con espacios en blanco
        val baseTextParts = question.baseTextP06.split("{}")
        baseTextParts.forEachIndexed { index, part ->
            Text(text = part, style = MaterialTheme.typography.bodyMedium)
            if (index < userAnswers.value.size) {
                TextField(
                    value = userAnswers.value[index],
                    onValueChange = { newAnswer ->
                        if (!isQuestionAnswered) {
                            val updatedAnswers = userAnswers.value.toMutableList()
                            updatedAnswers[index] = newAnswer
                            userAnswers.value = updatedAnswers
                        }
                    },
                    modifier = Modifier
                        .background(Color.LightGray)
                        .padding(8.dp),
                    label = { Text("Enter Answer ${index + 1}") },
                    enabled = !isQuestionAnswered
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isQuestionAnswered) {
            Text(
                text = if (isCorrect) "Correct!" else "Incorrect!",
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para verificar respuesta
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
            Text("Submit")
        }
    }
}



@Composable
fun P07(question: Question, viewModel: QuizScreenViewModel, isQuestionAnswered: Boolean) {
    val db = FirebaseFirestore.getInstance()
    val selectedAssociations = remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var isCorrect by remember { mutableStateOf(false) }

    // Conceptos y definiciones
    val concepts = question.options
    val definitions = remember { question.correctAnswers.shuffled() }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

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
            "Match the following:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        concepts.forEachIndexed { index, concept ->
            var expanded by remember { mutableStateOf(false) }
            var selectedDefinition by remember { mutableStateOf("") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = concept, modifier = Modifier.weight(1f))

                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedDefinition.isEmpty()) "Select a definition" else selectedDefinition,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .background(Color.LightGray)
                            .padding(8.dp)
                            .then(if (isQuestionAnswered) Modifier.background(Color.Transparent) else Modifier)
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
                                text = { Text(definition) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isQuestionAnswered) {
            Text(
                text = if (isCorrect) "Correct!" else "Incorrect!",
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isCorrect =
                    selectedAssociations.value.toSet() == concepts.zip(question.correctAnswers).toSet()
                viewModel.markQuestionAsAnswered(question.id)

                if (isCorrect) {
                    viewModel.registerCorrectAnswer()
                }

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
            Text("Submit")
        }
    }
}
@Composable
fun P08(question: Question, viewModel: QuizScreenViewModel, isQuestionAnswered: Boolean) {
    val db = FirebaseFirestore.getInstance()
    val userAnswers = remember { mutableStateOf(List(question.correctAnswers.size) { "" }) }
    var isCorrect by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = question.title, style = MaterialTheme.typography.bodyLarge)

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

        // Texto base con espacios en blanco
        val baseTextParts = question.baseTextP06.split("{}")
        baseTextParts.forEachIndexed { index, part ->
            Text(text = part, style = MaterialTheme.typography.bodyMedium)
            if (index < userAnswers.value.size) {
                var expanded by remember { mutableStateOf(false) }
                var selectedOption by remember { mutableStateOf(userAnswers.value[index]) }

                Box {
                    Text(
                        text = if (selectedOption.isEmpty()) "Select an option" else selectedOption,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .background(Color.LightGray)
                            .padding(8.dp)
                            .then(if (isQuestionAnswered) Modifier.background(Color.Transparent) else Modifier)
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
                                text = { Text(option) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isQuestionAnswered) {
            Text(
                text = if (isCorrect) "Correct!" else "Incorrect!",
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isCorrect = userAnswers.value == question.correctAnswers
                viewModel.markQuestionAsAnswered(question.id)

                if (isCorrect) {
                    viewModel.registerCorrectAnswer()
                }

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
            Text("Submit")
        }
    }
}






