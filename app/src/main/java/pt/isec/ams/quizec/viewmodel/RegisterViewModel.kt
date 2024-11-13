// RegisterViewModel.kt
package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {
    // Para registrar nuevos usuarios en Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado de registro (exitoso o con error)
    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> get() = _registerState

    // Método para registrar un nuevo usuario en Firebase
    fun register(email: String, password: String) {
        // Crear un nuevo usuario con email y contraseña
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registro exitoso: Actualizamos RegisterState.Success
                    _registerState.value = RegisterState.Success
                } else {
                    // Registro no exitoso: Actualizamos RegisterState.Error
                    _registerState.value = RegisterState.Error(task.exception?.message ?: "Error desconocido")
                }
            }
    }
}

// Clase para representar el estado de registro
sealed class RegisterState {
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}
