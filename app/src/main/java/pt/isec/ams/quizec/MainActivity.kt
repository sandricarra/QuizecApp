package pt.isec.ams.quizec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.ams.quizec.ui.screens.*
import pt.isec.ams.quizec.viewmodel.AuthViewModel
import pt.isec.ams.quizec.viewmodel.LoginViewModel
import pt.isec.ams.quizec.viewmodel.QuizScreenViewModel
import pt.isec.ams.quizec.viewmodel.RegisterViewModel


class MainActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Activa el modo edge-to-edge para un diseño inmersivo
        setContent {
            QuizecApp()
        }
    }
}

@Composable
fun QuizecApp() {
    val navController = rememberNavController()
    Scaffold(
        modifier = androidx.compose.ui.Modifier.systemBarsPadding(), // Asegura que la UI respete las áreas del sistema
    ) { innerPadding ->
        AppNavHost(navController, innerPadding, authViewModel = viewModel())
    }
}

@Composable
fun AppNavHost(navController: NavHostController, innerPadding: PaddingValues, authViewModel: AuthViewModel) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = androidx.compose.ui.Modifier.padding(innerPadding)
    ) {
        composable("login") {
            LoginScreen(navController, viewModel<LoginViewModel>())
        }
        composable("register") {
            RegisterScreen(navController, viewModel<RegisterViewModel>())
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                creatorId = authViewModel.creatorId
            )
        }

        composable("quizAccessScreen/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizAccessScreen(
                viewModel = viewModel<QuizScreenViewModel>(),
                isLocationValid = {
                    // Acción cuando la ubicación es válida
                    println("Location is valid, proceeding with quiz participation.")
                },
                onError = { errorMessage ->
                    println("Error: $errorMessage")
                }
            )
        }
        composable("quizScreen/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizScreen(quizId = quizId, viewModel = viewModel<QuizScreenViewModel>())
        }

        composable("questionHistory/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuestionHistoryScreen(navController = navController, quizId = quizId)
        }

        composable("quizCreation") {
            QuizCreationScreen(
                creatorId = authViewModel.creatorId, // Pasa el creatorId al crear el quiz
                onQuizSaved = { navController.navigate("home") }
            )
        }
        composable("quizHistory") {
            QuizHistoryScreen(navController = navController)
        }

        composable("quizAccessCode/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            QuizAccessCodeScreen(quizId = quizId)
        }
    }
}







