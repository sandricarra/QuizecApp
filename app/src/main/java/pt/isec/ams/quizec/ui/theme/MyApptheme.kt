package pt.isec.ams.quizec.ui.theme


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import pt.isec.ams.quizec.R

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    QuizecTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background // Fondo para accesibilidad
        ) {
            // Fondo con imagen
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(R.drawable.background), // background.jpg en res/drawable
                    contentDescription = null,
                    contentScale = ContentScale.Crop, // Ajusta la imagen al tamaño de la pantalla
                    modifier = Modifier.fillMaxSize()
                )

                // Contenido de las pantallas
                content()
            }
        }
    }
}
