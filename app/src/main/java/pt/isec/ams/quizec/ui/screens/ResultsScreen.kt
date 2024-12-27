package pt.isec.ams.quizec.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.ui.viewmodel.ViewResultsP01
import pt.isec.ams.quizec.ui.viewmodel.ViewResultsP02
import pt.isec.ams.quizec.ui.viewmodel.ViewResultsP03
import pt.isec.ams.quizec.ui.viewmodel.ViewResultsP04
import pt.isec.ams.quizec.ui.viewmodel.ViewResultsP05
import pt.isec.ams.quizec.ui.viewmodel.ViewResultsP06

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







