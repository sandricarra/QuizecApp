package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.data.models.QuizJoin

class QuizJoinViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    fun joinQuizRoom(
        roomId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val quizJoin = QuizJoin(
            roomId = roomId,
            userId = userId
        )

        db.collection("quizRooms").document(roomId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    db.collection("quizRooms").document(roomId).collection("participants")
                        .document(userId)
                        .set(quizJoin)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it) }
                } else {
                    onError(Exception("Room does not exist"))
                }
            }
            .addOnFailureListener { onError(it) }
    }
}
