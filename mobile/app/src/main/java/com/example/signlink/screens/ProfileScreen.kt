package com.example.signlink.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Tambahkan import NavController jika belum ada
import androidx.navigation.NavController

import com.example.signlink.Destinations
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import com.example.signlink.components.NavItem
import com.example.signlink.ui.theme.*
import com.example.signlink.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    // FIX: Tambahkan NavController sebagai parameter wajib untuk navigasi
    navController: NavController,
    viewModel: AuthViewModel,
    onHomeClicked: () -> Unit = {},
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {}
) {
    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Home, false, "home"),
        NavItem("Kamus", Icons.Default.Book, false, "kamus"),
        NavItem("Penerjemah", Icons.Default.Camera, false, "penerjemah"),
        NavItem("VTT", Icons.Default.Mic, false, "vtt"),
        NavItem("Profil", Icons.Default.Person, true, "profil")
    )

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


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
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            ProfileHeader(
                name = "User",
                phone = "+6285935294045",
                onEditProfileClicked = { /* TODO: Navigasi ke Edit Profil */ }
            )

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
            HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = LightGrayBackground)

            ProfileOptionItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                text = "Keluar",
                onClick = {
                    viewModel.logout(context) { isSuccess ->
                        if (isSuccess) {
                            navController.navigate(Destinations.OPENING_SCREEN) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Gagal keluar. Silakan coba lagi.",
                                    actionLabel = "Tutup",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                },
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
        HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = LightGrayBackground)

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
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = Color.Gray
                )
            }
        }
        if (text != "Bantuan SignLink" && text != "Keluar" && contentColor != DangerRed) {
            HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = LightGrayBackground)
        }
    }
}
