package com.example.signlink.screens.kuis

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.signlink.components.VideoPlayer
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.SignLinkTeal
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
private fun formatTime(seconds: Int): String {
    val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())
    val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuisDetailScreen(
    navController: NavController,
    quizId: String?
) {
    val questions = remember(quizId) {
        if (quizId != null) {
            QuizRepository.getQuestionsByRoute(quizId)
        } else {
            emptyList()
        }
    }

    val timeLimitMinutes = remember(quizId) {
        QuizRepository.getTimeLimit(quizId) ?: 10
    }

    val totalTimeSeconds = timeLimitMinutes * 60

    var timeRemainingSeconds by remember { mutableIntStateOf(totalTimeSeconds) }
    var isTimeUp by remember { mutableStateOf(false) }

    LaunchedEffect(timeRemainingSeconds) {
        if (timeRemainingSeconds > 0) {
            delay(1000L)
            timeRemainingSeconds--
        } else if (timeRemainingSeconds == 0 && !isTimeUp) {
            isTimeUp = true
            navController.navigate("kuis_result_screen/$quizId")
        }
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, String>() }
    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    val totalQuestions = questions.size

    val isQuizComplete = userAnswers.size == totalQuestions

    val quizTitle = quizId
        ?.split("/")?.last()
        ?.replace("_", " ")
        ?.split(" ")
        ?.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        ?: "Kuis SignLink"

    val timeDisplayText = formatTime(timeRemainingSeconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quizTitle, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Sisa Waktu", tint = SignLinkTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeDisplayText,
                            color = if (timeRemainingSeconds <= 60) Color.Red else SignLinkTeal,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Soal kuis untuk $quizTitle tidak ditemukan. Cek QuizRepository.kt.", color = DarkText)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            currentQuestion?.let { question ->
                QuestionStepper(
                    currentStep = currentQuestionIndex,
                    totalSteps = totalQuestions,
                    onStepClick = { index -> currentQuestionIndex = index },
                    answeredQuestionIds = userAnswers.keys.toList(),
                    allQuestions = questions,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                ) {
                    VideoPlayer(
                        videoUrl = question.videoUrl,
                        modifier = Modifier.fillMaxWidth().width(300.dp).clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = question.questionText, fontSize = 18.sp, color = DarkText, modifier = Modifier.padding(bottom = 16.dp))

                    question.options.forEach { option ->
                        QuizOption(
                            option = option,
                            isSelected = userAnswers[question.id] == option.answerText,
                            onSelect = { selectedAnswer ->
                                userAnswers[question.id] = selectedAnswer
                                QuizResultHolder.userAnswers = userAnswers.toMap()
                                QuizResultHolder.quizId = quizId
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                        enabled = currentQuestionIndex > 0,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.LightGray.copy(alpha = 0.5f), contentColor = DarkText, disabledContentColor = Color.LightGray),
                        modifier = Modifier.size(48.dp)
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Sebelumnya") }

                    if (currentQuestionIndex == totalQuestions - 1) {
                        Button(
                            onClick = {
                                navController.navigate("kuis_result_screen/$quizId")
                            },
                            enabled = isQuizComplete,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal, disabledContainerColor = SignLinkTeal.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f).height(48.dp).padding(horizontal = 16.dp)
                        ) { Text("Submit Quiz", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    IconButton(
                        onClick = {
                            if (currentQuestionIndex < totalQuestions - 1) currentQuestionIndex++
                            else if (currentQuestionIndex == totalQuestions - 1 && isQuizComplete) {
                                navController.navigate("kuis_result_screen/$quizId")
                            }
                        },
                        enabled = currentQuestionIndex < totalQuestions - 1,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = SignLinkTeal, contentColor = Color.White, disabledContainerColor = SignLinkTeal.copy(alpha = 0.5f)),
                        modifier = Modifier.size(48.dp)
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Selanjutnya") }
                }
            }
        }
    }
}

/**
 * Komponen untuk men-display nomor-nomor soal (Stepper/Pagination)
 */
@Composable
fun QuestionStepper(
    currentStep: Int,
    totalSteps: Int,
    onStepClick: (Int) -> Unit,
    answeredQuestionIds: List<Int>,
    allQuestions: List<QuizQuestion>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(totalSteps) { index ->
            val isActive = index == currentStep
            val questionId = allQuestions.getOrNull(index)?.id ?: -1
            val isAnswered = answeredQuestionIds.contains(questionId)

            val color = when {
                isActive -> SignLinkTeal
                isAnswered -> Color(0xFF4CAF50)
                else -> Color.LightGray
            }
            val textColor = if (isActive || isAnswered) Color.White else DarkText

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onStepClick(index) }
                        .border(
                            width = if (isActive) 2.dp else 1.dp,
                            color = if (isActive) SignLinkTeal else Color.LightGray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                if (isActive) {
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        modifier = Modifier.width(32.dp).clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
                        thickness = 2.dp,
                        color = SignLinkTeal
                    )
                }
            }
        }
    }
}

/**
 * Komponen untuk setiap opsi jawaban (A, B, C, D)
 */
@Composable
fun QuizOption(
    option: Option,
    isSelected: Boolean,
    onSelect: (String) -> Unit
) {
    val backgroundColor = if (isSelected) SignLinkTeal else Color.White
    val borderColor = if (isSelected) SignLinkTeal else Color.LightGray
    val contentColor = if (isSelected) Color.White else DarkText
    val labelColor = if (isSelected) Color.White else DarkText

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onSelect(option.answerText) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.White.copy(alpha = 0.2f) else borderColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = option.label,
                color = labelColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = option.answerText,
            color = contentColor,
            fontSize = 16.sp
        )
    }
}
