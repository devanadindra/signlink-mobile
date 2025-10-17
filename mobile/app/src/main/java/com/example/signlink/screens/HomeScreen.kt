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
import com.example.signlink.R
import com.example.signlink.ui.theme.*
import com.example.signlink.components.NavItem
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onKuisClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {},
    onHomeClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {}
) {
    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Home, true, "home"),
        NavItem("Kamus", Icons.Default.Book, false, "kamus"),
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
        floatingActionButton = { MainFloatingActionButton(onCameraClicked) },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = SignLinkTeal
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {

            HeaderWithTranslatorSection(onCameraClicked)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                QuickAccessSection(onKamusClicked, onVTTClicked, onKuisClicked)
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
fun HeaderWithTranslatorSection(onCameraClicked: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(SignLinkTeal)
        )
        TopAppBar(
            title = {
                Text(
                    text = "SignLink",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        )

        MainTranslatorButton(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.BottomCenter)
                .offset(y = 80.dp),
            onClick = onCameraClicked
        )
    }
    Spacer(modifier = Modifier.height(80.dp))
}

/**
 * Komponen Tombol Penerjemah Utama (Hero Section) yang sekarang menerima Modifier.
 */
@Composable
fun MainTranslatorButton(modifier: Modifier = Modifier, onClick: () -> Unit) {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightTealBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .height(180.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp).fillMaxSize()
        ) {
            Text(
                text = "Mulai Terjemahkan",
                color = SignLinkTeal,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = "Kamera Penerjemah",
                tint = SignLinkTeal,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

/**
 * Komponen Bagian Akses Cepat (Quick Access)
 */
@Composable
fun QuickAccessSection(
    onKamusClicked: () -> Unit,
    onVTTClicked: () -> Unit,
    onKuisClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        QuickAccessCard(
            title = "Kamus Sibi",
            subtitle = "Kumpulan Bahasa Isyarat dan terjemahannya",
            icon = Icons.Default.Book,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            onClick = onKamusClicked
        )

        Spacer(modifier = Modifier.height(16.dp))

        QuickAccessCard(
            title = "Suara ke Tulisan",
            subtitle = "Rekam suara untuk mengubah ke tulisan",
            icon = Icons.Default.Mic,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            onClick = onVTTClicked
        )

        Spacer(modifier = Modifier.height(16.dp))

        QuickAccessCard(
            title = "Kuis Singkat SignLink",
            subtitle = "Kerjakan Kuis singkat untuk melatih kemampuan bahasa isyarat anda",
            icon = R.drawable.signlink_logo,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            onClick = onKuisClicked
        )
    }
}

/**
 * Komponen Card Akses Cepat Reusable
 */
@Composable
fun QuickAccessCard(
    title: String,
    subtitle: String,
    icon: Any?,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            when (icon) {
                is ImageVector -> {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = SignLinkTeal,
                        modifier = Modifier.size(56.dp)
                    )
                }
                is Int -> {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        modifier = Modifier.size(56.dp)
                    )
                }
                else -> Spacer(modifier = Modifier.size(56.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = DarkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 12.sp
                )
            }
        }
    }
}
