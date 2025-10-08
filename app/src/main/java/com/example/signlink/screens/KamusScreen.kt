package com.example.signlink.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import com.example.signlink.components.NavItem
import com.example.signlink.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KamusScreen(
    onHomeClicked: () -> Unit = {},
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {}
) {
    // Menetapkan Kamus sebagai item navigasi aktif
    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Home, false, "home"),
        NavItem("Kamus", Icons.Default.Book, true, "kamus"),
        NavItem("Penerjemah", Icons.Default.Camera, false, "penerjemah"),
        NavItem("VTT", Icons.Default.Mic, false, "vtt"),
        NavItem("Profil", Icons.Default.Person, false, "profil")
    )

    Scaffold(
        bottomBar = {
            BottomBarSignLink(
                items = navItems,
                onHomeClicked = onHomeClicked,
                onKamusClicked = onKamusClicked,
                onVTTClicked = onVTTClicked,
                onProfileClicked = onProfileClicked
            )
        },
        floatingActionButton = { MainFloatingActionButton(onClick = onCameraClicked) },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                // MODIFIKASI: Menggunakan verticalScroll di Column utama
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. Logo
            Image(
                painter = painterResource(id = R.drawable.signlink),
                contentDescription = "SignLink Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Deskripsi Card untuk Kamus
            DictionaryHeaderCard(
                modifier = Modifier.fillMaxWidth(0.9f).padding(horizontal = 8.dp),
                title = "Kamus Bahasa Isyarat SIBI",
                description = "Kumpulan Bahasa Isyarat dan terjemahannya"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Grid Alphabet
            AlphabetGrid(
                onLetterClick = { letter ->
                    // TODO: Tambahkan logika navigasi ke halaman detail kata
                    println("Letter clicked: $letter")
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Komponen Header Card khusus untuk Kamus
 */
@Composable
fun DictionaryHeaderCard(
    modifier: Modifier = Modifier,
    title: String,
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
            Text(
                text = title,
                color = DarkText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Komponen Grid Tombol Alphabet (A-Z)
 */
@Composable
fun AlphabetGrid(
    onLetterClick: (Char) -> Unit
) {
    // Characters A through Z
    val alphabet = ('A'..'Z').toList()
    val columns = 4
    val itemsPerGroup = columns * 6 // A-X (24 letters)

    // Split A-Z into the main grid (A-X) and the centered row (Y, Z)
    val mainGridLetters = alphabet.take(itemsPerGroup)
    val remainingLetters = alphabet.drop(itemsPerGroup) // Y and Z

    Column(
        modifier = Modifier.fillMaxWidth(0.9f).padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Grid (A-X) - 4 columns, semua harus square
        mainGridLetters.chunked(columns).forEach { rowLetters ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowLetters.forEach { letter ->
                    AlphabetButton(
                        letter = letter,
                        onClick = { onLetterClick(letter) },
                        modifier = Modifier.weight(1f),
                        isSquare = true // Tetapkan sebagai kotak
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Remaining Letters (Y, Z) - Centered dan persegi panjang
        // MODIFIKASI: Menghapus fillMaxWidth(0.6f) agar row mengambil lebar penuh parent
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            remainingLetters.forEach { letter ->
                AlphabetButton(
                    letter = letter,
                    onClick = { onLetterClick(letter) },
                    modifier = Modifier.weight(1f).height(60.dp), // Set tinggi agar terlihat seperti persegi panjang
                    isSquare = false // Tetapkan sebagai persegi panjang
                )
            }
        }
    }
}

/**
 * Komponen Tombol Tunggal untuk setiap huruf
 */
@Composable
fun AlphabetButton(
    letter: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSquare: Boolean = true // Flag baru untuk mengontrol rasio aspek
) {
    val finalModifier = if (isSquare) {
        // Untuk A-X, terapkan aspek rasio 1:1
        modifier.aspectRatio(1f)
    } else {
        // Untuk Y dan Z, gunakan modifier yang diberikan (yang sudah memiliki weight)
        modifier
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = SignLinkTeal,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = finalModifier
    ) {
        Text(
            text = letter.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
