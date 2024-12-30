package pt.isec.ams.quizec.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter

import pt.isec.ams.quizec.R
import pt.isec.ams.quizec.ui.theme.BackgroundImage

import pt.isec.ams.quizec.ui.viewmodel.UserProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserProfileScreen(navController: NavController, userId: String, viewModel: UserProfileViewModel) {
    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    val user by viewModel.user
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackgroundImage()
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Blue // Cambiado a azul oscuro
            )
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red.copy(alpha = 0.8f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (user != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val profileImage = if (user!!.profilePictureUrl.isNullOrEmpty()) {
                            painterResource(id = R.drawable.ic_logo)
                        } else {
                            rememberAsyncImagePainter(model = user!!.profilePictureUrl)
                        }

                        Image(
                            painter = profileImage,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.3f)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = user!!.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue // Cambiado a azul oscuro
                        )

                        Text(
                            text = user!!.email,
                            fontSize = 16.sp,
                            color = Color.Blue // Cambiado a azul oscuro
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard(title = "Score", value = user!!.score.toString())
                        StatCard(title = "Role", value = user!!.role.toString())
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoCard(
                            title = "Join Date",
                            value = formatTimestamp(user!!.joinDate)
                        )
                        InfoCard(
                            title = "Last Login",
                            value = user!!.lastLogin?.let { formatTimestamp(it) } ?: "Never"
                        )
                    }
                }

                if (user!!.participatedQuizzes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Participated Quizzes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue, // Cambiado a azul oscuro
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(user!!.participatedQuizzes) { quizId ->
                        QuizCard(quizId, viewModel = viewModel)
                    }
                }

                if (user!!.completedQuizzes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Completed Quizzes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue, // Cambiado a azul oscuro
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(user!!.completedQuizzes) { quizId ->
                        QuizCard(quizId, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Blue // Cambiado a azul oscuro
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue // Cambiado a azul oscuro
            )
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Blue // Cambiado a azul oscuro
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue // Cambiado a azul oscuro
            )
        }
    }
}

@Composable
fun QuizCard(quizId: String, viewModel: UserProfileViewModel) {
    val quizTitle by rememberUpdatedState(viewModel.quizTitles[quizId] ?: "Loading...")

    LaunchedEffect(quizId) {
        viewModel.loadQuizTitle(quizId)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Text(
            text = quizTitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Blue, // Cambiado a azul oscuro
            modifier = Modifier.padding(16.dp)
        )
    }
}





fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(date)
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(date)
}
