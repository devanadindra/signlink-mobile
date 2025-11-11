package com.example.signlink.screens.kamus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.signlink.R
import com.example.signlink.components.BottomBarSignLink
import com.example.signlink.components.MainFloatingActionButton
import com.example.signlink.components.NavItem
import com.example.signlink.ui.theme.*
import androidx.compose.ui.platform.LocalContext
import com.example.signlink.data.utils.AuthUtil.getRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KamusScreen(
    navController: NavController,
    onHomeClicked: () -> Unit = {},
    onKamusClicked: () -> Unit = {},
    onVTTClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {},
    onAddKamusClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    var userRole by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userRole = getRole(context)
    }

    val navItems = listOf(
        NavItem("Beranda", Icons.Default.Home, false, "home"),
        NavItem("Kamus", Icons.Default.Book, true, "kamus"),
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
        floatingActionButton = { MainFloatingActionButton(onClick = onCameraClicked) },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Color.White
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. Logo
            Image(
                painter = painterResource(id = R.drawable.signlink),
                contentDescription = "SignLink Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (userRole == "ADMIN") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAddKamusClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Kamus",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tambah Kata ke Kamus",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. Deskripsi Card untuk Kamus
            DictionaryHeaderCard(
                modifier = Modifier.fillMaxWidth(0.9f).padding(horizontal = 8.dp),
                title = "Kamus Bahasa Isyarat BISINDO",
                description = "Kumpulan Bahasa Isyarat dan terjemahannya"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Grid Alphabet
            AlphabetGrid(
                onLetterClick = { letter ->
                    navController.navigate("kamus_list/${letter}")
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Komponen Header Card khusus untuk Kamus
 */
@Composable
fun DictionaryHeaderCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {
            Text(
                text = title,
                color = DarkText,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Komponen Grid Tombol Alphabet (A-Z)
 */
@Composable
fun AlphabetGrid(
    onLetterClick: (Char) -> Unit
) {
    val alphabet = ('A'..'Z').toList()
    val columns = 4
    val itemsPerGroup = columns * 6

    val mainGridLetters = alphabet.take(itemsPerGroup)
    val remainingLetters = alphabet.drop(itemsPerGroup)

    Column(
        modifier = Modifier.fillMaxWidth(0.9f).padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        mainGridLetters.chunked(columns).forEach { rowLetters ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowLetters.forEach { letter ->
                    AlphabetButton(
                        letter = letter,
                        onClick = { onLetterClick(letter) },
                        modifier = Modifier.weight(1f),
                        isSquare = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            remainingLetters.forEach { letter ->
                AlphabetButton(
                    letter = letter,
                    onClick = { onLetterClick(letter) },
                    modifier = Modifier.weight(1f).height(60.dp),
                    isSquare = false
                )
            }
        }
    }
}

/**
 * Komponen Tombol Tunggal untuk setiap huruf
 */
@Composable
fun AlphabetButton(
    letter: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSquare: Boolean = true
) {
    val finalModifier = if (isSquare) {
        modifier.aspectRatio(1f)
    } else {
        modifier
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = SignLinkTeal,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = finalModifier
    ) {
        Text(
            text = letter.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}