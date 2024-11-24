package pt.isec.ams.quizec

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import pt.isec.ams.quizec.ui.screens.*
import pt.isec.ams.quizec.ui.theme.QuizecTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            QuizecApp()
        }

        // Interacción con Firebase Realtime Database
        val database = FirebaseDatabase.getInstance() // Obtén la instancia de la base de datos
        val myRef = database.getReference("message") // Nodo "message"

        // Escribir un mensaje en la base de datos
        myRef.setValue("Hello, World!")
            .addOnSuccessListener {
                Log.d(TAG, "Mensaje guardado correctamente en la base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al guardar el mensaje en la base de datos.", e)
            }

        // Leer datos en tiempo real
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java) // Lee el valor como String
                Log.d(TAG, "El valor es: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error al leer datos.", error.toException())
            }
        })
    }
}

@Composable
fun QuizecApp() {
    QuizecTheme {
        val navController = rememberNavController()
        AppNavHost(navController)
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("createQuiz") { QuizCreationScreen(navController) } // Nueva pantalla añadida
        composable("quizHistory") { /* TODO: Implementa QuizHistoryScreen */ }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QuizecApp()
}
