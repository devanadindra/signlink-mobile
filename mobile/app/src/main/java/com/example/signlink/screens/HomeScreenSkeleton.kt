package com.example.signlink.screens

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

/**
 * Komponen Utama Loading Skeleton untuk HomeScreen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenSkeleton() {
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
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .verticalScroll(scrollState)
            ) {
                SkeletonHeaderWithTranslatorSection()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    SkeletonQuickAccessSection()
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
                                modifier = Modifier.size(24.dp),
                                shape = RoundedCornerShape(12.dp),
                                brush = brush
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            SkeletonCard(
                                modifier = Modifier.width(40.dp).height(8.dp),
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
fun SkeletonHeaderWithTranslatorSection() {
    ShimmerAnimation { brush ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(SignLinkTeal)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonCard(
                    modifier = Modifier.width(80.dp).height(20.dp),
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color.White.copy(alpha = 0.8f))
                    )
                )
                SkeletonCard(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(12.dp),
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color.White.copy(alpha = 0.8f))
                    )
                )
            }

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
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SkeletonQuickAccessCard(modifier = Modifier.fillMaxWidth().height(120.dp))
        Spacer(modifier = Modifier.height(16.dp))
        SkeletonQuickAccessCard(modifier = Modifier.fillMaxWidth().height(120.dp))
        Spacer(modifier = Modifier.height(16.dp))
        SkeletonQuickAccessCard(modifier = Modifier.fillMaxWidth().height(120.dp))
        Spacer(modifier = Modifier.height(16.dp))
        SkeletonQuickAccessCard(modifier = Modifier.fillMaxWidth().height(120.dp))
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
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                SkeletonCard(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    brush = brush
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    SkeletonCard(
                        modifier = Modifier.width(100.dp).height(14.dp),
                        brush = brush
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SkeletonCard(
                        modifier = Modifier.fillMaxWidth(0.8f).height(10.dp),
                        brush = brush
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SkeletonCard(
                        modifier = Modifier.fillMaxWidth(0.6f).height(10.dp),
                        brush = brush
                    )
                }
            }
        }
    }
}