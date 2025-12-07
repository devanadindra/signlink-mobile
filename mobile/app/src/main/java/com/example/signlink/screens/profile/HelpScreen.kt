package com.example.signlink.screens.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.LightGrayBackground
import com.example.signlink.ui.theme.SignLinkTeal

/**
 * Halaman Bantuan SignLink (FAQ & Kontak Dukungan)
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bantuan SignLink") },
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
                    .background(LightGrayBackground)
                    .verticalScroll(rememberScrollState())
            ) {
                // Section 1: FAQ
                HelpSection(title = "Pertanyaan Umum (FAQ)") {
                    FaqItem(
                        question = "Bagaimana cara kerja fitur Penerjemah?",
                        answer = "Fitur Penerjemah menggunakan model Machine Learning yang telah dilatih dengan data **Bahasa Isyarat Indonesia (BISINDO)** untuk mengidentifikasi gerakan tangan Anda melalui kamera dan mengubahnya menjadi teks."
                    )
                    HorizontalDivider(color = LightGrayBackground, thickness = 1.dp)
                    FaqItem(
                        question = "Apakah SignLink berfokus pada BISINDO?",
                        answer = "Ya, SignLink berfokus pada pengenalan isyarat berdasarkan **BISINDO**. Kami terus berupaya memperkaya basis data kami untuk mencakup variasi isyarat regional BISINDO lainnya."
                    )
                    HorizontalDivider(color = LightGrayBackground, thickness = 1.dp)
                    FaqItem(
                        question = "Mengapa terjemahan saya kadang tidak akurat?",
                        answer = "Akurasi terjemahan sangat dipengaruhi oleh pencahayaan, latar belakang, dan kejelasan gerakan tangan Anda. Pastikan Anda berada di area yang terang dan gerakan isyarat dilakukan dengan jelas dan lengkap."
                    )
                    HorizontalDivider(color = LightGrayBackground, thickness = 1.dp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Kontak Dukungan
                HelpSection(title = "Dukungan dan Informasi") {
                    SupportItem(
                        icon = Icons.Default.Email,
                        text = "Kirim Email ke Dukungan",
                        subtext = "signlink.app@support.com",
                        onClick = {
                            // TODO: Implementasi Intent untuk membuka aplikasi email
                        }
                    )
                    HorizontalDivider(color = LightGrayBackground, thickness = 1.dp)
                    SupportItem(
                        icon = Icons.Default.Info,
                        text = "Tentang Aplikasi SignLink",
                        subtext = "Versi 1.0.0",
                        onClick = {
                            // TODO: Navigasi ke halaman Tentang Kami jika ada
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
}

/**
 * Komponen untuk menampung bagian Bantuan (FAQ/Dukungan)
 */
@Composable
fun HelpSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(Color.White)
    ) {
        // Judul Bagian
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = LightGrayBackground)

        content()
    }
}

/**
 * Komponen FAQ yang dapat diperluas
 */
@Composable
fun FaqItem(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { isExpanded = !isExpanded }
        .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = question,
                fontSize = 16.sp,
                color = DarkText,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (isExpanded) "Tutup" else "Buka",
                tint = Color.Gray
            )
        }

        if (isExpanded) {
            Text(
                text = answer,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

/**
 * Komponen Item Kontak Dukungan
 */
@Composable
fun SupportItem(
    icon: ImageVector,
    text: String,
    subtext: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SignLinkTeal,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = DarkText,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = subtext,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Next",
            tint = Color.Gray
        )
    }
}