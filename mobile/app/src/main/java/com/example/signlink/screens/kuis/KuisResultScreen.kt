@file:Suppress("DEPRECATION")

package com.example.signlink.screens.kuis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.signlink.Destinations
import com.example.signlink.components.VideoPlayer
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.SignLinkTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuisResultScreen(
    navController: NavController,
    quizId: String?
) {
    val storedQuizId = QuizResultHolder.quizId
    val questions = remember(storedQuizId) {
        QuizRepository.getQuestionsByRoute(storedQuizId)
    }

    val (detailedResults, correctAnswers) = remember {
        QuizResultHolder.calculateDetailedScore(questions)
    }

    LaunchedEffect(Unit) {
        QuizResultHolder.clear()
    }

    val totalQuestions = questions.size
    val finalScore = if (totalQuestions > 0) {
        (correctAnswers.toFloat() / totalQuestions.toFloat() * 100).toInt()
    } else {
        0
    }

    val quizName = storedQuizId?.split("/")?.last()?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "Kuis"

    val message = when {
        finalScore >= 80 -> "Luar Biasa! Kamu menguasai ${quizName}!"
        finalScore >= 50 -> "Bagus! Terus tingkatkan pemahamanmu tentang ${quizName}."
        else -> "Jangan menyerah! Coba lagi dan pelajari $quizName lebih dalam."
    }

    val scoreColor = when {
        finalScore >= 80 -> Color(0xFF4CAF50)
        finalScore >= 50 -> Color(0xFFFF9800)
        else -> Color.Red
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Hasil Skor", "Tinjauan Jawaban")


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Kuis", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                containerColor = Color.White,
                contentColor = SignLinkTeal,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(selectedTabIndex)
                            .height(3.dp),
                        color = SignLinkTeal
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedTabIndex == index) SignLinkTeal else DarkText.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }


            when (selectedTabIndex) {
                0 -> ResultTab(
                    finalScore = finalScore,
                    correctAnswers = correctAnswers,
                    totalQuestions = totalQuestions,
                    quizName = quizName,
                    message = message,
                    scoreColor = scoreColor,
                    onRepeatQuiz = {
                        if (storedQuizId != null) {
                            navController.navigate("${Destinations.KUIS_DETAIL_SCREEN}/$quizId")
                        }
                    },
                    onGoHome = {
                        navController.popBackStack(route = "home_screen", inclusive = false)
                    }
                )
                1 -> ReviewTab(detailedResults = detailedResults)
            }
        }
    }
}

@Composable
fun ResultTab(
    finalScore: Int,
    correctAnswers: Int,
    totalQuestions: Int,
    quizName: String,
    message: String,
    scoreColor: Color,
    onRepeatQuiz: () -> Unit,
    onGoHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {

            Text(
                text = if (finalScore >= 80) "🏆" else "💪",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Skor Akhir Anda",
                fontSize = 20.sp,
                color = DarkText.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.1f)),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(160.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "$finalScore%",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = message,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ResultStat(label = "Benar", value = correctAnswers.toString(), color = Color(0xFF4CAF50))
                ResultStat(label = "Salah", value = (totalQuestions - correctAnswers).toString(), color = Color.Red)
                ResultStat(label = "Total", value = totalQuestions.toString(), color = SignLinkTeal)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onRepeatQuiz,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Ulangi Kuis", modifier = Modifier.size(24.dp).padding(end = 8.dp))
                Text("Ulangi Kuis", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoHome,
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkText),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = "Kembali ke Home", modifier = Modifier.size(24.dp).padding(end = 8.dp))
                Text("Kembali ke Home", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ResultStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = DarkText.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ReviewTab(detailedResults: List<QuestionResult>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(detailedResults) { index, result ->
            ReviewItem(index + 1, result)
        }
    }
}

@Composable
fun ReviewItem(questionNumber: Int, result: QuestionResult) {
    val statusColor = if (result.isCorrect) Color(0xFF4CAF50) else Color.Red
    val statusIcon = if (result.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Soal $questionNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DarkText
                )
                Icon(
                    statusIcon,
                    contentDescription = if (result.isCorrect) "Benar" else "Salah",
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            VideoPlayer(
                videoUrl = result.videoUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(250.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(bottom = 12.dp)
            )

            Text(
                text = result.questionText,
                fontSize = 16.sp,
                color = DarkText.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Jawaban Anda:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = DarkText
            )
            Text(
                text = result.userAnswer ?: "Tidak Dijawab",
                fontSize = 16.sp,
                color = if (result.isCorrect) Color(0xFF4CAF50) else Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Jawaban Benar:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = DarkText
            )
            Text(
                text = result.correctAnswer,
                fontSize = 16.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}