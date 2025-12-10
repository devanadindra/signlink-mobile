package com.example.signlink.screens.latihan

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.signlink.R
import com.example.signlink.components.DictionaryHeaderCard
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.Destinations
import com.example.signlink.data.models.latihan.LatihanData
import com.example.signlink.data.utils.AuthUtil.getRole
import com.example.signlink.viewmodel.LatihanViewModel
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LatihanScreen(
    viewModel: LatihanViewModel = hiltViewModel(),
    navController: NavController,
    onAddLatihanClicked: () -> Unit = {}
) {
    val context = LocalContext.current

    var userRole by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userRole = getRole(context)
    }

    val currentRole = userRole ?: "CUSTOMER"

    val latihanList by viewModel.latihanList.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var currentPage by remember { mutableIntStateOf(1) }
    var hasNextPage by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()

    val pageSize = 5

    LaunchedEffect(currentPage) {
        listState.scrollToItem(0)

        viewModel.getAllLatihan(context, currentPage, pageSize)
    }

    LaunchedEffect(latihanList, isLoading) {
        if (!isLoading && latihanList.size < pageSize) {
            hasNextPage = false
        } else if (!isLoading && latihanList.size == pageSize) {
            hasNextPage = true
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { success ->
            if (success.isNotBlank() && currentRole == "ADMIN") {
                Toast.makeText(context, "Berhasil: $success", Toast.LENGTH_SHORT).show()
                viewModel.clearSuccess()
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            if (error.isNotBlank()) {
                Toast.makeText(context, "Gagal: $error", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.signlink),
                contentDescription = "SignLink Logo",
                modifier = Modifier
                    .size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (userRole == "ADMIN") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAddLatihanClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Latihan",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tambah Modul Latihan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Header Card
            DictionaryHeaderCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 8.dp),
                title = "Latihan Bahasa Isyarat",
                description = "Latih kemampuan bahasa isyarat Anda melalui praktik interaktif (Halaman $currentPage)"
            )
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 0.dp)
            ) {
                var previousIsDone = true
                items(
                    latihanList.size,
                    key = { index -> latihanList[index].id }
                ) { index ->
                    val item = latihanList[index]

                    val isLocked = if (index == 0) {
                        false
                    } else {
                        !previousIsDone
                    }

                    LatihanModulCard(
                        navController = navController,
                        modul = item,
                        userRole = currentRole,
                        isLocked = isLocked,
                        onClick = {
                            if (!isLocked || item.isDone) {
                                navController.navigate("${Destinations.LATIHAN_DETAIL_SCREEN}/${item.id}")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Selesaikan modul sebelumnya untuk membuka ini.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onDeleteClicked = {
                            viewModel.deleteLatihan(context, item.id, currentPage, pageSize)
                        },
                        modifier = Modifier.padding(vertical = 8.dp),
                    )

                    previousIsDone = item.isDone
                }
            }

            // --- Tombol Paginasi ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Sebelumnya (Previous)
                Button(
                    onClick = { currentPage -= 1 },
                    enabled = currentPage > 1 && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SignLinkTeal,
                        contentColor = Color.White
                    )
                ) {
                    Text("<<")
                }

                Text(
                    text = "$currentPage",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Tombol Selanjutnya (Next)
                Button(
                    onClick = { currentPage += 1 },
                    enabled = hasNextPage && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SignLinkTeal,
                        contentColor = Color.White
                    )
                ) {
                    Text(">>")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Komponen Card modul kuis
 */
@Composable
fun LatihanModulCard(
    navController: NavController,
    modul: LatihanData,
    onClick: () -> Unit,
    userRole: String,
    isLocked: Boolean,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUserAdmin = userRole == "ADMIN"
    val itemHeight = if (isUserAdmin) 80.dp else 60.dp

    val cardColor = if (modul.isDone) Color(0xFF4CAF50) else SignLinkTeal

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(itemHeight)
            .clickable(
                enabled = (!isLocked || isUserAdmin),
                onClick = onClick
            )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (modul.isDone) Icons.Default.Check else Icons.AutoMirrored.Filled.ListAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = modul.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.ListAlt,
                            contentDescription = "Total Soal",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${modul.totalSoal} Latihan",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

                // ADMIN: tombol delete
                if (isUserAdmin) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .fillMaxHeight()
                            .background(Color(0xFFE57373), RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                            .clickable(onClick = onDeleteClicked),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                if (!isUserAdmin && modul.isDone) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF388E3C), RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                            .clickable(
                                onClick = {navController.navigate("${Destinations.LATIHAN_DETAIL_SCREEN}/${modul.id}") }
                                ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Ulangi",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            if (isLocked && !isUserAdmin) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xAA000000))
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Terkunci",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Selesaikan modul sebelumnya untuk membuka",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
