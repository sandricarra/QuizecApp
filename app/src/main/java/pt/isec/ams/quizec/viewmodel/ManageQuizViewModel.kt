package pt.isec.ams.quizec.viewmodel

import User
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    private val _waitingParticipants = MutableStateFlow<List<User>>(emptyList())
    private val _showResultsImmediatelyMap = mutableMapOf<String, MutableStateFlow<Boolean>>()

    private val _participantsForQuiz = mutableMapOf<String, MutableStateFlow<List<User>>>()

    private val _geolocationRestricted = MutableStateFlow(false)
    val geolocationRestricted: StateFlow<Boolean> = _geolocationRestricted
    private val _geolocationRestrictedMap = mutableMapOf<String, MutableStateFlow<Boolean>>()

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
                        println("Quiz: ${quiz.title}, Participants: ${quiz.participants.size}")
                    }
                    quiz
                }
                _quizzes.value = quizList
            }
            .addOnFailureListener { exception ->
                _message.value = "Failed to load quizzes: ${exception.message}"
            }
    }





    // Función para obtener los participantes

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
                val newStatus =
                    if (currentStatus == QuizStatus.AVAILABLE) QuizStatus.LOCKED else QuizStatus.AVAILABLE

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

    fun loadParticipantsForQuiz(quizId: String) {
        val db = FirebaseFirestore.getInstance()

        // Usamos `addSnapshotListener` para la actualización en tiempo real
        db.collection("quizzes").document(quizId)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    _message.value = "Failed to load quiz: ${exception.message}"
                    return@addSnapshotListener
                }

                documentSnapshot?.let { document ->
                    val quiz = document.toObject(Quiz::class.java)
                    quiz?.let {
                        loadUsersByIds(it.participants, quizId) // Cargar los usuarios por sus IDs
                    }
                }
            }
    }

    private fun loadUsersByIds(userIds: List<String>, quizId: String) {
        if (userIds.isEmpty()) {
            _participantsForQuiz[quizId]?.value = emptyList()
            return
        }

        val db = FirebaseFirestore.getInstance()

        // Usamos `addSnapshotListener` para observar los cambios en tiempo real
        db.collection("users")
            .whereIn("id", userIds)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _message.value = "Error loading users: ${exception.message}"
                    return@addSnapshotListener
                }

                snapshot?.let { docSnapshot ->
                    if (docSnapshot.isEmpty) {
                        _participantsForQuiz[quizId]?.value = emptyList()
                    } else {
                        val users = docSnapshot.documents.mapNotNull { document ->
                            document.toObject(User::class.java)
                        }
                        _participantsForQuiz[quizId]?.value = users
                    }
                }
            }
    }

    fun getParticipantsForQuiz(quizId: String): StateFlow<List<User>> {
        // Si no existe el StateFlow para este quiz, crear uno nuevo
        if (!_participantsForQuiz.containsKey(quizId)) {
            _participantsForQuiz[quizId] = MutableStateFlow(emptyList())
            loadParticipantsForQuiz(quizId)  // Cargar los participantes al crearlo
        }
        return _participantsForQuiz[quizId]!!
    }
    fun toggleGeolocationRestriction(quizId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            try {
                val quizRef = db.collection("quizzes").document(quizId)
                val quizSnapshot = quizRef.get().await()

                if (!quizSnapshot.exists()) {
                    _message.value = "Quiz not found."
                    return@launch
                }

                // Obtener el estado actual de la restricción de geolocalización
                val currentGeolocationRestriction =
                    quizSnapshot.getBoolean("isGeolocationRestricted") ?: false

                // Alternar la restricción de geolocalización
                val newGeolocationRestriction = !currentGeolocationRestriction

                // Actualizar la restricción en Firestore
                quizRef.update("isGeolocationRestricted", newGeolocationRestriction).await()

                // Actualizar el estado local
                _message.value = "Geolocation restriction updated successfully!"
                _geolocationRestrictedMap[quizId]?.value = newGeolocationRestriction

            } catch (e: Exception) {
                _message.value = "Failed to update geolocation restriction: ${e.message}"
            }
        }
        // Función para obtener el estado de geolocalización para un quiz específico

    }
    fun getGeolocationRestricted(quizId: String): StateFlow<Boolean> {
        // Si no existe el estado para este quiz, crearlo
        if (!_geolocationRestrictedMap.containsKey(quizId)) {
            _geolocationRestrictedMap[quizId] = MutableStateFlow(false) // valor inicial
        }
        return _geolocationRestrictedMap[quizId]!!
    }
    fun getShowResultsImmediately(quizId: String): StateFlow<Boolean> {
        // Si no existe el estado para este quiz, crearlo
        if (!_showResultsImmediatelyMap.containsKey(quizId)) {
            _showResultsImmediatelyMap[quizId] = MutableStateFlow(false) // valor inicial
        }
        return _showResultsImmediatelyMap[quizId]!!
    }
    fun toggleShowResultsImmediately(quizId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            try {
                val quizRef = db.collection("quizzes").document(quizId)
                val quizSnapshot = quizRef.get().await()

                if (!quizSnapshot.exists()) {
                    _message.value = "Quiz not found."
                    return@launch
                }

                // Obtener el estado actual de "Show Results Immediately"
                val currentShowResultsImmediately = quizSnapshot.getBoolean("showResultsImmediately") ?: false

                // Alternar el estado de "Show Results Immediately"
                val newShowResultsImmediately = !currentShowResultsImmediately

                // Actualizar el estado en Firestore
                quizRef.update("showResultsImmediately", newShowResultsImmediately).await()

                // Actualizar el estado local
                _message.value = "Show Results Immediately updated successfully!"
                _showResultsImmediatelyMap[quizId]?.value = newShowResultsImmediately

            } catch (e: Exception) {
                _message.value = "Failed to update Show Results Immediately: ${e.message}"
            }
        }
    }
}
















