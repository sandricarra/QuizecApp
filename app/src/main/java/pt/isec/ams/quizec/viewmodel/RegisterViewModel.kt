import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegisterViewModel : ViewModel() {
    // Para registrar nuevos usuarios en Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado de registro (exitoso o con error), ahora es anulable
    private val _registerState = MutableLiveData<RegisterState?>()
    val registerState: LiveData<RegisterState?> get() = _registerState

    // Método para registrar un nuevo usuario en Firebase
    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registerState.value = RegisterState.Success
                } else {
                    val errorMessage = getFirebaseAuthErrorMessage(task.exception)
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            }
    }

    // Método para reiniciar el estado de registro
    fun resetRegisterState() {
        _registerState.value = null
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
}

// Clase para representar el estado de registro
sealed class RegisterState {
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
