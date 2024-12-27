package pt.isec.ams.quizec.ui.screens

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ResultsScreen(questionId: String) {
    val db = FirebaseFirestore.getInstance()
    var questionType by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar el tipo de pregunta desde Firebase
    LaunchedEffect(questionId) {
        db.collection("responses") // Asegúrate de que esta colección es correcta
            .whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { documents ->
                // Asumimos que al menos una respuesta tiene el tipo de pregunta
                val firstResponse = documents.firstOrNull()
                questionType = firstResponse?.getString("questionType")
                isLoading = false
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
                "P01" -> ViewResultsP01(questionId)
                // Añadir más casos según sea necesario
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
fun ViewResultsP01(questionId: String) {
    val db = FirebaseFirestore.getInstance()
    var trueCount by remember { mutableStateOf(0) }
    var falseCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar datos de Firebase
    LaunchedEffect(questionId) {
        db.collection("responses")
            .whereEqualTo("questionId", questionId)
            .get()
            .addOnSuccessListener { documents ->
                val responses = documents.toObjects(Response::class.java)
                trueCount = responses.count { it.selectedAnswer == "True" }
                falseCount = responses.count { it.selectedAnswer == "False" }
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Results for Question $questionId",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar gráfico de barras
            BarChart(
                data = listOf(trueCount, falseCount),
                labels = listOf("True", "False"),
                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// Modelo de respuesta
data class Response(
    val questionId: String = "",
    val questionType: String = "",
    val selectedAnswer: String = "",
    val correctAnswer: String = "",
    val isCorrect: Boolean = false,
    val timestamp: Long = 0L
)

// Componente gráfico simple para el gráfico de barras
@Composable
fun BarChart(
    data: List<Int>,
    labels: List<String>,
    colors: List<androidx.compose.ui.graphics.Color>,
    modifier: Modifier = Modifier
) {
    // Configuración de dimensiones y espaciado
    val maxDataValue = data.maxOrNull() ?: 1
    val barWidthFraction = 0.2f // Fracción del ancho total para cada barra

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(400.dp)) {
            val barSpacing = size.width / data.size
            val barWidth = size.width * barWidthFraction / data.size

            // Dibujar eje Y con valores
            val stepY = maxDataValue / 5f
            val yStepHeight = size.height / (stepY * 5)

            for (i in 0..5) {
                val y = size.height - i * yStepHeight
                drawLine(
                    color = androidx.compose.ui.graphics.Color.Gray,
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

                drawRect(
                    color = colors[index],
                    topLeft = Offset(barX, barY),
                    size = Size(barWidth, barHeight)
                )

                // Etiqueta encima de la barra
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

            // Etiquetas debajo de las barras
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


