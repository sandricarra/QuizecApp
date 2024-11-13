// LoginViewModel.kt
package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    // Instancia de FirebaseAuth: Para gestionar el inicio de sesión de usuario con Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado para almacenar el resultado de autenticación
    private val _loginState = MutableLiveData<LoginState>()

    // Propiedad de solo lectura para acceder al estado de autenticación
    val loginState: LiveData<LoginState> get() = _loginState

    // Método para iniciar sesión con email y contraseña
    fun login(email: String, password: String) {

        // Autentica usuario con email y contraseña
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Autentificación exitosa: Actualizamos LoginState.Success (indica éxito)
                    _loginState.value = LoginState.Success // Autenticación exitosa
                } else {
                    // Autentificación no exitosa: Actualizamos LoginState.Error (indica error)
                    _loginState.value = LoginState.Error(task.exception?.message ?: "Error desconocido")
                }
            }
    }
}

// Clase para representar el estado de autenticación
sealed class LoginState {
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}



