package pt.isec.ams.quizec.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import User
import pt.isec.ams.quizec.data.models.UserRole


class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _registerState = MutableLiveData<RegisterState?>()
    val registerState: LiveData<RegisterState?> get() = _registerState

    fun register(email: String, password: String, name: String, role: UserRole) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Al registrar al usuario, creamos el documento en Firestore
                    val userId = auth.currentUser?.uid
                    val user = User(
                        id = userId ?: "",
                        name = name,
                        email = email,
                        role = role,
                        joinDate = Timestamp.now()
                    )
                    userId?.let { addUserToFirestore(it, user) }
                    _registerState.value = RegisterState.Success
                } else {
                    val errorMessage = getFirebaseAuthErrorMessage(task.exception)
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            }
    }

    // Método para agregar usuario a Firestore
    private fun addUserToFirestore(userId: String, user: User) {
        firestore.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                // El usuario se ha guardado correctamente
            }
            .addOnFailureListener { e ->
                // Manejar el error
                e.printStackTrace()
            }
    }



    // Función para obtener mensajes de error más descriptivos
    private fun getFirebaseAuthErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil. Elige una más segura."
            is FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico es inválido."
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo electrónico."
            else -> exception?.message ?: "Se produjo un error desconocido. Inténtalo nuevamente."
        }
    }

    // Método para reiniciar el estado de registro
    fun resetRegisterState() {
        _registerState.value = null
    }
}

// Clase para representar el estado de registro
sealed class RegisterState {
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}



