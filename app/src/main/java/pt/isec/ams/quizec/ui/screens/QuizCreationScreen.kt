package pt.isec.ams.quizec.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.firebase.firestore.GeoPoint
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import androidx.compose.ui.res.stringResource
import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.data.models.QuestionType
import pt.isec.ams.quizec.ui.viewmodel.QuizCreationViewModel


@Composable
fun QuizCreationScreen(
    creatorId: String, // ID del creador del cuestionario
    viewModel: QuizCreationViewModel = viewModel() // Usamos el ViewModel para gestionar la lógica de creación
) {
    // Estado mutable para cada campo de entrada y configuraciones del cuestionario
    var title by remember { mutableStateOf(TextFieldValue("")) } // Título del cuestionario
    var description by remember { mutableStateOf(TextFieldValue("")) } // Descripción del cuestionario
    var isLoading by remember { mutableStateOf(false) } // Estado de carga mientras se guarda el cuestionario
    var imageUrl by remember { mutableStateOf<String?>(null) } // URL de la imagen seleccionada
    var imageUri by remember { mutableStateOf<Uri?>(null) } // URI de la imagen seleccionada
    var timeLimit by remember { mutableStateOf<Long?>(null) } // Límite de tiempo del cuestionario
    var isGeolocationRestricted by remember { mutableStateOf(false) } // Restricción por geolocalización
    var creatorLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var isAccessControlled by remember { mutableStateOf(false) } // Control de acceso (cuestionario empieza cuando el creador lo desea)
    var showResultsImmediately by remember { mutableStateOf(false) } // Mostrar resultados inmediatamente después de terminar
    var showAccessCodeScreen by remember { mutableStateOf(false) } // Controla la visibilidad de la pantalla de código
    var savedQuizId by remember { mutableStateOf<String?>(null) } // ID del cuestionario guardado

    val context = LocalContext.current
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

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

    // Lista de opciones posibles para completar los espacios en blanco
    var optionsP06 by remember { mutableStateOf(listOf("sun", "moon", "star")) }

    // Respuestas correctas asociadas a los espacios en blanco
    var correctAnswersP06 by remember { mutableStateOf(listOf("sun")) }

    var associationsP07 = remember { mutableStateListOf<Pair<String, String>>() }
    val imageUrlP07 by remember { mutableStateOf<String?>(null) }

    var correctAnswersP08 by remember { mutableStateOf(listOf("sun")) }

    var optionsP08 by remember { mutableStateOf(listOf("sun", "moon", "star")) }

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

    // Función para obtener la ubicación
    fun requestLocation(
        context: Context,
        fusedLocationProviderClient: FusedLocationProviderClient,
        onLocationRetrieved: (GeoPoint?) -> Unit
    ) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationRetrieved(GeoPoint(location.latitude, location.longitude))
                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación.", Toast.LENGTH_LONG).show()
                    onLocationRetrieved(null)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al obtener la ubicación.", Toast.LENGTH_LONG).show()
                onLocationRetrieved(null)
            }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                requestLocation(
                    context,
                    fusedLocationProviderClient
                ) { location ->
                    creatorLocation = location
                }
            } else {
                Toast.makeText(
                    context,
                    "Ubicación no permitida. No se puede habilitar la restricción por geolocalización.",
                    Toast.LENGTH_LONG
                ).show()
                isGeolocationRestricted = false
            }
        }
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
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
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
                        contentDescription = stringResource(R.string.no_image_selected),
                        tint = Color.Gray,
                        modifier = Modifier.size(100.dp) // Tamaño del ícono
                    )
                    Text(
                        text = stringResource(R.string.no_image_selected),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Título principal del cuestionario
        item {
            Text(stringResource(R.string.create_quiz), style = MaterialTheme.typography.headlineMedium)
        }

        // Campo para el título del cuestionario
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.quiz_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Campo para la descripción del cuestionario
        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )
        }

        // Campo para el límite de tiempo del cuestionario
        item {
            OutlinedTextField(
                value = timeLimit?.toString() ?: "",
                onValueChange = {
                    // Validar que el valor ingresado sea un número
                    timeLimit = it.toLongOrNull() // Si no es un número, será null
                },
                label = { Text(stringResource(R.string.time_limit)) },
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
                    text = stringResource(R.string.restrict_by_geolocation),
                    modifier = Modifier.weight(1f)
                )
                // Al cambiar el switch de restricción geográfica, obtener la ubicación
                Switch(
                    checked = isGeolocationRestricted,
                    onCheckedChange = { isChecked ->
                        isGeolocationRestricted = isChecked
                        if (isChecked) {
                            val permissionStatus = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                requestLocation(
                                    context,
                                    fusedLocationProviderClient
                                ) { location ->
                                    creatorLocation = location
                                }
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        } else {
                            requestLocation(
                                context,
                                fusedLocationProviderClient
                            ) { location ->
                                creatorLocation = location
                            }
                        }
                    },

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
                    text = stringResource(R.string.access_controlled),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isAccessControlled,
                    onCheckedChange = { isAccessControlled = it },
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
                    text = stringResource(R.string.show_results_immediately),
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
                Text(stringResource(R.string.select_image))
            }
        }

        // Botón y menú desplegable para seleccionar el tipo de pregunta
        item {
            var selectedQuestionTypeText by remember { mutableStateOf("Selecione o tipo de pergunta") }

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
                    )
                }


                QuestionType.P06 -> {
                    P06Question(
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

                QuestionType.P08 -> {
                    P08Question(
                        questionTitle = questionTitle,
                        onTitleChange = { questionTitle = it },
                        answers = correctAnswersP08,
                        onAnswersChange = { correctAnswersP08 = it },
                        imageUrl = imageUrl,
                        onImageChange = { imageUrl = it },
                        onOptionsChange = { optionsP08 = it }
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
                        options = optionsP02,
                        correctAnswers = listOf(selectedAnswerP02 ?: ""),
                        imageUrl = imageUrl,
                        onUpdate = onUpdate,
                        viewModel = viewModel
                    )
                }

                QuestionType.P03 -> {
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle,
                        options = optionsP03,
                        correctAnswers = selectedAnswerP03,
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
                        imageUrl = imageUrl, // Las preguntas de completar no requieren imagen (puedes cambiar esto)
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


                QuestionType.P08 -> {
                    AddQuestionButton(
                        questionType = questionType,
                        questionTitle = questionTitle,
                        options = optionsP08,

                        correctAnswers = correctAnswersP08,
                        imageUrl = imageUrl,
                        onUpdate = onUpdate,
                        viewModel = viewModel
                    )
                }
                else -> {}
            }
        }


        // Botón para guardar el cuestionario
        item {

            // Mostrar el código del cuestionario si ya ha sido guardado
            if (showAccessCodeScreen && savedQuizId != null) {
                Text(
                    text = stringResource(R.string.quiz_access_code) + " $savedQuizId",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el código y el botón
            }

            Button(
                onClick = {
                    isLoading = true
                    if (title.text.isNotBlank() &&
                        viewModel.questions.isNotEmpty()) {
                    viewModel.saveQuiz(
                        title = title.text,
                        description = description.text,
                        imageUrl = imageUri?.toString(),
                        timeLimit = timeLimit?.toInt(),
                        creatorId = creatorId,
                        isGeolocationRestricted = isGeolocationRestricted,
                        location = creatorLocation,
                        isAccessControlled = isAccessControlled,
                        showResultsImmediately = showResultsImmediately,

                        onSuccess = { id ->
                            isLoading = false
                            showAccessCodeScreen = true // Muestra la pantalla del código de acceso
                            savedQuizId = id
                        },

                        onError = {
                            isLoading = false
                        }
                    )
                }
                    else {
                        isLoading = false // Si falla la validación, asegurarse de desactivar el loading
                    }
                          },
                enabled = !isLoading &&
                        title.toString().isNotBlank() &&
                        viewModel.questions.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.save_quiz))
                }
            }
        }


        // Agregar preguntas añadidas como otro ítem en el LazyColumn
        item {
            if (viewModel.questions.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.added_questions),
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
                                    text = stringResource(R.string.title) + " ${question.title}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                // Tipo de pregunta
                                Text(
                                    text = stringResource(R.string.type) + " ${question.type}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                // Respuestas correctas
                                Text(
                                    text = stringResource(R.string.correct_answers) + " ${question.correctAnswers}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.no_questions_added),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
















