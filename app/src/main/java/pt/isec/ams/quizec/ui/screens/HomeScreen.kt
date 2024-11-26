package pt.isec.ams.quizec.ui.screens



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pt.isec.ams.quizec.R

@Composable
fun HomeScreen(navController: NavController, creatorId: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Tamaño para ajustar la imagen
                    .padding(bottom = 16.dp)
            )
        }

        item {
            Text(
                text = "Welcome to Quizec!",
                color = Color(0xFFFFA6A6),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        item {
            Text(
                text = "HOMEPAGE",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        item {
            Button(
                onClick = { navController.navigate("quizCreation") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Create a Quiz")
            }
        }
        item {
            // Asume que el quizId que quieres pasar es una variable, por ejemplo "quizId"
            val quizId = "someQuizId"  // Este debe ser el ID del cuestionario que quieras pasar
            Button(
                onClick = {
                    // Navega pasando el quizId como argumento
                    navController.navigate("quizScreen/$quizId")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Participate in a Quiz")
            }
        }

        item {
            Button(
                onClick = {
                    navController.navigate("quizHistory")
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("My Quiz History")
            }
        }







        item {
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Logout")
            }
        }
    }
}
