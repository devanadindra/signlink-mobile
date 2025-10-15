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

    private val kataDasar1Questions = listOf(
        QuizQuestion(
            id = 1,
            videoUrl = "/kamus_videos/A/Apa.mp4",
            questionText = "Apa arti dari bahasa isyarat pada video diatas?",
            options = listOf(
                Option("A", "Apa"),
                Option("B", "Abadi"),
                Option("C", "Abad"),
                Option("D", "Aku")
            ),
            correctAnswer = "Apa"
        ),
        QuizQuestion(
            id = 2,
            videoUrl = "/kamus_videos/B/Baik.mp4",
            questionText = "Kata apa yang diperagakan?",
            options = listOf(
                Option("A", "Anak"),
                Option("B", "Baik"),
                Option("C", "Kamu"),
                Option("D", "Contoh")
            ),
            correctAnswer = "Baik"
        ),
        QuizQuestion(
            id = 3,
            videoUrl = "/kamus_videos/B/Belajar.mp4",
            questionText = "Pilihan mana yang benar?",
            options = listOf(
                Option("A", "Aku"),
                Option("B", "Cara"),
                Option("C", "Anak"),
                Option("D", "Belajar")
            ),
            correctAnswer = "Belajar"
        ),
        QuizQuestion(
            id = 4,
            videoUrl = "/kamus_videos/D/Duduk.mp4",
            questionText = "Tebak kata ini!",
            options = listOf(
                Option("A", "Bicara"),
                Option("B", "Belajar"),
                Option("C", "Duduk"),
                Option("D", "Abad")
            ),
            correctAnswer = "Duduk"
        ),
        QuizQuestion(
            id = 5,
            videoUrl = "/kamus_videos/D/Dia.mp4",
            questionText = "Terakhir, apa isyaratnya?",
            options = listOf(
                Option("A", "Dia"),
                Option("B", "Duduk"),
                Option("C", "Abad"),
                Option("D", "Bisa")
            ),
            correctAnswer = "Abad"
        )
    )

    private val abjadDasarQuestions = listOf(
        QuizQuestion(
            id = 1,
            videoUrl = "/kamus_videos/A/A.mp4",
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
            videoUrl = "/kamus_videos/H/H.mp4",
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
            videoUrl = "/kamus_videos/G/G.mp4",
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
            videoUrl = "/kamus_videos/F/F.mp4",
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
            videoUrl = "/kamus_videos/E/E.mp4",
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
        "kata_dasar_1" to kataDasar1Questions,
    )

    fun getQuestionsByRoute(route: String?): List<QuizQuestion> {
        val key = route?.split("/")?.last() ?: return emptyList()
        return allQuizzes[key] ?: emptyList()
    }

    fun getTimeLimit(route: String?): Int? {
        return when (route) {
            "kuis_start/abjad" -> 10
            "kuis_start/kata_dasar_1" -> 10
            else -> null
        }
    }
}
