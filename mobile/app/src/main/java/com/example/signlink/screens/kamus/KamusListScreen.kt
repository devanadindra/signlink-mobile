package com.example.signlink.screens.kamus

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.signlink.Destinations
import com.example.signlink.R
import com.example.signlink.viewmodel.KamusViewModel
import com.example.signlink.ui.theme.*
import com.example.signlink.components.DictionaryHeaderCard
import com.example.signlink.data.models.kamus.KamusData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KamusListScreen(
    letter: Char,
    viewModel: KamusViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val kamusList by viewModel.kamusList.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(letter) {
        viewModel.getKamus(context, letter.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Kamus Huruf: $letter", fontWeight = FontWeight.Bold, color = DarkText) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
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
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.signlink),
                contentDescription = "SignLink Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header Card
            DictionaryHeaderCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp),
                title = "Kamus Bahasa Isyarat BISINDO",
                description = "Kumpulan Bahasa Isyarat dan terjemahannya"
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(kamusList) { item ->
                        KamusListItem(
                            item = item,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

/**
 * Komponen Tombol Item Kamus dalam Grid
 */
@Composable
fun KamusListItem(item: KamusData, navController: NavController) {
    Button(
        onClick = {
            val artiEncoded = Uri.encode(item.arti.replace("_", " "))
            val videoEncoded = Uri.encode(item.url)
            navController.navigate("${Destinations.KAMUS_DETAIL_SCREEN}/$artiEncoded/$videoEncoded")
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
            text = item.arti.replace("_", " "),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

