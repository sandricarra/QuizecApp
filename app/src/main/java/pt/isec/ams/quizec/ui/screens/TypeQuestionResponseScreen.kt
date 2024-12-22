package pt.isec.ams.quizec.ui.screens

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
import pt.isec.ams.quizec.data.models.Question
import pt.isec.ams.quizec.viewmodel.QuizScreenViewModel

@Composable
fun P01(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedAnswer == "True",
                onClick = { if (!isAnswerChecked) selectedAnswer = "True" },
                enabled = !isAnswerChecked
            )
            Text(text = "True", modifier = Modifier.padding(start = 8.dp))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedAnswer == "False",
                onClick = { if (!isAnswerChecked) selectedAnswer = "False" },
                enabled = !isAnswerChecked
            )
            Text(text = "False", modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isAnswerChecked) {
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
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("⬅\uFE0F")
            }
            Button(
                onClick = {
                    // Verificar si la respuesta está correcta
                    isCorrect = selectedAnswer == question.correctAnswers.firstOrNull()
                    isAnswerChecked = true
                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswerChecked) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("✅")
            }
            Button(
                onClick = {
                    isAnswerChecked = false
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("➡\uFE0F")
            }
        }
    }
}


@Composable
fun P02(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
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
                        onClick = { if (!isAnswerChecked) selectedAnswer = option },
                        enabled = !isAnswerChecked
                    )
                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isAnswerChecked) {
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
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("⬅\uFE0F")
            }
            Button(
                onClick = {
                    isCorrect = selectedAnswer == question.correctAnswers.firstOrNull()
                    isAnswerChecked = true
                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswerChecked) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("✅")
            }
            Button(
                onClick = {
                    isAnswerChecked = false
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("➡\uFE0F")
            }
        }
    }
}


@Composable
fun P03(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel) {
    var selectedAnswers by remember { mutableStateOf(setOf<String>()) }
    var isAnswerChecked by remember { mutableStateOf(false) }
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
                            if (!isAnswerChecked) {
                                selectedAnswers = if (isChecked) {
                                    selectedAnswers + option
                                } else {
                                    selectedAnswers - option
                                }
                            }
                        },
                        enabled = !isAnswerChecked
                    )
                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isAnswerChecked) {
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
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("⬅\uFE0F")
            }
            Button(
                onClick = {
                    isCorrect = selectedAnswers == question.correctAnswers.toSet()
                    isAnswerChecked = true
                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswerChecked) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("✅")
            }
            Button(
                onClick = {
                    isAnswerChecked = false
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("➡\uFE0F")
            }
        }
    }
}


@Composable
fun P04(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel) {
    val (selectedPairs, setSelectedPairs) = remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var isAnswerChecked by remember { mutableStateOf(false) }
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
                            .clickable(enabled = !isAnswerChecked) { expanded = true }
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
                                    if (!isAnswerChecked) {
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

        if (isAnswerChecked) {
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
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("⬅\uFE0F")
            }
            Button(
                onClick = {
                    isCorrect = selectedPairs.toSet() == pairs.toSet()
                    isAnswerChecked = true
                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswerChecked) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("✅")
            }
            Button(
                onClick = {
                    isAnswerChecked = false
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("➡\uFE0F")
            }
        }
    }
}

@Composable
fun P05(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel) {
    val items = remember { mutableStateOf(question.options.shuffled().toMutableList()) }
    var isAnswerChecked by remember { mutableStateOf(false) }
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

                IconButton(onClick = {
                    if (index > 0) {
                        val updatedItems = items.value.toMutableList()
                        val temp = updatedItems[index]
                        updatedItems[index] = updatedItems[index - 1]
                        updatedItems[index - 1] = temp
                        items.value = updatedItems
                    }
                },
                    enabled = !isAnswerChecked
                    ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move up")
                }

                IconButton(onClick = {
                    if (index < items.value.size - 1) {
                        val updatedItems = items.value.toMutableList()
                        val temp = updatedItems[index]
                        updatedItems[index] = updatedItems[index + 1]
                        updatedItems[index + 1] = temp
                        items.value = updatedItems
                    }
                },
                    enabled = !isAnswerChecked
                    ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move down")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isAnswerChecked) {
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
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("⬅\uFE0F")
            }
            Button(
                onClick = {
                    isCorrect = items.value == question.correctAnswers
                    isAnswerChecked = true
                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswerChecked) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("✅")
            }
            Button(
                onClick = {
                    isAnswerChecked = false
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("➡\uFE0F")
            }
        }
    }
}


@Composable
fun P06(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel) {
    val userAnswers = remember { mutableStateOf(List(question.correctAnswers.size) { "" }) }
    var isAnswerChecked by remember { mutableStateOf(false) }
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
                        if (!isAnswerChecked) {
                            val updatedAnswers = userAnswers.value.toMutableList()
                            updatedAnswers[index] = newAnswer
                            userAnswers.value = updatedAnswers
                        }
                    },
                    modifier = Modifier
                        .background(Color.LightGray)
                        .padding(8.dp),
                    label = { Text("Enter Answer ${index + 1}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isAnswerChecked) {
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
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("⬅\uFE0F")
            }
            Button(
                onClick = {
                    isCorrect = userAnswers.value == question.correctAnswers
                    isAnswerChecked = true
                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswerChecked) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("✅")
            }
            Button(
                onClick = {
                    isAnswerChecked = false
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("➡\uFE0F")
            }
        }
    }
}


@Composable
fun P07(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel) {
    val selectedAssociations = remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var isAnswerChecked by remember { mutableStateOf(false) }
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
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        definitions.forEach { definition ->
                            DropdownMenuItem(
                                onClick = {
                                    if (!isAnswerChecked) {
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

        if (isAnswerChecked) {
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
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("⬅\uFE0F")
            }
            Button(
                onClick = {
                    isCorrect = selectedAssociations.value.toSet() == concepts.zip(question.correctAnswers).toSet()
                    isAnswerChecked = true
                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswerChecked) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("✅")
            }
            Button(
                onClick = {
                    isAnswerChecked = false
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("➡\uFE0F")
            }
        }
    }
}



@Composable
fun P08(question: Question, onNext: () -> Unit, onPrevious: () -> Unit, viewModel: QuizScreenViewModel) {
    val userAnswers = remember { mutableStateOf(List(question.correctAnswers.size) { "" }) }
    var isAnswerChecked by remember { mutableStateOf(false) }
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
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        question.correctAnswers.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    if (!isAnswerChecked) {
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

        if (isAnswerChecked) {
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
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("⬅\uFE0F")
            }
            Button(
                onClick = {
                    isCorrect = userAnswers.value == question.correctAnswers
                    isAnswerChecked = true
                    if (isCorrect) {
                        viewModel.registerCorrectAnswer()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAnswerChecked) {
                        if (isCorrect) Color.Green else Color.Red
                    } else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("✅")
            }
            Button(
                onClick = {
                    isAnswerChecked = false
                    onNext()
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("➡\uFE0F")
            }
        }
    }
}


