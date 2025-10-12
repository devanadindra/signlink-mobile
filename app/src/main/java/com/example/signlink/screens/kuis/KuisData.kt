package com.example.signlink.screens.kuis

data class Option(val label: String, val answerText: String)

data class QuizQuestion(
    val id: Int,
    val videoUrl: String,
    val questionText: String,
    val options: List<Option>,
    val correctAnswer: String
)

/**
 * Repository sederhana untuk data kuis.
 * Dalam aplikasi nyata, data ini akan diambil dari Firestore atau API.
 */
object QuizRepository {

    // Kumpulan data untuk Kuis "Kata Dasar A"
    private val kataDasarAQuestions = listOf(
        QuizQuestion(
            id = 1,
            videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
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
            videoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4",
            questionText = "Kata apa yang diperagakan?",
            options = listOf(
                Option("A", "Ibu"),
                Option("B", "Ayah"),
                Option("C", "Kakek"),
                Option("D", "Nenek")
            ),
            correctAnswer = "Ayah"
        ),
        QuizQuestion(
            id = 3,
            videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            questionText = "Pilihan mana yang benar?",
            options = listOf(
                Option("A", "Cinta"),
                Option("B", "Kasih"),
                Option("C", "Sayang"),
                Option("D", "Rindu")
            ),
            correctAnswer = "Sayang"
        ),
        QuizQuestion(
            id = 4,
            videoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4",
            questionText = "Tebak kata ini!",
            options = listOf(
                Option("A", "Pagi"),
                Option("B", "Siang"),
                Option("C", "Sore"),
                Option("D", "Malam")
            ),
            correctAnswer = "Pagi"
        ),
        QuizQuestion(
            id = 5,
            videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            questionText = "Terakhir, apa isyaratnya?",
            options = listOf(
                Option("A", "Ya"),
                Option("B", "Tidak"),
                Option("C", "Mungkin"),
                Option("D", "Bisa")
            ),
            correctAnswer = "Tidak"
        )
    )

    private val abjadDasarQuestions = listOf(
        QuizQuestion(
            id = 1,
            videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
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
            videoUrl = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4",
            questionText = "Huruf yang benar adalah?",
            options = listOf(
                Option("A", "E"),
                Option("B", "F"),
                Option("C", "G"),
                Option("D", "H")
            ),
            correctAnswer = "H"
        )
    )

    private val allQuizzes = mapOf(
        "abjad" to abjadDasarQuestions,
        "kata_a" to kataDasarAQuestions,

    )

    /**
     * Mengambil daftar pertanyaan kuis berdasarkan ID/Route.
     */
    fun getQuestionsByRoute(route: String): List<QuizQuestion> {
        return allQuizzes[route] ?: emptyList()
    }
}
