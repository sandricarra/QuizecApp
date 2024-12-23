package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.isec.ams.quizec.data.models.Quiz
import pt.isec.ams.quizec.data.models.QuizStatus

class ManageQuizViewModel : ViewModel() {

    // Estado para manejar el mensaje de retroalimentación
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Estado para manejar el nuevo estado de los cuestionarios
    private val _newStatus = MutableStateFlow<QuizStatus?>(null)
    val newStatus: StateFlow<QuizStatus?> = _newStatus

    // Estado para manejar la lista de cuestionarios
    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes

    // Función para cargar los cuestionarios del creatorId
    fun loadQuizzesByCreatorId(creatorId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("quizzes")
            .whereEqualTo("creatorId", creatorId)
            .get()
            .addOnSuccessListener { result ->
                val quizList = result.documents.mapNotNull { document ->
                    val quiz = document.toObject(Quiz::class.java)
                    if (quiz != null) {
                        println("Quiz: ${quiz.title}, isAccessControlled: ${quiz.isAccessControlled}")
                    }
                    quiz
                }
                _quizzes.value = quizList
            }
            .addOnFailureListener { exception ->
                _message.value = "Failed to load quizzes: ${exception.message}"
            }
    }


    // Función para alternar el estado de todos los cuestionarios
    fun toggleAllQuizzesStatus(creatorId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            try {
                val querySnapshot = db.collection("quizzes")
                    .whereEqualTo("creatorId", creatorId)
                    .get()
                    .await() // Esperar a que se complete la consulta

                val batch = db.batch()
                var newStatus: QuizStatus? = null

                querySnapshot.documents.forEach { document ->
                    val currentStatus = document.getString("status")?.let {
                        QuizStatus.valueOf(it)
                    } ?: QuizStatus.AVAILABLE

                    val accessControlled = document.getBoolean("accessControlled") ?: false

                    if (accessControlled) {
                        val tempNewStatus = if (currentStatus == QuizStatus.AVAILABLE) QuizStatus.LOCKED else QuizStatus.AVAILABLE

                        if (newStatus == null) {
                            newStatus = tempNewStatus
                        }

                        batch.update(document.reference, "status", tempNewStatus.name)
                    }
                }

                batch.commit().await() // Esperar a que se complete el batch

                _message.value = "All quizzes updated successfully!"
                _newStatus.value = newStatus

            } catch (e: Exception) {
                _message.value = "Failed to update quiz status: ${e.message}"
            }
        }
    }

    // Función para obtener los participantes
    fun getParticipants(creatorId: String, onParticipantsUpdate: (List<String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("quizParticipation")
            .whereEqualTo("creatorId", creatorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _message.value = "Failed to fetch participants: ${error.message}"
                    return@addSnapshotListener
                }

                val participants = snapshot?.documents?.mapNotNull { it.getString("userName") } ?: emptyList()
                onParticipantsUpdate(participants)
            }
    }

    fun toggleQuizStatus(quizId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            try {
                // Obtener referencia al cuestionario por su ID
                val quizRef = db.collection("quizzes").document(quizId)
                val quizSnapshot = quizRef.get().await() // Obtener datos del cuestionario

                if (!quizSnapshot.exists()) {
                    _message.value = "Quiz not found."
                    return@launch
                }

                // Obtener el estado actual y el flag de control de acceso
                val currentStatus = quizSnapshot.getString("status")?.let {
                    QuizStatus.valueOf(it)
                } ?: QuizStatus.AVAILABLE

                val isAccessControlled = quizSnapshot.getBoolean("accessControlled") ?: false

                if (!isAccessControlled) {
                    _message.value = "This quiz does not have access control enabled."
                    return@launch
                }

                // Alternar el estado del cuestionario
                val newStatus = if (currentStatus == QuizStatus.AVAILABLE) QuizStatus.LOCKED else QuizStatus.AVAILABLE

                // Actualizar el estado en Firestore
                quizRef.update("status", newStatus.name).await() // Esperar la actualización

                // Actualizar el estado local
                _message.value = "Quiz status updated successfully!"
                _newStatus.value = newStatus

                // Recargar la lista de cuestionarios para reflejar el cambio
                val creatorId = quizSnapshot.getString("creatorId") ?: ""
                loadQuizzesByCreatorId(creatorId)

            } catch (e: Exception) {
                _message.value = "Failed to update quiz status: ${e.message}"
            }
        }
    }






}

