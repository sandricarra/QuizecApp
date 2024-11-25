package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.data.models.QuizRoom

class QuizRoomViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    fun createQuizRoom(
        quizId: String,
        creatorId: String,
        onSuccess: (roomId: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val roomId = generateRoomId()
        val quizRoom = QuizRoom(
            roomId = roomId,
            quizId = quizId,
            creatorId = creatorId,
            startTime = System.currentTimeMillis(),
            isActive = true
        )

        db.collection("quizRooms").document(roomId)
            .set(quizRoom)
            .addOnSuccessListener { onSuccess(roomId) }
            .addOnFailureListener { onError(it) }
    }

    private fun generateRoomId(): String {
        return (1..6).map { ('A'..'Z') + ('0'..'9') }.flatten().shuffled().take(6).joinToString("")
    }
}
