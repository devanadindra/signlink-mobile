package com.example.signlink.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import com.example.signlink.components.NavItem
import com.example.signlink.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onHomeClicked: () -> Unit = {},
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {}
) {
    // Menetapkan Profil sebagai item navigasi aktif
    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Home, false, "home"),
        NavItem("Kamus", Icons.Default.Book, false, "kamus"),
        NavItem("Penerjemah", Icons.Default.Camera, false, "penerjemah"),
        NavItem("VTT", Icons.Default.Mic, false, "vtt"),
        NavItem("Profil", Icons.Default.Person, true, "profil")
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header: Detail Akun
            ProfileHeader(
                name = "User",
                phone = "+6285935294045",
                onEditProfileClicked = { /* TODO: Navigasi ke Edit Profil */ }
            )

            // --- Bagian Keamanan ---
            ProfileSection(title = "Keamanan") {
                ProfileOptionItem(
                    text = "Ubah Password",
                    onClick = { /* TODO: Navigasi Ubah Password */ }
                )
                ProfileOptionItem(
                    text = "Ubah Nomor Handphone",
                    onClick = { /* TODO: Navigasi Ubah Nomor HP */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Bagian Pusat Bantuan ---
            ProfileSection(title = "Pusat Bantuan") {
                ProfileOptionItem(
                    text = "Kebijakan Privasi",
                    onClick = { /* TODO: Navigasi Kebijakan Privasi */ }
                )
                ProfileOptionItem(
                    text = "Bantuan SignLink",
                    onClick = { /* TODO: Navigasi Bantuan */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = LightGrayBackground, thickness = 1.dp)

            // --- Bagian Keluar ---
            ProfileOptionItem(
                icon = Icons.Default.ExitToApp,
                text = "Keluar",
                onClick = { /* TODO: Logout User */ },
                contentColor = DarkText,
                showTrailingIcon = false // Menghilangkan panah di kanan
            )

            Divider(color = LightGrayBackground, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // --- Bagian Hapus Akun ---
            ProfileOptionItem(
                icon = Icons.Default.Delete,
                text = "Hapus Akun",
                onClick = { /* TODO: Tampilkan konfirmasi dialog Hapus Akun */ },
                contentColor = DangerRed,
                showTrailingIcon = false
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Komponen Header Profil (Foto, Nama, Nomor HP, Edit Profil)
 */
@Composable
fun ProfileHeader(
    name: String,
    phone: String,
    onEditProfileClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ikon Profil Besar
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(SignLinkTeal.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Icon",
                tint = SignLinkTeal,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Detail Nama dan Nomor HP
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )
            Text(
                text = phone,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Tombol Edit Profil
        Text(
            text = "Edit Profil",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = SignLinkTeal,
            modifier = Modifier.clickable(onClick = onEditProfileClicked)
        )
    }
}

/**
 * Komponen Bagian Profil dengan Judul (misalnya: Keamanan, Pusat Bantuan)
 */
@Composable
fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        // Judul Bagian
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = LightGrayBackground, thickness = 1.dp)

        // Konten Bagian (Opsi-opsi)
        content()
    }
}

/**
 * Komponen Item Opsi Profil yang dapat diklik
 */
@Composable
fun ProfileOptionItem(
    icon: ImageVector? = null,
    text: String,
    onClick: () -> Unit,
    contentColor: Color = DarkText,
    showTrailingIcon: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp).padding(end = 8.dp)
                    )
                }
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = contentColor,
                    fontWeight = if (contentColor == DangerRed) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            if (showTrailingIcon) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = Color.Gray
                )
            }
        }
        // Pemisah hanya di bawah item kecuali yang terakhir di setiap bagian
        if (text != "Bantuan SignLink" && text != "Keluar" && contentColor != DangerRed) {
            Divider(color = LightGrayBackground, thickness = 1.dp)
        }
    }
}

// Preview untuk memvisualisasikan layar
@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    // Membungkus dalam MaterialTheme jika ini adalah file standalone
    ProfileScreen()
}
