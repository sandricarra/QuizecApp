package pt.isec.ams.quizec.ui.viewmodel



import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val creatorId: String
        get() = firebaseAuth.currentUser?.uid ?: "" // Obtén el UID del usuario autenticado
}



