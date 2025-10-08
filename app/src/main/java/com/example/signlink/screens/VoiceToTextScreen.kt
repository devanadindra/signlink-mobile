package com.example.signlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Tetap diperlukan untuk NavItem
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R // Asumsi Resource R ada untuk logo
import com.example.signlink.ui.theme.* // Menggunakan warna dari theme Anda
import com.example.signlink.components.NavItem
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import androidx.compose.foundation.rememberScrollState // BARU: Import untuk fungsionalitas scroll
import androidx.compose.foundation.verticalScroll // BARU: Import untuk fungsionalitas scroll
import androidx.compose.ui.text.style.TextAlign

/**
 * Screen utama untuk fungsi Suara ke Tulisan (Voice to Text).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceToTextScreen(
    onHomeClicked: () -> Unit = {},
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {}
) {
    // State untuk menampung teks yang dihasilkan dari suara
    var recognizedText by remember { mutableStateOf("Teks hasil rekaman akan muncul di sini...") }
    // State untuk mengelola mode rekaman (true = sedang merekam)
    var isRecording by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Home, false, "home"),
        NavItem("Kamus", Icons.Default.Book, false, "kamus"),
        NavItem("Penerjemah", Icons.Default.Camera, false, "penerjemah"),
        NavItem("VTT", Icons.Default.Mic, true, "vtt"), // VTT adalah yang aktif
        NavItem("Profil", Icons.Default.Person, false, "profil")
    )

    Scaffold(
        bottomBar = {
            // MENGGUNAKAN: BottomBarSignLink dari AppComponents.kt
            BottomBarSignLink(
                items = navItems,
                onHomeClicked = onHomeClicked,
                onKamusClicked = onKamusClicked,
                onVTTClicked = onVTTClicked,
                onProfileClicked = onProfileClicked
            )
        },
        floatingActionButton = {
            // MENGGUNAKAN: MainFloatingActionButton dari AppComponents.kt
            MainFloatingActionButton(onClick = onCameraClicked)
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp)) // Jarak dari atas layar

            // 1. Logo Aplikasi (TERPISAH DARI CARD)
            Image(
                painter = painterResource(id = R.drawable.signlink),
                contentDescription = "SignLink Logo",
                modifier = Modifier.size(80.dp) // Ukuran logo
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Teks Aplikasi, Subtitle dan Deskripsi DIBUNGKUS DALAM CARD
            VttDescriptionCard(
                modifier = Modifier.fillMaxWidth(0.9f).padding(horizontal = 8.dp),
                subtitle = "Suara ke Tulisan",
                description = "Mulailah berbicara untuk mengubah suara menjadi teks"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Area Teks dan Tombol Mikrofon
            // BARU: Menggunakan Card untuk membungkus area teks agar memiliki elevasi yang sama dengan kartu deskripsi
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightGrayBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(300.dp) // Ketinggian tetap untuk area teks
                    .padding(horizontal = 8.dp)
            ) {
                // Teks Hasil Rekaman
                val scrollState = rememberScrollState()
                Text(
                    text = recognizedText,
                    color = DarkText,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize() // Memastikan Text mengisi Card
                        .verticalScroll(scrollState) // Mengaktifkan scroll
                )
            }

            // BARU: Tombol Mikrofon diletakkan di Column utama, di bawah Card Teks
            // Kami menggunakan Box untuk memposisikan FAB di atas elemen sebelumnya (Card Teks)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp)
                    // Hapus Spacer(modifier = Modifier.height(40.dp)) di bagian bawah
                    // sebagai gantinya, gunakan Spacer di sini untuk memberikan jarak ke Bottom Bar.
                    .height(60.dp) // Tinggi yang cukup untuk menampung offset FAB (48dp + padding)
            ) {
                FloatingActionButton(
                    onClick = {
                        isRecording = !isRecording // Toggle status rekaman
                        recognizedText = if (isRecording) "Mendengarkan..." else "Teks hasil rekaman akan muncul di sini..."
                        // TODO: Implementasi logika Voice-to-Text
                    },
                    containerColor = if (isRecording) Color.Red else SignLinkTeal, // Warna merah saat merekam
                    shape = RoundedCornerShape(50),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter) // Posisikan di atas Box ini
                        // Geser ke atas agar tumpang tindih dengan Card Teks
                        .offset(y = (-30).dp) // Offset negatif untuk mengangkat tombol
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = if (isRecording) "Stop Rekam" else "Mulai Rekam",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

        }
    }
}

/**
 * Komponen Card yang membungkus Teks Aplikasi, Subtitle, dan Deskripsi (Logo dipisahkan).
 */
@Composable
fun VttDescriptionCard(
    modifier: Modifier = Modifier,
    subtitle: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {

            // 1. Subtitle
            Text(
                text = subtitle,
                color = DarkText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 2. Deskripsi
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center, // DIPERBAIKI: Menambahkan rata tengah
                modifier = Modifier.fillMaxWidth() // DIPERBAIKI: Memastikan teks mengisi lebar penuh
            )
        }
    }
}
