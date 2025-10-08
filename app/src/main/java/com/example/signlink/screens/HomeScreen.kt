package com.example.signlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R // Asumsi Resource R ada untuk logo kuis
import com.example.signlink.ui.theme.SignLinkTeal // Menggunakan warna dari theme Anda

// Definisi warna tambahan
val LightTealBackground = Color(0xFFE0F7FA) // Warna latar belakang pucat untuk area atas/kartu
val CardBackground = Color.White
val DarkText = Color(0xFF1E1E1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() { // Saya biarkan namanya tetap HomeScreen sesuai file yang Anda berikan
    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Home, true),
        NavItem("Kamus", Icons.Default.Book, false),
        NavItem("Penerjemah", Icons.Default.Camera, false),
        NavItem("VTT", Icons.Default.Mic, false),
        NavItem("Profil", Icons.Default.Person, false)
    )

    Scaffold(
        bottomBar = { BottomBarSignLink(navItems) },
        floatingActionButton = { MainFloatingActionButton() },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {

            HeaderWithTranslatorSection()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // Mengurangi padding horizontal agar sesuai dengan QuickAccessSection sebelumnya
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp)) // Jarak antara Tombol Kamera dan QuickAccessSection
                QuickAccessSection()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Komponen Gabungan: Header Aplikasi (TopBar) dan Kartu Tombol Penerjemah Utama (MainTranslatorButton).
 * Menggunakan Box untuk menumpuk elemen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderWithTranslatorSection() {
    // Gunakan Box untuk menumpuk: Latar belakang Kurva, TopAppBar, dan Tombol Kamera
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        // 1. Area Latar Belakang Teal (Kurva utama yang menjadi latar TopBar dan Kartu)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Ketinggian kurva
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(SignLinkTeal)
        )

        // 2. Top App Bar (Header Aplikasi)
        TopAppBar(
            title = {
                Text(
                    text = "SignLink",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent), // Jadikan transparan
            actions = {
                IconButton(onClick = { /* Aksi menu */ }) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            },
            // Atur agar TopAppBar berada di paling atas Box
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        )

        // 3. Tombol Penerjemah Utama (diletakkan di bagian bawah Box, tertanam di kurva)
        MainTranslatorButton(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Biarkan sedikit padding di sisi
                .align(Alignment.BottomCenter) // Posisikan di bagian bawah Box
                .offset(y = 80.dp) // DIPERBAIKI: Mengembalikan offset menjadi 50.dp dan Spacer juga 50.dp agar tata letak tidak terlalu longgar
        )
    }
    // DIPERBAIKI: Mengembalikan Spacer menjadi 50.dp agar menyeimbangkan offset Tombol Kamera
    Spacer(modifier = Modifier.height(80.dp))
}

/**
 * Komponen Tombol Penerjemah Utama (Hero Section) yang sekarang menerima Modifier.
 */
@Composable
fun MainTranslatorButton(modifier: Modifier = Modifier) {
    // Kartu luar yang besar dengan background light teal
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightTealBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .height(180.dp)
            .padding(vertical = 8.dp)
            .clickable { /* Aksi: Mulai terjemahan kamera */ }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp).fillMaxSize()
        ) {
            // Teks tombol di atas
            Text(
                text = "Mulai Terjemahkan",
                color = SignLinkTeal,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Ikon Kamera besar di tengah kartu
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = "Kamera Penerjemah",
                tint = SignLinkTeal,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

// --- Komponen Lain Tetap Sama (Kecuali TopBarSignLink Dihapus) ---

/**
 * Komponen Bagian Akses Cepat (Quick Access)
 * DIPERBAIKI: Mengubah tata letak agar semua kartu berada dalam satu kolom vertikal.
 */
@Composable
fun QuickAccessSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. Kartu Kamus Sibi (Baru, menggunakan fillMaxWidth)
        QuickAccessCard(
            title = "Kamus Sibi",
            subtitle = "Kumpulan Bahasa Isyarat dan terjemahannya",
            icon = Icons.Default.Book,
            modifier = Modifier.fillMaxWidth().height(120.dp) // Menghapus padding horizontal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Kartu Suara ke Tulisan (Baru, menggunakan fillMaxWidth)
        QuickAccessCard(
            title = "Suara ke Tulisan",
            subtitle = "Rekam suara untuk mengubah ke tulisan",
            icon = Icons.Default.Mic,
            modifier = Modifier.fillMaxWidth().height(120.dp) // Menghapus padding horizontal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Kartu Kuis
        QuickAccessCard(
            title = "Kuis Singkat SignLink",
            subtitle = "Kerjakan Kuis singkat untuk melatih kemampuan bahasa isyarat anda",
            icon = R.drawable.signlink_logo, // Saya asumsikan nama resource yang benar adalah signlink
            modifier = Modifier.fillMaxWidth().height(120.dp),
        )
    }
}

/**
 * Komponen Card Akses Cepat Reusable
 * DIPERBAIKI: Mengubah tipe parameter 'icon' menjadi Any? dan menambahkan logika 'when' untuk menangani ImageVector dan Int (Drawable ID).
 * TATA LETAK DIPERBAIKI: Ikon di kiri, seluruh teks di kanan, dan menghapus ikon Star tambahan di paling kanan.
 */
@Composable
fun QuickAccessCard(
    title: String,
    subtitle: String,
    icon: Any?, // DIPERBAIKI: Mengubah tipe data menjadi Any?
    modifier: Modifier,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.clickable { /* Aksi kartu */ }
    ) {
        // DIPERBAIKI: Menggunakan Row untuk ikon/gambar (kiri) dan Column teks (kanan)
        Row(
            modifier = Modifier.padding(16.dp).fillMaxSize(), // Menambah padding card keseluruhan
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // Mulai dari kiri
        ) {

            // 1. LOGIKA UNTUK MENAMPILKAN ICON ATAU IMAGE (Sisi Kiri)
            when (icon) {
                is ImageVector -> {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = SignLinkTeal,
                        modifier = Modifier.size(56.dp) // DIPERBESAR: Dari 40.dp menjadi 56.dp
                    )
                }
                is Int -> {
                    // Menggunakan Image untuk Drawable ID
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        modifier = Modifier.size(56.dp) // DIPERBESAR: Dari 40.dp menjadi 56.dp
                    )
                }
                else -> Spacer(modifier = Modifier.size(56.dp)) // DIPERBESAR: Dari 40.dp menjadi 56.dp
            }

            // Spacer antara ikon dan teks
            Spacer(modifier = Modifier.width(12.dp))

            // 2. KOLOM TEKS (Sisi Kanan)
            Column(
                modifier = Modifier.weight(1f) // Memberikan sisa ruang horizontal untuk teks
            ) {
                Text(
                    text = title,
                    color = DarkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    // Menghapus padding-top/bottom yang tidak perlu di sini
                )
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 12.sp
                )
            }
            // DIPERBAIKI: Menghapus ikon Icons.Default.Star tambahan di paling kanan
        }
    }
}

/**
 * Komponen Floating Action Button utama (Kamera)
 */
@Composable
fun MainFloatingActionButton() {
    FloatingActionButton(
        onClick = { /* Aksi: Kamera utama */ },
        containerColor = SignLinkTeal,
        shape = RoundedCornerShape(50),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
        modifier = Modifier.offset(y = 52.dp)
    ) {
        Icon(
            Icons.Default.PhotoCamera,
            contentDescription = "Kamera",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}

/**
 * Data class untuk item navigasi bawah
 */
data class NavItem(val label: String, val icon: ImageVector, val isSelected: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarSignLink(items: List<NavItem>) {
    BottomAppBar(
        containerColor = Color.White,
        modifier = Modifier.height(72.dp)
    ) {
        val navItemsFiltered = items.filter { it.icon != Icons.Default.Camera }

        navItemsFiltered.forEachIndexed { index, item ->
            // Logika untuk menempatkan spacer di tengah (setelah item kedua dari total 4 item yang difilter)
            if (index == 2) {
                Spacer(Modifier.weight(1f))
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (item.isSelected) SignLinkTeal else Color.Gray
                    )
                },
                selected = item.isSelected,
                onClick = { /* Aksi navigasi */ },
                label = null
            )
        }
    }
}
