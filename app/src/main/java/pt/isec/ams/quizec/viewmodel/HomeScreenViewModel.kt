package pt.isec.ams.quizec.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import pt.isec.ams.quizec.data.models.QuizStatus

class HomeScreenViewModel : ViewModel() {



    fun toggleAllQuizzesStatus(creatorId: String, context: Context, onSuccess: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("quizzes")
            .whereEqualTo("creatorId", creatorId) // Filtrar por el creador
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch() // Usar un batch para eficiencia y atomicidad

                querySnapshot.documents.forEach { document ->
                    val currentStatus = document.getString("status")?.let {
                        QuizStatus.valueOf(it)
                    } ?: QuizStatus.AVAILABLE

                    val newStatus = if (currentStatus == QuizStatus.AVAILABLE) QuizStatus.LOCKED else QuizStatus.AVAILABLE

                    batch.update(document.reference, "status", newStatus.name) // Actualizar en el batch
                }

                batch.commit() // Aplicar todas las actualizaciones del batch
                    .addOnSuccessListener {
                        Toast.makeText(context, "All quizzes updated successfully!", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to update quiz status: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch quizzes: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }




    // Función para obtener el estado actual del cuestionario desde Firestore
    private fun getQuizStatus(quizId: String): QuizStatus {
        // Simplemente devuelve un estado por defecto, ya que no tenemos una implementación completa aquí
        return QuizStatus.AVAILABLE
    }

    // Función para actualizar el estado del cuestionario en Firestore
    private fun updateQuizStatus(quizId: String, status: QuizStatus, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        FirebaseFirestore.getInstance().collection("quizzes").document(quizId)
            .update("status", status)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }


}
