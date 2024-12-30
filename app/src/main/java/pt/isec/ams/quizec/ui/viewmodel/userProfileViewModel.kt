package pt.isec.ams.quizec.ui.viewmodel

import User
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // Estado para almacenar los datos del usuario
    val user = mutableStateOf<User?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // Estado para almacenar los títulos de los cuestionarios
    val quizTitles = mutableStateMapOf<String, String>()

    // Función para cargar los datos del usuario
    fun loadUserProfile(userId: String) {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                // Cargar los datos del usuario
                val userSnapshot = firestore.collection("users").document(userId).get().await()
                if (userSnapshot.exists()) {
                    user.value = userSnapshot.toObject(User::class.java)

                    // Cargar los títulos de los cuestionarios en los que ha participado
                    user.value?.participatedQuizzes?.forEach { quizId ->
                        loadQuizTitle(quizId)
                    }

                    // Cargar los títulos de los cuestionarios completados
                    user.value?.completedQuizzes?.forEach { quizId ->
                        loadQuizTitle(quizId)
                    }
                } else {
                    errorMessage.value = "User not found"
                }
            } catch (e: Exception) {
                errorMessage.value = "Failed to load user profile: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Función para cargar el título de un cuestionario
    fun loadQuizTitle(quizId: String) {
        if (quizTitles.containsKey(quizId)) return // Evitar recargas innecesarias

        viewModelScope.launch {
            try {
                val quizSnapshot = firestore.collection("quizzes").document(quizId).get().await()
                val title = quizSnapshot.getString("title") ?: "Unknown Quiz"
                quizTitles[quizId] = title
            } catch (e: Exception) {
                quizTitles[quizId] = "Failed to load title"
            }
        }
    }



}
