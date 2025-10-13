package com.example.signlink.screens.kuis

data class Option(val label: String, val answerText: String)

data class QuizQuestion(
    val id: Int,
    val videoUrl: String,
    val questionText: String,
    val options: List<Option>,
    val correctAnswer: String
)

data class QuestionResult(
    val questionId: Int,
    val questionText: String,
    val videoUrl: String,
    val userAnswer: String?,
    val correctAnswer: String,
    val isCorrect: Boolean
)

object QuizResultHolder {
    var userAnswers: Map<Int, String> = emptyMap()
    var quizId: String? = null

    fun calculateDetailedScore(questions: List<QuizQuestion>): Pair<List<QuestionResult>, Int> {
        val results = mutableListOf<QuestionResult>()
        var correctCount = 0

        questions.forEach { question ->
            val userAnswer = userAnswers[question.id]
            val isCorrect = userAnswer != null && userAnswer == question.correctAnswer

            if (isCorrect) {
                correctCount++
            }

            results.add(
                QuestionResult(
                    questionId = question.id,
                    questionText = question.questionText,
                    videoUrl = question.videoUrl,
                    userAnswer = userAnswer,
                    correctAnswer = question.correctAnswer,
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


/**
 * Repository sederhana untuk data kuis.
 */
object QuizRepository {

    private val kataDasarAQuestions = listOf(
        QuizQuestion(
            id = 1,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Abang.webm",
            questionText = "Apa arti dari bahasa isyarat pada video diatas?",
            options = listOf(
                Option("A", "Abang"),
                Option("B", "Abadi"),
                Option("C", "Abad"),
                Option("D", "Aku")
            ),
            correctAnswer = "Abang"
        ),
        QuizQuestion(
            id = 2,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Ayah.webm",
            questionText = "Kata apa yang diperagakan?",
            options = listOf(
                Option("A", "Anak"),
                Option("B", "Ayah"),
                Option("C", "Anda"),
                Option("D", "Aku")
            ),
            correctAnswer = "Ayah"
        ),
        QuizQuestion(
            id = 3,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Aku.webm",
            questionText = "Pilihan mana yang benar?",
            options = listOf(
                Option("A", "Aku"),
                Option("B", "Andai"),
                Option("C", "Anak"),
                Option("D", "Ayah")
            ),
            correctAnswer = "Aku"
        ),
        QuizQuestion(
            id = 4,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Andai.webm",
            questionText = "Tebak kata ini!",
            options = listOf(
                Option("A", "Aku"),
                Option("B", "Akan"),
                Option("C", "Andai"),
                Option("D", "Abad")
            ),
            correctAnswer = "Andai"
        ),
        QuizQuestion(
            id = 5,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/Abad.webm",
            questionText = "Terakhir, apa isyaratnya?",
            options = listOf(
                Option("A", "Aba"),
                Option("B", "Abang"),
                Option("C", "Abad"),
                Option("D", "Aku")
            ),
            correctAnswer = "Abad"
        )
    )

    private val abjadDasarQuestions = listOf(
        QuizQuestion(
            id = 1,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/A.webm",
            questionText = "Isyarat ini mewakili huruf apa?",
            options = listOf(
                Option("A", "A"),
                Option("B", "B"),
                Option("C", "C"),
                Option("D", "D")
            ),
            correctAnswer = "A"
        ),
        QuizQuestion(
            id = 2,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/H.webm",
            questionText = "Huruf yang benar adalah?",
            options = listOf(
                Option("A", "E"),
                Option("B", "F"),
                Option("C", "G"),
                Option("D", "H")
            ),
            correctAnswer = "H"
        ),
        QuizQuestion(
            id = 3,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/G.webm",
            questionText = "Huruf yang benar adalah?",
            options = listOf(
                Option("A", "E"),
                Option("B", "F"),
                Option("C", "G"),
                Option("D", "H")
            ),
            correctAnswer = "G"
        ),
        QuizQuestion(
            id = 4,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/F.webm",
            questionText = "Huruf yang benar adalah?",
            options = listOf(
                Option("A", "E"),
                Option("B", "F"),
                Option("C", "G"),
                Option("D", "H")
            ),
            correctAnswer = "F"
        ),
        QuizQuestion(
            id = 5,
            videoUrl = "http://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/E.webm",
            questionText = "Huruf yang benar adalah?",
            options = listOf(
                Option("A", "E"),
                Option("B", "F"),
                Option("C", "G"),
                Option("D", "H")
            ),
            correctAnswer = "E"
        )

    )

    private val allQuizzes = mapOf(
        "abjad" to abjadDasarQuestions,
        "kata_dasar_a" to kataDasarAQuestions,
    )

    fun getQuestionsByRoute(route: String?): List<QuizQuestion> {
        val key = route?.split("/")?.last() ?: return emptyList()
        return allQuizzes[key] ?: emptyList()
    }

    fun getTimeLimit(route: String?): Int? {
        return when (route) {
            "kuis_start/abjad" -> 10
            "kuis_start/kata_dasar_a" -> 10
            else -> null
        }
    }
}
