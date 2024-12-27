package pt.isec.ams.quizec.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LightBlue,          // Botones y elementos primarios
    secondary = LightGreyBlue,    // Elementos secundarios
    tertiary = DarkBlue,          // Elementos terciarios
    background = White,           // Fondo blanco
    surface = White,              // Superficies
    onPrimary = Color.White,      // Texto sobre botones
    onSecondary = Color.Black,    // Texto sobre elementos secundarios
    onBackground = Color.Black,   // Texto sobre el fondo
    onSurface = Color.Black       // Texto sobre superficies
)

@Composable
fun QuizecTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}