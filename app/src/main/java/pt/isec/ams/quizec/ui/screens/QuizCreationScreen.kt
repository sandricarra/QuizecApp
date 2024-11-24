
package pt.isec.ams.quizec.ui.screens


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
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.viewmodel.QuizCreationState
import pt.isec.ams.quizec.viewmodel.QuizCreationViewModel




import android.util.Log

import androidx.compose.foundation.border

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material3.AlertDialog

import androidx.compose.material3.Checkbox

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.RadioButton

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

import pt.isec.ams.quizec.data.models.Question


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
    var showTypeSelectorDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<QuestionType?>(null) }


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



        if (showTypeSelectorDialog) {
            QuestionTypeSelectorDialog(
                onDismiss = { showTypeSelectorDialog = false },
                onTypeSelected = { type ->
                    selectedType = type
                    showTypeSelectorDialog = false
                }
            )
        }

        selectedType?.let { type ->
            when (type) {
                QuestionType.P01 -> TrueFalseQuestionInput(
                    onSave = { question ->
                        questions = questions + question
                        selectedType = null
                    },
                    onCancel = { selectedType = null }
                )
                QuestionType.P02 -> MultipleChoiceSingleQuestionInput(
                    onSave = { question ->
                        questions = questions + question
                        selectedType = null
                    },
                    onCancel = { selectedType = null }
                )
                QuestionType.P02 -> MultipleChoiceSingleQuestionInput(
                    onSave = { question ->
                        questions = questions + question
                        selectedType = null
                    },
                    onCancel = { selectedType = null }
                )
                QuestionType.P03 -> MultipleChoiceMultipleAnswersInput(
                    onSave = { question ->
                        questions = questions + question
                        selectedType = null
                    },
                    onCancel = { selectedType = null }
                )
                QuestionType.P04 -> MatchingQuestionInput(
                    onSave = { question ->
                        questions = questions + question
                        selectedType = null
                    },
                    onCancel = { selectedType = null }
                )
                QuestionType.P05 -> OrderingQuestionInput(
                    onSave = { question ->
                        questions = questions + question
                        selectedType = null
                    },
                    onCancel = { selectedType = null }
                )
                // Implementa más casos para otros tipos de preguntas
                else -> {
                    // Ejemplo para True/False
                    questions = questions + Question(
                        questionText = "¿Es esta una afirmación correcta?",
                        questionType = QuestionType.P01,
                        options = listOf("Sí", "No"),
                        correctAnswers = listOf(0)
                    )
                    selectedType = null
                }
            }
        }

        Button(
            onClick = { showTypeSelectorDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Pregunta")
        }

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

@Composable
fun QuestionTypeSelectorDialog(
    onDismiss: () -> Unit,
    onTypeSelected: (QuestionType) -> Unit
) {
    val questionTypes = QuestionType.values()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Tipo de Pregunta") },
        text = {
            Column {
                questionTypes.forEach { type ->
                    Button(
                        onClick = {
                            onTypeSelected(type)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(type.label)
                    }
                }
            }
        },
        confirmButton = {}
    )
}
@Composable
fun MultipleChoiceSingleQuestionInput(
    onSave: (Question) -> Unit,
    onCancel: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "", "")) }
    var correctAnswerIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Input para el texto de la pregunta
        TextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Texto de la Pregunta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Inputs para las opciones
        options.forEachIndexed { index, option ->
            OutlinedTextField(
                value = option,
                onValueChange = { newValue ->
                    options = options.toMutableList().apply { this[index] = newValue }
                },
                label = { Text("Opción ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Botón para agregar más opciones
        Button(onClick = { options = options + "" }) {
            Text("Agregar Opción")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de respuesta correcta
        Text("Selecciona la respuesta correcta:")
        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = correctAnswerIndex == index,
                    onClick = { correctAnswerIndex = index }
                )
                Text(option)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones para guardar o cancelar
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    if (questionText.isNotBlank() && correctAnswerIndex != null) {
                        val question = Question(
                            questionText = questionText,
                            questionType = QuestionType.P02,
                            options = options,
                            correctAnswers = listOf(correctAnswerIndex!!)
                        )
                        onSave(question)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}
@Composable
fun TrueFalseQuestionInput(
    onSave: (Question) -> Unit,
    onCancel: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Campo para ingresar el texto de la pregunta
        TextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Texto de la Pregunta (Sí/No)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de respuesta correcta
        Text("Selecciona la respuesta correcta:")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Botón para seleccionar "Sí"
            Button(onClick = { correctAnswer = true }) {
                Text("Sí")
            }
            // Botón para seleccionar "No"
            Button(onClick = { correctAnswer = false }) {
                Text("No")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones para guardar o cancelar
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    if (questionText.isNotBlank() && correctAnswer != null) {
                        val question = Question(
                            questionText = questionText,
                            questionType = QuestionType.P01,
                            options = listOf("Sí", "No"),
                            correctAnswers = listOf(if (correctAnswer == true) 0 else 1)
                        )
                        onSave(question)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun MultipleChoiceMultipleAnswersInput(
    onSave: (Question) -> Unit,
    onCancel: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var options by remember { mutableStateOf<List<String>>(listOf("", "", "")) } // Lista de opciones inicial
    var correctAnswers by remember { mutableStateOf<Set<Int>>(emptySet()) } // Índices de respuestas correctas

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Campo para ingresar el texto de la pregunta
        TextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Texto de la Pregunta (Opción Múltiple)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campos para opciones
        Text("Opciones (2-6):")
        options.forEachIndexed { index, option ->
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = option,
                    onValueChange = { newOption ->
                        options = options.toMutableList().apply { set(index, newOption) }
                    },
                    label = { Text("Opción ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = correctAnswers.contains(index),
                    onCheckedChange = { isChecked ->
                        correctAnswers = if (isChecked) {
                            correctAnswers + index
                        } else {
                            correctAnswers - index
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para agregar más opciones
        Button(
            onClick = {
                if (options.size < 6) {
                    options = options + ""
                }
            },
            enabled = options.size < 6
        ) {
            Text("Agregar Opción")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones para guardar o cancelar
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    if (questionText.isNotBlank() &&
                        options.size in 2..6 &&
                        correctAnswers.isNotEmpty()
                    ) {
                        val question = Question(
                            questionText = questionText,
                            questionType = QuestionType.P03,
                            options = options,
                            correctAnswers = correctAnswers.toList()
                        )
                        onSave(question)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}


@Composable
fun MatchingQuestionInput(
    onSave: (Question) -> Unit,
    onCancel: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var columnA by remember { mutableStateOf<List<String>>(listOf("", "")) } // Inicializamos con dos elementos
    var columnB by remember { mutableStateOf<List<String>>(listOf("", "")) }
    var matches by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) } // Emparejamientos seleccionados

    LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Campo para ingresar el texto de la pregunta
        item {
            TextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Texto de la Pregunta (Emparejamiento)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Columnas de opciones
        item {
            Text("Columna A (2-6 elementos):")
            columnA.forEachIndexed { index, value ->
                TextField(
                    value = value,
                    onValueChange = { newValue ->
                        columnA = columnA.toMutableList().apply { set(index, newValue) }
                    },
                    label = { Text("Elemento ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(onClick = { if (columnA.size < 6) columnA = columnA + "" }) {
                Text("Agregar a Columna A")
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text("Columna B (2-6 elementos):")
            columnB.forEachIndexed { index, value ->
                TextField(
                    value = value,
                    onValueChange = { newValue ->
                        columnB = columnB.toMutableList().apply { set(index, newValue) }
                    },
                    label = { Text("Elemento ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(onClick = { if (columnB.size < 6) columnB = columnB + "" }) {
                Text("Agregar a Columna B")
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Seleccionar emparejamientos
            Text("Emparejamientos:")
            columnA.forEachIndexed { indexA,itemA ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(columnA[indexA], modifier = Modifier.weight(1f))
                    Text("→")
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        columnB.forEachIndexed { indexB, itemB ->
                            DropdownMenuItem(
                                onClick = {
                                    // Asegúrate de agregar ambos índices a los emparejamientos
                                    matches = matches + (indexA to indexB)
                                },
                                text = { Text(itemB) }
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Botones de acción
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        val areColumnsValid = columnA.all { it.isNotBlank() } && columnB.all { it.isNotBlank() }
                        val areMatchesValid = matches.isNotEmpty() && matches.all {
                            it.first in columnA.indices && it.second in columnB.indices
                        }

                        if (questionText.isNotBlank() &&
                            columnA.size in 2..6 &&
                            columnB.size in 2..6 &&
                            areColumnsValid &&
                            areMatchesValid
                        ) {
                            val question = Question(
                                questionText = questionText,
                                questionType = QuestionType.P04,
                                options = columnA + columnB,
                                correctAnswers = matches.map { it.first * columnB.size + it.second }
                            )
                            onSave(question)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}





@Composable
fun OrderingQuestionInput(
    onSave: (Question) -> Unit,
    onCancel: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var options by remember { mutableStateOf<List<String>>(listOf("", "")) } // Inicializamos con dos opciones
    var correctOrder by remember { mutableStateOf<List<Int>>(emptyList()) } // Orden correcto

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Campo para el texto de la pregunta
        TextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Texto de la Pregunta (Ordenación)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opciones de ordenación
        Text("Opciones (2-6 elementos):")
        options.forEachIndexed { index, value ->
            TextField(
                value = value,
                onValueChange = { newValue ->
                    options = options.toMutableList().apply { set(index, newValue) }
                },
                label = { Text("Opción ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Button(onClick = { if (options.size < 6) options = options + "" }) {
            Text("Agregar Opción")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Seleccionar orden correcto
        Text("Orden Correcto:")
        LazyColumn {
            items(options.size) { index ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(options[index], modifier = Modifier.weight(1f))
                    Text("Posición:")
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        (1..options.size).forEach { position ->
                            DropdownMenuItem(
                                onClick = {
                                    if (!correctOrder.contains(position - 1)) {
                                        correctOrder = correctOrder + (position - 1)
                                    }
                                },
                                text = { Text("$position") }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    if (questionText.isNotBlank() &&
                        options.size in 2..6 &&
                        correctOrder.size == options.size
                    ) {
                        val question = Question(
                            questionText = questionText,
                            questionType = QuestionType.P05,
                            options = options,
                            correctAnswers = correctOrder
                        )
                        onSave(question)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}






