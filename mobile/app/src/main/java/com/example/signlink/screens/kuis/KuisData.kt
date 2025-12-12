package com.example.signlink.screens.kuis

import com.example.signlink.data.models.kuis.SoalKuisRes

data class QuestionResult(
    val questionId: String,
    val questionText: String,
    val videoUrl: String,
    val userAnswer: String?,
    val correctAnswer: String,
    val isCorrect: Boolean
)

object QuizResultHolder {
    var userAnswers: Map<String, String> = emptyMap()
    var quizId: String? = null

    fun calculateDetailedScore(questions: List<SoalKuisRes>): Pair<List<QuestionResult>, Int> {
        val results = mutableListOf<QuestionResult>()
        var correctCount = 0

        questions.forEach { question ->
            val userAnswer = userAnswers[question.id]
            val isCorrect = userAnswer != null && userAnswer == question.jawabanBenar

            if (isCorrect) {
                correctCount++
            }

            results.add(
                QuestionResult(
                    questionId = question.id,
                    questionText = question.soal,
                    videoUrl = question.videoUrl,
                    userAnswer = userAnswer,
                    correctAnswer = question.jawabanBenar,
                    isCorrect = isCorrect
                )
            )
        }
        return Pair(results, correctCount)
    }

    fun clear() {
        userAnswers = emptyMap()
        quizId = null
    }
}
