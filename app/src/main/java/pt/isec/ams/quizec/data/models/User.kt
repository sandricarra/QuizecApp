import com.google.firebase.Timestamp
import pt.isec.ams.quizec.data.models.UserRole
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User(
    val id: String = "",
    val name: String = "",
    val participatedQuizzes: List<String> = listOf(), // Lista de IDs de los cuestionarios en los que ha participado
    val email: String = "",
    val profilePictureUrl: String? = null,
    val role: UserRole = UserRole.STUDENT,
    val joinDate: Timestamp = Timestamp.now(), // Timestamp
    val completedQuizzes: List<String> = listOf(),
    val score: Int = 0,
    val lastLogin: Long? = null
)

