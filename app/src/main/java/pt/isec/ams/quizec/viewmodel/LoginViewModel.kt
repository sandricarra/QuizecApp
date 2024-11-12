package pt.isec.ams.quizec.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LoginViewModel", "Login successful!")
                } else {
                    Log.e("LoginViewModel", "Login failed: ${task.exception?.message}")
                }
            }
    }
}


