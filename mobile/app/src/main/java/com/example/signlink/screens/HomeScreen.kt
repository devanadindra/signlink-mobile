package com.example.signlink.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.signlink.R
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import com.example.signlink.components.NavItem
import com.example.signlink.ui.theme.CardBackground
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.LightTealBackground
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.viewmodel.HomeViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.example.signlink.viewmodel.CustomerViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onKuisClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {},
    onHomeClicked: () -> Unit = {},
    onTTIClicked: () -> Unit = {},
    onLatihanClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
    customerViewModel: CustomerViewModel
) {

    val isLoading = viewModel.isLoading
    val context = LocalContext.current
    var userName by remember { mutableStateOf("Memuat...") }

    LaunchedEffect(Unit) {
        customerViewModel.getPersonal(context) { personal ->
            userName = personal?.name ?: "Guest"
        }
    }

    if (isLoading) {
        HomeScreenSkeleton(userName)
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
                containerColor = Color.Transparent,
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {

                        HeaderWithTranslatorSection(userName,onCameraClicked)

                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(32.dp))
                            QuickAccessSection(onKamusClicked, onVTTClicked, onKuisClicked, onTTIClicked, onLatihanClicked)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderWithTranslatorSection(userName: String, onCameraClicked: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(SignLinkTeal)
        )
        TopAppBar(
            title = {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "SignLink",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Selamat Datang $userName 👋🏻",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                val primaryColor = SignLinkTeal.copy(alpha = 0.1f)

                drawCircle(
                    color = primaryColor,
                    radius = size.minDimension * 0.4f,
                    center = Offset(x = size.width * 0.1f, y = size.height * 0.9f)
                )

                drawCircle(
                    color = primaryColor,
                    radius = size.minDimension * 0.2f,
                    center = Offset(x = size.width * 0.8f, y = size.height * 0.1f)
                )

                drawArc(
                    color = primaryColor,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(x = size.width * 0.7f, y = size.height * 0.5f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.4f, size.height * 0.5f),
                    style = Stroke(width = 4.dp.toPx())
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
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
}

@Composable
fun QuickAccessSection(
    onKamusClicked: () -> Unit,
    onVTTClicked: () -> Unit,
    onKuisClicked: () -> Unit,
    onTTIClicked: () -> Unit = {},
    onLatihanClicked: () -> Unit
) {
    val quickAccessItems = listOf(
        QuickAccessData(
            title = "Kamus BISINDO",
            icon = Icons.Default.Book,
            onClick = onKamusClicked
        ),
        QuickAccessData(
            title = "Teks ke Bahasa Isyarat",
            icon = R.drawable.logotti,
            onClick = onTTIClicked
        ),
        QuickAccessData(
            title = "Latihan Bahasa Isyarat",
            icon = R.drawable.orang2,
            onClick = onLatihanClicked
        ),
        QuickAccessData(
            title = "Kuis Singkat SignLink",
            icon = R.drawable.signlink_logo,
            onClick = onKuisClicked
        ),
        QuickAccessData(
            title = "Suara ke Tulisan",
            icon = Icons.Default.Mic,
            onClick = onVTTClicked
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        quickAccessItems.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    QuickAccessCard(
                        title = item.title,
                        icon = item.icon,
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        onClick = item.onClick
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(86.dp))
    }
}

private data class QuickAccessData(
    val title: String,
    val icon: Any?,
    val onClick: () -> Unit
)

/**
 * Komponen Card Akses Cepat Reusable (DIMODIFIKASI UNTUK TATA LETAK 2xN DAN TANPA SUBTITLE)
 */
@Composable
fun QuickAccessCard(
    title: String,
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
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ikon
            when (icon) {
                is ImageVector -> {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = SignLinkTeal,
                        modifier = Modifier.size(48.dp)
                    )
                }
                is Int -> {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        modifier = Modifier.size(48.dp)
                    )
                }
                else -> Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Judul (Title)
            Text(
                text = title,
                color = DarkText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}