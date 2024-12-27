package pt.isec.ams.quizec.ui.screens

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ResultsScreen(questionId: String) {
    val db = FirebaseFirestore.getInstance()
    var questionType by remember { mutableStateOf<String?>(null) }
    var questionTitle by remember { mutableStateOf<String>("") }
    var questionOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar el tipo de pregunta, título y opciones desde Firebase
    LaunchedEffect(questionId) {
        db.collection("responses")
            .whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { documents ->
                val firstResponse = documents.firstOrNull()
                questionType = firstResponse?.get("questionType") as? String

                val questionDocId = firstResponse?.get("questionId") as? String
                if (questionDocId != null) {
                    db.collection("questions")
                        .document(questionDocId)
                        .get()
                        .addOnSuccessListener { questionDocument ->
                            questionTitle = questionDocument.getString("title") ?: "Unknown Question"
                            questionOptions = questionDocument.get("options") as? List<String> ?: emptyList()
                            isLoading = false
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error fetching question title", e)
                            isLoading = false
                        }
                } else {
                    isLoading = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching question type", e)
                isLoading = false
            }
    }

    // Mostrar UI según el estado de carga y tipo de pregunta
    when {
        isLoading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        questionType == null -> {
            Text(
                text = "Error: Question type not found",
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.error
            )
        }
        else -> {
            when (questionType) {
                "P01" -> ViewResultsP01(questionId, questionTitle)
                "P02" -> ViewResultsP02(questionId, questionTitle, questionOptions)
                "P03" -> ViewResultsP03(questionId, questionTitle, questionOptions)
                "P04" -> ViewResultsP04(questionId, questionTitle)
                "P05" -> ViewResultsP05(questionId, questionTitle)
                "P06" -> ViewResultsP06(questionId, questionTitle)
                "P07" -> ViewResultsP04(questionId, questionTitle)
                "P08" -> ViewResultsP06(questionId, questionTitle)
                else -> Text(
                    text = "Unsupported question type: $questionType",
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ViewResultsP01(questionId: String, questionTitle: String) {
    val db = FirebaseFirestore.getInstance()
    var trueCount by remember { mutableStateOf(0) }
    var falseCount by remember { mutableStateOf(0) }
    var correctAnswer by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar datos de Firebase
    LaunchedEffect(questionId) {
        db.collection("responses")
            .whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { documents ->
                val responses = documents.mapNotNull { it.data }
                trueCount = responses.count { it["selectedAnswer"] == "True" }
                falseCount = responses.count { it["selectedAnswer"] == "False" }

                // Suponiendo que hay solo una respuesta correcta en P01
                correctAnswer = documents.firstOrNull()?.get("correctAnswer") as? String

                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching results", e)
                isLoading = false
            }
    }

    // UI para mostrar resultados
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Results for $questionTitle",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Colores de las barras dependiendo de la respuesta correcta
            val colors = listOf(
                if (correctAnswer == "True") Color(0xFFBEFF99) else Color(0xFFBFDEFF),  // "True" correcta
                if (correctAnswer == "False") Color(0xFFBEFF99) else Color(0xFFBFDEFF)  // "False" correcta
            )

            // Mostrar gráfico de barras (arriba)
            BarChart(
                data = listOf(trueCount, falseCount),
                labels = listOf("True", "False"),
                colors = colors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)  // Limitar el tamaño de la barra
            )

            Spacer(modifier = Modifier.height(16.dp))  // Espacio entre gráficos

            // Mostrar gráfico circular (debajo)
            PieChart(
                data = listOf(trueCount, falseCount),
                labels = listOf("True", "False"),
                colors = colors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)  // Limitar el tamaño del gráfico circular
            )
        }
    }
}

@Composable
fun ViewResultsP02(questionId: String, questionTitle: String, questionOptions: List<String>) {
    val db = FirebaseFirestore.getInstance()
    var results by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var correctAnswers by remember { mutableStateOf<List<String>>(emptyList()) }

    // Cargar datos de Firebase
    LaunchedEffect(questionId) {
        db.collection("questions")
            .document(questionId)
            .get()
            .addOnSuccessListener { questionDocument ->
                correctAnswers = questionDocument.get("correctAnswers") as? List<String> ?: emptyList()
                val options = questionDocument.get("options") as? List<String> ?: emptyList()

                db.collection("responses")
                    .whereEqualTo("questionId", questionId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val responses = documents.mapNotNull { it.data["selectedAnswer"] as? String }

                        val resultCounts = questionOptions.associateWith { option -> responses.count { it == option } }
                        results = resultCounts
                        isLoading = false
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Error fetching responses", e)
                        isLoading = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching question data", e)
                isLoading = false
            }
    }

    // UI para mostrar resultados
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Results for $questionTitle",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar gráfico de barras (arriba)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BarChart(
                    data = questionOptions.map { results[it] ?: 0 },
                    labels = questionOptions,
                    colors = questionOptions.mapIndexed { index, option ->
                        if (correctAnswers.contains(option)) {
                            Color(0xFFBEFF99)
                        } else {
                            Color(0xFFBFDEFF)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(300.dp)  // Limitar el tamaño del gráfico de barras
                )
            }

            Spacer(modifier = Modifier.height(16.dp))  // Espacio entre gráficos

            // Mostrar gráfico circular (debajo)
            PieChart(
                data = questionOptions.map { results[it] ?: 0 },
                labels = questionOptions,
                colors = questionOptions.mapIndexed { index, option ->
                    if (correctAnswers.contains(option)) {
                        Color(0xFFBEFF99)
                    } else {
                        Color(0xFFBFDEFF)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)  // Limitar el tamaño del gráfico circular
            )
        }
    }
}

@Composable
fun ViewResultsP03(questionId: String, questionTitle: String, questionOptions: List<String>) {
    val db = FirebaseFirestore.getInstance()
    var results by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var correctAnswers by remember { mutableStateOf<List<String>>(emptyList()) }

    // Cargar datos de Firebase
    LaunchedEffect(questionId) {
        db.collection("responses")
            .whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { documents ->
                val responses = documents.flatMap {
                    // Aquí se asume que "selectedAnswers" es una lista de respuestas
                    (it.data["selectedAnswers"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                }
                correctAnswers = documents.mapNotNull { it.data["correctAnswers"] as? List<String> }.flatten()

                // Contar cuántas veces se seleccionó cada opción
                val resultCounts = questionOptions.associateWith { option -> responses.count { it == option } }
                results = resultCounts
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching results", e)
                isLoading = false
            }
    }

    // UI para mostrar resultados
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Results for $questionTitle",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar gráfico de barras (arriba)
            BarChart(
                data = questionOptions.map { results[it] ?: 0 },
                labels = questionOptions,
                colors = questionOptions.mapIndexed { index, option ->
                    if (correctAnswers.contains(option)) {
                        Color(0xFFBEFF99) // Verde claro para respuestas correctas
                    } else {
                        Color(0xFFBFDEFF) // Azul claro para respuestas incorrectas
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)  // Limitar el tamaño del gráfico de barras
            )

            Spacer(modifier = Modifier.height(16.dp))  // Espacio entre gráficos

            // Mostrar gráfico circular (debajo)
            PieChart(
                data = questionOptions.map { results[it] ?: 0 },
                labels = questionOptions,
                colors = questionOptions.mapIndexed { index, option ->
                    if (correctAnswers.contains(option)) {
                        Color(0xFFBEFF99) // Verde claro para respuestas correctas
                    } else {
                        Color(0xFFBFDEFF) // Azul claro para respuestas incorrectas
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)  // Limitar el tamaño del gráfico circular
            )
        }
    }
}

@Composable
fun ViewResultsP04(questionId: String, questionTitle: String) {
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(true) }
    var selectedAnswers by remember { mutableStateOf<List<String>>(emptyList()) }
    var correctAnswers by remember { mutableStateOf<List<String>>(emptyList()) }

    // Cargar los datos de Firebase
    LaunchedEffect(questionId) {
        db.collection("responses")
            .whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { documents ->
                // Obtener las respuestas seleccionadas por los usuarios
                selectedAnswers = documents.flatMap {
                    val selectedPairs = it.data["selectedAnswers"] as? List<Map<String, String>> ?: emptyList()
                    selectedPairs.map { pair ->
                        val leftOption = pair["first"] ?: ""
                        val rightOption = pair["second"] ?: ""
                        "$leftOption ➔ $rightOption"
                    }
                }
                // Obtener las respuestas correctas
                correctAnswers = documents.flatMap {
                    val correctPairs = it.data["correctAnswers"] as? List<Map<String, String>> ?: emptyList()
                    correctPairs.map { pair ->
                        val leftOption = pair["first"] ?: ""
                        val rightOption = pair["second"] ?: ""
                        "$leftOption ➔ $rightOption"
                    }
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching results", e)
                isLoading = false
            }
    }

    // UI para mostrar los resultados
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Results for $questionTitle",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contar cuántas veces se seleccionó cada combinación
            val allOptionCounts = selectedAnswers.groupingBy { it }.eachCount()

            // Ordenar las combinaciones por la cantidad de selecciones de forma descendente
            val sortedOptionCounts = allOptionCounts.entries.sortedByDescending { it.value }

            // Mostrar todas las combinaciones posibles y cuántas veces fueron seleccionadas
            sortedOptionCounts.forEach { (option, count) ->
                val isCorrect = option in correctAnswers
                val icon = if (isCorrect) "✔️" else "❌"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(if (isCorrect) Color(0xFFDFF0D8) else Color(0xFFF2DEDE))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "$icon $option: $count",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
@Composable
fun ViewResultsP05(questionId: String, questionTitle: String) {
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(true) }
    var selectedAnswers by remember { mutableStateOf<List<List<String>>>(emptyList()) }
    var correctAnswers by remember { mutableStateOf<List<String>>(emptyList()) }

    // Cargar los datos de Firebase
    LaunchedEffect(questionId) {
        db.collection("responses")
            .whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { documents ->
                val allSelectedAnswers = mutableListOf<List<String>>()
                documents.forEach { document ->
                    val answers = document.get("selectedAnswers") as? List<String> ?: emptyList()
                    allSelectedAnswers.add(answers)
                }
                selectedAnswers = allSelectedAnswers
                correctAnswers = documents.firstOrNull()?.get("correctAnswers") as? List<String> ?: emptyList()
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching results", e)
                isLoading = false
            }
    }

    // UI para mostrar los resultados
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Results for $questionTitle",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar las respuestas seleccionadas y correctas
            correctAnswers.forEachIndexed { index, correctAnswer ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    selectedAnswers.forEach { answers ->
                        val answer = answers.getOrNull(index) ?: ""
                        val isCorrectAnswer = correctAnswer == answer
                        val icon = if (isCorrectAnswer) "✔️" else "❌"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(if (isCorrectAnswer) Color(0xFFDFF0D8) else Color(0xFFF2DEDE))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "$icon $answer",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ViewResultsP06(questionId: String, questionTitle: String) {
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(true) }
    var selectedAnswers by remember { mutableStateOf<List<String>>(emptyList()) }
    var correctAnswers by remember { mutableStateOf<List<String>>(emptyList()) }

    // Cargar los datos de Firebase
    LaunchedEffect(questionId) {
        db.collection("responses")
            .whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { documents ->
                // Obtener las respuestas seleccionadas por los usuarios
                selectedAnswers = documents.flatMap {
                    it.data["selectedAnswers"] as? List<String> ?: emptyList()
                }
                // Obtener las respuestas correctas
                correctAnswers = documents.firstOrNull()?.get("correctAnswers") as? List<String> ?: emptyList()
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching results", e)
                isLoading = false
            }
    }

    // UI para mostrar los resultados
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Results for $questionTitle",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contar cuántas veces se seleccionó cada opción
            val allOptionCounts = selectedAnswers.groupingBy { it }.eachCount()

            // Ordenar las opciones por la cantidad de selecciones de forma descendente
            val sortedOptionCounts = allOptionCounts.entries.sortedByDescending { it.value }

            // Mostrar todas las opciones posibles y cuántas veces fueron seleccionadas
            sortedOptionCounts.forEach { (option, count) ->
                val isCorrect = option in correctAnswers
                val icon = if (isCorrect) "✔️" else "❌"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(if (isCorrect) Color(0xFFDFF0D8) else Color(0xFFF2DEDE))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "$icon $option: $count",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}


@Composable
fun BarChart(
    data: List<Int>,
    labels: List<String>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val maxDataValue = data.maxOrNull() ?: 1
    val barWidthFraction = 0.6f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(400.dp)) {
            val barSpacing = size.width / data.size
            val barWidth = size.width * barWidthFraction / data.size

            val stepY = maxDataValue / 5f
            val yStepHeight = size.height / (stepY * 5)

            // Dibujar las líneas horizontales
            for (i in 0..5) {
                val y = size.height - i * yStepHeight
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    (i * stepY).toInt().toString(),
                    10f,
                    y,
                    Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 28f
                        textAlign = Paint.Align.LEFT
                    }
                )
            }

            // Dibujar las barras
            data.forEachIndexed { index, value ->
                val barHeight = size.height * (value.toFloat() / maxDataValue)
                val barX = barSpacing * index + barSpacing / 2 - barWidth / 2
                val barY = size.height - barHeight

                drawRoundRect(
                    color = colors[index],
                    topLeft = Offset(barX, barY),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    value.toString(),
                    barX + barWidth / 2,
                    barY - 8f,
                    Paint().apply {
                        textSize = 32f
                        textAlign = Paint.Align.CENTER
                        color = android.graphics.Color.BLACK
                    }
                )
            }

            // Etiquetas de las barras
            labels.forEachIndexed { index, label ->
                val labelX = barSpacing * index + barSpacing / 2
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    labelX,
                    size.height + 36f,
                    Paint().apply {
                        textSize = 36f
                        textAlign = Paint.Align.CENTER
                        color = android.graphics.Color.BLACK
                    }
                )
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<Int>,
    labels: List<String>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val size = 300f
    val radius = size / 2f
    var startAngle = 0f
    val total = data.sum()

    Canvas(modifier = modifier.size(size.dp)) {
        data.forEachIndexed { index, value ->
            val sweepAngle = if (total > 0) 360f * (value.toFloat() / total) else 0f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(size / 2 - radius, size / 2 - radius),
                size = Size(size, size)
            )
            startAngle += sweepAngle
        }

        startAngle = 0f
        data.forEachIndexed { index, value ->
            val sweepAngle = if (total > 0) 360f * (value.toFloat() / total) else 0f
            val middleAngle = startAngle + sweepAngle / 2f
            val labelX = size / 2 + radius / 2 * cos(Math.toRadians(middleAngle.toDouble())).toFloat()
            val labelY = size / 2 + radius / 2 * sin(Math.toRadians(middleAngle.toDouble())).toFloat()

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    labels[index],
                    labelX,
                    labelY,
                    Paint().apply {
                        textSize = 20f
                        color = android.graphics.Color.BLACK
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
            startAngle += sweepAngle
        }
    }
}







