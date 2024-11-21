// LoginViewModel.kt
package pt.isec.ams.quizec.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loginState = MutableLiveData<LoginState?>() // Permitir valores nulos
    val loginState: LiveData<LoginState?> get() = _loginState

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "Error desconocido")
                }
            }
    }

    // Nueva función para limpiar el estado
    fun resetState() {
        _loginState.value = null // Ahora es posible asignar null
    }
}




// Clase para representar el estado de autenticación
sealed class LoginState {
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}



