package pt.isec.ams.quizec.ui.screens



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
fun HomeScreen(navController: NavController,creatorId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(painter = painterResource(id = R.drawable.baseline_quiz_24), contentDescription = null)
        // Mensaje de bienvenida
        Text(
            text = "Welcome to Quizec!",
            color = Color(0xFFFFA6A6), // Rojo claro (personalizado con código hexadecimal)
            fontSize = 28.sp, // Tamaño grande
            fontWeight = FontWeight.Bold, // Opcional: Hacerlo en negrita
            textAlign = TextAlign.Center, // Opcional: Centrar el texto
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Espaciado alrededor del texto
        )

        Spacer(modifier = Modifier.height(24.dp))


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("HOMEPAGE", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("quizCreation") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create a Quiz")
                }
            }


        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ver el historial de cuestionarios
        Button(
            onClick = {
                // Navegar a una pantalla para ver cuestionarios (a implementar)
                navController.navigate("quizHistory")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("My Quiz History")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cerrar sesión (puedes conectarlo con FirebaseAuth más adelante)
        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true } // Navegar a Login y limpiar pila
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
