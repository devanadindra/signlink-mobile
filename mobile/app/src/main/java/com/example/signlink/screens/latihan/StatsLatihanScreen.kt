package com.example.signlink.screens.latihan

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.signlink.components.DescriptionCard
import com.example.signlink.data.models.latihan.StatsLatihanByUserIdRes
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.viewmodel.LatihanViewModel

// Asumsi LatihanViewModel memiliki fungsi getStatsLatihanByUserId
// dan koleksi statsLatihanByuserIdList: StateFlow<List<StatsLatihanByUserIdRes>>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsLatihanScreen(
    viewModel: LatihanViewModel = hiltViewModel(),
    navController: NavController,
) {
    val context = LocalContext.current

    val statsLatihanList by viewModel.statsLatihanByuserIdList.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Hitung rata-rata kecepatan pengerjaan
    val totalScore = statsLatihanList.sumOf { it.score.toDouble() }
    val totalModules = statsLatihanList.size
    val averageSpeed = if (totalModules > 0) (totalScore / totalModules).toFloat() else 0f

    LaunchedEffect(Unit) {
        viewModel.getStatsLatihanByUserId(context)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            if (error.isNotBlank()) {
                Toast.makeText(context, "Gagal memuat data statistik: $error", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                    text = "Statistik Latihan",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                    }
                },
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

            // Header Card
            DescriptionCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp),
                description = "Lihat riwayat dan performa Anda pada setiap modul latihan."
            )

            // --- Row Card Ringkasan Statistik ---
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Jumlah Modul Selesai
                SummaryStatsCard(
                    totalCompletedModules = totalModules,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 2. Rata-Rata Kecepatan
                AverageSpeedCard(
                    averageSpeed = averageSpeed,
                    modifier = Modifier.weight(1f)
                )
            }
            // ------------------------------------

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (statsLatihanList.isEmpty()) {
                Text(
                    text = "Belum ada riwayat latihan yang diselesaikan.",
                    modifier = Modifier.padding(32.dp),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        statsLatihanList,
                        key = { it.id }
                    ) { item ->
                        StatsLatihanCard(
                            stats = item,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Komponen Card untuk menampilkan Ringkasan Statistik (Jumlah Modul Selesai).
 */
@Composable
fun SummaryStatsCard(
    totalCompletedModules: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4DB6AC)),
        modifier = modifier
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon
            Icon(
                imageVector = Icons.Default.AssignmentTurnedIn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Detail
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Total Modul Selesai",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp, // Dikecilkan agar cukup
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$totalCompletedModules",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

/**
 * Komponen Card untuk menampilkan Rata-Rata Kecepatan.
 */
@SuppressLint("DefaultLocale")
@Composable
fun AverageSpeedCard(
    averageSpeed: Float,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784)), // Warna berbeda (Hijau Muda)
        modifier = modifier
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Rata-Rata Kecepatan",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    // Format angka dengan koma
                    text = "%.1f".format(averageSpeed).replace('.', ','),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "detik/soal",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

/**
 * Komponen Card untuk menampilkan Statistik Latihan per modul.
 */
@SuppressLint("DefaultLocale")
@Composable
fun StatsLatihanCard(
    stats: StatsLatihanByUserIdRes,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SignLinkTeal),
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Ikon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AssignmentTurnedIn, // Ikon untuk statistik/hasil
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Detail Nama Latihan & Total Soal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stats.latihanName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.ListAlt,
                        contentDescription = "Total Soal",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Total Soal: ${stats.totalSoal}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            // Kecepatan Rata-rata Modul
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Kecepatan Rata-rata",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        // Menggunakan replace('.', ',') untuk format Indonesia
                        text = "%.1f".format(stats.score).replace('.', ','),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "detik/soal",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        }
    }
}