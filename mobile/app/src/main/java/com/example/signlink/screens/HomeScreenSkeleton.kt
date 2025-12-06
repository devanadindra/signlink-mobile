package com.example.signlink.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.signlink.ui.theme.CardBackground
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.components.NavItem
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Komponen Utama Loading Skeleton untuk HomeScreen
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenSkeleton(userName: String) {
    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Place, true, "home"),
        NavItem("Kamus", Icons.Default.Place, false, "kamus"),
        NavItem("Penerjemah", Icons.Default.Place, false, "penerjemah"),
        NavItem("VTT", Icons.Default.Place, false, "vtt"),
        NavItem("Profil", Icons.Default.Place, false, "profil")
    )

    val scrollState = rememberScrollState()

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
            bottomBar = { SkeletonBottomBarSignLink(navItems) },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                SkeletonHeaderWithTranslatorSection(userName)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    SkeletonQuickAccessSection()

                    SkeletonQuizButton(modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * Efek Shimmer untuk Loading Skeleton
 */
@Composable
fun ShimmerAnimation(content: @Composable (Brush) -> Unit) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = "shimmerTranslateAnim"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value - 200, translateAnim.value - 200),
        end = Offset(translateAnim.value, translateAnim.value)
    )

    content(brush)
}

/**
 * Skeleton Card Reusable
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    brush: Brush
) {
    Spacer(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/**
 * Skeleton untuk Bottom Bar
 */
@Composable
fun SkeletonBottomBarSignLink(items: List<NavItem>) {
    ShimmerAnimation { brush ->
        BottomAppBar(
            containerColor = Color.White,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, _ ->
                    if (index != 2) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                        ) {
                            SkeletonCard(
                                modifier = Modifier.size(34.dp),
                                shape = RoundedCornerShape(12.dp),
                                brush = brush
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Skeleton untuk Header Aplikasi dan Tombol Penerjemah Utama
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkeletonHeaderWithTranslatorSection(userName: String) {
    ShimmerAnimation { brush ->
        Box(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
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
            SkeletonCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(180.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 80.dp),
                shape = RoundedCornerShape(16.dp),
                brush = brush
            )
        }
    }
    Spacer(modifier = Modifier.height(80.dp))
}

@Composable
fun SkeletonQuickAccessSection() {
    val totalItems = 4
    val itemIndices = (0 until totalItems).toList()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemIndices.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { _ ->
                    SkeletonQuickAccessCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SkeletonQuickAccessCard(
    modifier: Modifier
) {
    ShimmerAnimation { brush ->
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ikon Skeleton
                SkeletonCard(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    brush = brush
                )

                Spacer(modifier = Modifier.height(8.dp))

                SkeletonCard(
                    modifier = Modifier.fillMaxWidth(0.8f).height(13.dp),
                    brush = brush
                )
            }
        }
    }
}

@Composable
fun SkeletonQuizButton(modifier: Modifier = Modifier) {
    ShimmerAnimation { brush ->
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = modifier
                .height(120.dp)
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    SkeletonCard(
                        modifier = Modifier.width(180.dp).height(18.dp),
                        brush = brush
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SkeletonCard(
                        modifier = Modifier.width(200.dp).height(14.dp),
                        brush = brush
                    )
                }

                SkeletonCard(
                    modifier = Modifier.size(76.dp),
                    shape = RoundedCornerShape(28.dp),
                    brush = brush
                )
            }
        }
    }
}