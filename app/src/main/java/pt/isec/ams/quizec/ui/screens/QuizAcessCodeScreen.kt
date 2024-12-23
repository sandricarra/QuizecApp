package pt.isec.ams.quizec.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.viewmodel.QuizCreationViewModel

@Composable
fun QuizAccessCodeScreen(quizId: String, viewModel: QuizCreationViewModel = viewModel()) {
    var accessCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isCreator by remember { mutableStateOf(false) }
    var isAccessControlled by remember { mutableStateOf(false) }
    var isAuthorized by remember { mutableStateOf(false) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loadQuizData(quizId, currentUserId) { result ->
            when (result) {
                is QuizResult.Success -> {
                    val quiz = result.quiz
                    isCreator = quiz.creatorId == currentUserId
                    isAccessControlled = quiz.isAccessControlled
                    isAuthorized = !isAccessControlled || quiz.participants.contains(currentUserId)
                    accessCode = quiz.id
                }
                is QuizResult.Failure -> {
                    errorMessage = result.error
                }
            }
            isLoading = false
        }
    }

    QuizAccessCodeContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        accessCode = accessCode,
        isCreator = isCreator,
        isAccessControlled = isAccessControlled,
        isAuthorized = isAuthorized,
        context = context
    )
}

@Composable
fun QuizAccessCodeContent(
    isLoading: Boolean,
    errorMessage: String,
    accessCode: String,
    isCreator: Boolean,
    isAccessControlled: Boolean,
    isAuthorized: Boolean,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Text(text = "Loading...")
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        } else if (isAccessControlled && !isAuthorized) {
            WaitingScreen()
        } else {
            Text(
                text = "Access Code: $accessCode",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { copyToClipboard(context, accessCode) }) {
                Text(text = "Copy Code")
            }
        }
    }
}

sealed class QuizResult {
    data class Success(val quiz: Quiz) : QuizResult()
    data class Failure(val error: String) : QuizResult()
}

fun loadQuizData(quizId: String, userId: String?, onResult: (QuizResult) -> Unit) {
    FirebaseFirestore.getInstance().collection("quizzes").document(quizId)
        .get()
        .addOnSuccessListener { document ->
            val quiz = document.toObject(Quiz::class.java)
            if (quiz != null) {
                onResult(QuizResult.Success(quiz))
            } else {
                onResult(QuizResult.Failure("Quiz not found"))
            }
        }
        .addOnFailureListener {
            onResult(QuizResult.Failure("Error loading quiz"))
        }
}

@Composable
fun WaitingScreen() {
    // Pantalla de espera
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Waiting for approval from the creator...", style = MaterialTheme.typography.titleMedium)
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Quiz Access Code", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
}