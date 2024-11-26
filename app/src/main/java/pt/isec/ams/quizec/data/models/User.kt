import com.google.firebase.Timestamp
import pt.isec.ams.quizec.data.models.UserRole
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User(
    val id: String = "", // ID único del usuario, generado automáticamente, lo genera firebase
    val name: String = "",
    val participatedQuizzes: List<String> = listOf(), // Lista de IDs de los cuestionarios en los que ha participado
    val email: String = "", // Correo electrónico del usuario
    val profilePictureUrl: String? = null, //sin implementar
    val role: UserRole = UserRole.STUDENT, // Rol del usuario (estudiante, profesor, etc.)
    val joinDate: Timestamp = Timestamp.now(), // Timestamp, date de creacion
    val completedQuizzes: List<String> = listOf(), // Lista de IDs de los cuestionarios completados, sin implementar
    val score: Int = 0, // Puntaje del usuario, sin implementar
    val lastLogin: Long? = null // Timestamp del último inicio de sesión, sin implementar
)

