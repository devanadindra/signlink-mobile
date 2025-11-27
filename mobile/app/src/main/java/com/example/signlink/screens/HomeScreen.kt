package com.example.signlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signlink.R
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import com.example.signlink.components.NavItem
import com.example.signlink.ui.theme.CardBackground
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.LightTealBackground
import com.example.signlink.ui.theme.SignLinkTeal
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.signlink.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onKuisClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {},
    onHomeClicked: () -> Unit = {},
    onLatihanClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {

    val isLoading = viewModel.isLoading

    if (isLoading) {
        HomeScreenSkeleton()
    } else {
        val navItems = listOf(
            NavItem("Beranda", Icons.Default.Home, true, "home"),
            NavItem("Kamus", Icons.Default.Book, false, "kamus"),
            NavItem("Penerjemah", Icons.Default.Camera, false, "penerjemah"),
            NavItem("VTT", Icons.Default.Mic, false, "vtt"),
            NavItem("Profil", Icons.Default.Person, false, "profil")
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SignLinkTeal,
                            Color.White
                        )
                    )
                )
        ) {
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
                containerColor = Color.Transparent
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White)
                ) {

                    HeaderWithTranslatorSection(onCameraClicked)

                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        QuickAccessSection(onKamusClicked, onVTTClicked, onKuisClicked, onLatihanClicked)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

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
 * Komponen Tombol Penerjemah Utama yang sekarang menerima Modifier.
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

@Composable
fun QuickAccessSection(
    onKamusClicked: () -> Unit,
    onVTTClicked: () -> Unit,
    onKuisClicked: () -> Unit,
    onLatihanClicked: () -> Unit
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

        Spacer(modifier = Modifier.height(16.dp))

        QuickAccessCard(
            title = "Latihan Bahasa Isyarat",
            subtitle = "Latih kemampuan bahasa isyarat Anda melalui praktik interaktif",
            icon = R.drawable.orang2,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            onClick = onLatihanClicked
        )

        Spacer(modifier = Modifier.height(16.dp))
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
