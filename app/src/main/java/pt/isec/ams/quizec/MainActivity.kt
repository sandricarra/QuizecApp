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
import com.google.firebase.firestore.FirebaseFirestore

import pt.isec.ams.quizec.ui.screens.*
import pt.isec.ams.quizec.ui.theme.QuizecTheme

class MainActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase
        db = FirebaseFirestore.getInstance()


        setContent {
            QuizecApp()
        }


        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )

        // Agregar el usuario a la colección 'users'
        db.collection("users")
            .add(user)  // Agrega el documento con un ID automático
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error adding document", e)
            }

    }}


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

