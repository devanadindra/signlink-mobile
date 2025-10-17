package com.example.signlink.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.signlink.ui.theme.SignLinkTeal // Menggunakan warna dari theme Anda

/**
 * Data class untuk item navigasi bawah.
 */
data class NavItem(val label: String, val icon: ImageVector, val isSelected: Boolean, val tag: String)

/**
 * Komponen Floating Action Button utama (Kamera) untuk navigasi cepat.
 */
@Composable
fun MainFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
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
 * Komponen Bottom Navigation Bar yang digunakan di seluruh aplikasi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarSignLink(
    items: List<NavItem>,
    onHomeClicked: () -> Unit,
    onKamusClicked: () -> Unit,
    onVTTClicked: () -> Unit,
    onProfileClicked: () -> Unit
) {
    // Mengatur warna indikator menjadi transparan untuk menghilangkan background saat terpilih
    val navItemColors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        selectedIconColor = SignLinkTeal,
        unselectedIconColor = Color.Gray
    )

    BottomAppBar(
        containerColor = Color.White,
        modifier = Modifier.height(72.dp)
    ) {
        // Filter item yang bukan 'Penerjemah' karena itu diwakili oleh FAB
        val navItemsFiltered = items.filter { it.icon != Icons.Default.Camera }

        navItemsFiltered.forEachIndexed { index, item ->
            // Menempatkan spacer di tengah (setelah item kedua) untuk menyeimbangkan FAB
            if (index == 2) {
                Spacer(Modifier.weight(1f))
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                    )
                },
                selected = item.isSelected,
                onClick = {
                    when (item.tag) {
                        "home" -> onHomeClicked()
                        "kamus" -> onKamusClicked()
                        "vtt" -> onVTTClicked()
                        "profil" -> onProfileClicked()
                    }
                },
                label = null,
                colors = navItemColors
            )
        }
    }
}
