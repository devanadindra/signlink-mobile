package com.example.signlink.screens.profile

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.signlink.Destinations
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import com.example.signlink.components.NavItem
import com.example.signlink.data.utils.AuthUtil
import com.example.signlink.ui.theme.*
import com.example.signlink.viewmodel.AuthViewModel
import com.example.signlink.viewmodel.CustomerViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    customerViewModel: CustomerViewModel,
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

    var userName by remember { mutableStateOf("Memuat...") }
    var hasPassword by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf("") }
    val imageUrl = AuthUtil.getProfile(context)
    var userGoogleID by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        customerViewModel.getPersonal(context) { personal ->
            if (personal != null) {
                userName = personal.name
                userEmail = personal.email
                userGoogleID = personal.googleId
                hasPassword = personal.hasPassword
            } else {
                userName = "Tidak diketahui"
                userEmail = "-"
            }
        }
    }

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
                name = userName,
                email = userEmail,
                profile = imageUrl.toString(),
                onEditProfileClicked = {
                    val dataJson = """
                        {
                            "name": "$userName",
                            "email": "$userEmail",
                            "profile": "$imageUrl",
                            "google_id": "$userGoogleID"
                        }
                    """.trimIndent()

                    val encoded = Uri.encode(dataJson)

                    navController.navigate("${Destinations.EDIT_PROFILE_SCREEN}/$encoded")
                }

            )

            ProfileSection(title = "Keamanan") {
                ProfileOptionItem(
                    text = if (hasPassword) "Ubah Kata Sandi" else "Atur Kata Sandi",
                    onClick = {
                        navController.navigate("${Destinations.CHANGE_PASSWORD_SCREEN}/$hasPassword")
                    }
                )

                ProfileOptionItem(
                    text = "Statistik Kuis Singkat SignLink",
                    onClick = { /* TODO: Navigasi Ubah Nomor HP */ }
                )

                ProfileOptionItem(
                    text = "Statistik Latihan Bahasa Isyarat",
                    onClick = { navController.navigate(Destinations.STATS_LATIHAN_SCREEN) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSection(title = "Pusat Bantuan") {
                ProfileOptionItem(
                    text = "Kebijakan Privasi",
                    onClick = { navController.navigate(Destinations.PRIVACY_POLICY_SCREEN) }
                )
                ProfileOptionItem(
                    text = "Bantuan SignLink",
                    onClick = { navController.navigate(Destinations.HELP_SCREEN) }
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
    email: String,
    onEditProfileClicked: () -> Unit,
    profile: String
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
            if (profile.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = SignLinkTeal,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                AsyncImage(
                    model = profile,
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

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
                text = email,
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
