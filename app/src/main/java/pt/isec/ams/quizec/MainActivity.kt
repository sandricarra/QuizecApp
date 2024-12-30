package pt.isec.ams.quizec

import ManageQuizScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isec.ams.quizec.ui.screens.*
import pt.isec.ams.quizec.ui.theme.QuizecTheme
import pt.isec.ams.quizec.ui.viewmodel.AuthViewModel
import pt.isec.ams.quizec.ui.viewmodel.LanguageViewModel
import pt.isec.ams.quizec.ui.viewmodel.LoginViewModel
import pt.isec.ams.quizec.ui.viewmodel.ManageQuizViewModel
import pt.isec.ams.quizec.ui.viewmodel.QuizScreenViewModel
import pt.isec.ams.quizec.ui.viewmodel.RegisterViewModel
import java.util.Locale
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import pt.isec.ams.quizec.ui.theme.MyAppTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val languageViewModel: LanguageViewModel = LanguageViewModel()

        // Obtén la preferencia de idioma almacenada
        val preferredLanguage = languageViewModel.getLanguagePreference(applicationContext)
        Log.d("MainActivity", "Preferred Language: $preferredLanguage")
        // Establece el idioma
        languageViewModel.setAppLocale(applicationContext, "es")*/
        val languageViewModel = LanguageViewModel()

        // Comprueba y establece el idioma al iniciar la aplicación
        languageViewModel.checkAndSetLanguage(applicationContext)
        enableEdgeToEdge() // Activa el modo edge-to-edge para un diseño inmersivo
        setContent {
            MyAppTheme {
                QuizecApp()
            }
        }
    }

    @Composable
    fun QuizecApp() {
        val navController = rememberNavController()
        val context = LocalContext.current
        val languageViewModel: LanguageViewModel = LanguageViewModel()

        // Cambia el idioma al iniciar la aplicación
        /*LaunchedEffect(Unit) {
            val preferredLanguage = languageViewModel.getLanguagePreference(context)
            languageViewModel.setAppLocale(context, preferredLanguage)
        }*/
        languageViewModel.checkAndSetLanguage(context)

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Contenido principal
            Scaffold(
                modifier = Modifier.systemBarsPadding(),

            )


            { innerPadding ->
                AppNavHost(navController, innerPadding, authViewModel = viewModel())
            }
        }
    }



    @Composable
    fun AppNavHost(
        navController: NavHostController,
        innerPadding: PaddingValues,
        authViewModel: AuthViewModel
    ) {
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
                    creatorId = authViewModel.creatorId,
                )
            }

            composable("quizAccessScreen/{quizId}") {
                val viewModel = viewModel<QuizScreenViewModel>()
                QuizAccessScreen(
                    navController = navController,
                    viewModel = viewModel,
                    authViewModel.creatorId
                )
            }


            composable("questionHistory/{quizId}/{userId}") { backStackEntry ->
                val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                QuestionHistoryScreen(
                    navController = navController,
                    quizId = quizId,
                    userId = userId
                )
            }


            composable("quizCreation") {
                QuizCreationScreen(
                    creatorId = authViewModel.creatorId, // Pasa el creatorId al crear el quiz
                )
            }
            composable("quizHistory/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                QuizHistoryScreen(navController = navController, userId = userId)
            }


            composable("quizAccessCode/{quizId}") { backStackEntry ->
                val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
                QuizAccessCodeScreen(quizId = quizId)
            }

            composable("editQuestion/{questionId}") { backStackEntry ->
                val questionId = backStackEntry.arguments?.getString("questionId") ?: ""
                EditQuestionScreen(
                    questionId = questionId,
                    navController = navController
                )
            }
            composable("manageQuiz") {
                ManageQuizScreen(
                    navController = navController,
                    creatorId = authViewModel.creatorId,
                    viewModel = viewModel<ManageQuizViewModel>()
                )
            }

            composable("resultsScreen/{questionId}") { backStackEntry ->
                val questionId = backStackEntry.arguments?.getString("questionId") ?: ""
                ResultsScreen(questionId = questionId)
            }

        }

    }}

















