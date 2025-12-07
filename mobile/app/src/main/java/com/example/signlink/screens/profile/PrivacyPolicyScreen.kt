package com.example.signlink.screens.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.signlink.ui.theme.DarkText

/**
 * Halaman Kebijakan dan Privasi (Privacy Policy)
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kebijakan Privasi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkText
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                PrivacyPolicyContent()
            }
        }
    )
}

@Composable
fun PrivacyPolicyContent() {
    val titleModifier = Modifier.padding(top = 16.dp, bottom = 4.dp)

    Text(
        text = "Kebijakan Privasi SignLink",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = DarkText,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Text(
        text = "Efektif sejak 10 Desember 2025",
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray
    )

    // --- Pengantar ---
    Text(
        text = "Pendahuluan",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DarkText,
        modifier = titleModifier
    )
    Text(
        text = "SignLink ('Kami') berkomitmen untuk melindungi privasi Anda. Kebijakan Privasi ini menjelaskan bagaimana Kami mengumpulkan, menggunakan, mengungkapkan, dan melindungi informasi Anda saat Anda menggunakan aplikasi SignLink Kami. Dengan menggunakan aplikasi Kami, Anda menyetujui praktik yang dijelaskan dalam Kebijakan Privasi ini.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkText
    )

    // --- Informasi yang Kami Kumpulkan ---
    Text(
        text = "Informasi yang Kami Kumpulkan",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DarkText,
        modifier = titleModifier
    )

    // 1. Informasi yang Anda Berikan
    Text(
        text = "1. Informasi yang Anda Berikan",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = DarkText,
        modifier = Modifier.padding(top = 8.dp)
    )
    BulletPointText(text = "Informasi Profil: Nama, alamat email, dan kata sandi (dihash) saat Anda mendaftar atau mengedit profil.")
    BulletPointText(text = "Data Penggunaan: Video/gambar yang Anda unggah ke fitur penerjemah atau latihan, yang digunakan untuk memproses permintaan terjemahan dan meningkatkan akurasi model.")

    // 2. Informasi yang Dikumpulkan Otomatis
    Text(
        text = "2. Informasi yang Dikumpulkan Otomatis",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = DarkText,
        modifier = Modifier.padding(top = 8.dp)
    )
    BulletPointText(text = "Informasi Perangkat: Jenis perangkat, sistem operasi, pengenal perangkat unik.")
    BulletPointText(text = "Data Log: Informasi tentang interaksi Anda dengan aplikasi, seperti fitur yang diakses, waktu dan durasi penggunaan.")

    // --- Penggunaan Informasi Anda ---
    Text(
        text = "Penggunaan Informasi Anda",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DarkText,
        modifier = titleModifier
    )
    BulletPointText(text = "Untuk menyediakan dan memelihara aplikasi Kami, termasuk fitur Penerjemah Video Teks (VTT) dan Latihan Bahasa Isyarat.")
    BulletPointText(text = "Untuk mempersonalisasi pengalaman Anda dan meningkatkan layanan Kami.")
    BulletPointText(text = "Untuk berkomunikasi dengan Anda mengenai pembaruan atau masalah teknis.")
    BulletPointText(text = "Untuk tujuan analisis dan penelitian, demi meningkatkan model pemrosesan bahasa isyarat.")

    // --- Keamanan Informasi ---
    Text(
        text = "Keamanan Informasi",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DarkText,
        modifier = titleModifier
    )
    Text(
        text = "Kami menerapkan langkah-langkah keamanan teknis dan organisasi yang wajar untuk melindungi informasi pribadi Anda dari akses, penggunaan, atau pengungkapan yang tidak sah. Meskipun Kami berusaha untuk melindungi informasi Anda, tidak ada sistem keamanan yang sempurna, dan Kami tidak dapat menjamin keamanan mutlak data Anda.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkText
    )

    // --- Perubahan Kebijakan Ini ---
    Text(
        text = "Perubahan pada Kebijakan Privasi Ini",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DarkText,
        modifier = titleModifier
    )
    Text(
        text = "Kami dapat memperbarui Kebijakan Privasi Kami dari waktu ke waktu. Kami akan memberitahukan Anda tentang setiap perubahan dengan memposting Kebijakan Privasi baru di halaman ini. Anda disarankan untuk meninjau Kebijakan Privasi ini secara berkala untuk setiap perubahan.",
        style = MaterialTheme.typography.bodyMedium,
        color = DarkText
    )

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun BulletPointText(text: String) {
    Row(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkText,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkText
        )
    }
}