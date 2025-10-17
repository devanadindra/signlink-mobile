package com.example.signlink.screens.kamus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.signlink.R
import com.example.signlink.components.DictionaryHeaderCard
import com.example.signlink.components.VideoPlayer
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.SignLinkTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KamusDetailScreen(
    navController: NavController,
    arti: String,
    videoUrl: String? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = arti, fontWeight = FontWeight.Bold, color = DarkText) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
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

            // 2. Header Card
            DictionaryHeaderCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp),
                title = "Kamus Bahasa Isyarat SIBI",
                description = "Kumpulan Bahasa Isyarat dan terjemahannya"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Konten Detail (Video dan Tombol)
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    VideoPlayer(
                        videoUrl = videoUrl ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )


                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            println("Detail untuk kata: $arti")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SignLinkTeal,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Text(
                            text = arti,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
