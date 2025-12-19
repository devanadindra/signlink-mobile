package com.example.signlink.screens.tti

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.signlink.R
import com.example.signlink.viewmodel.KamusViewModel
import com.example.signlink.ui.theme.*
import com.example.signlink.components.DictionaryHeaderCard
import com.example.signlink.data.models.kamus.KamusData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextToIsyaratScreen(
    viewModel: KamusViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    val kamusList by viewModel.kamusList.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Status Paginasi
    var currentPage by remember { mutableIntStateOf(1) }
    var hasNextPage by remember { mutableStateOf(true) }

    var selectedKamusItems by remember { mutableStateOf(setOf<KamusData>()) }

    val listState = rememberLazyGridState()

    val pageSize = 14

    LaunchedEffect(currentPage) {
        listState.scrollToItem(0)

        viewModel.getAllKamus(context, "", currentPage, pageSize)
    }


    LaunchedEffect(kamusList, isLoading) {
        if (!isLoading && kamusList.size < pageSize) {
            hasNextPage = false
        } else if (!isLoading && kamusList.size == pageSize) {
            hasNextPage = true
        }
    }


    LaunchedEffect(successMessage) {
        successMessage?.let { success ->
            if (success.isNotBlank()) {
                viewModel.clearSuccess()
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            if (error.isNotBlank()) {
                Toast.makeText(context, "Gagal: $error", Toast.LENGTH_LONG).show()
                viewModel.clearError()
                hasNextPage = false
            }
        }
    }

    val onItemSelected: (KamusData) -> Unit = { item ->
        selectedKamusItems = if (selectedKamusItems.contains(item)) {
            selectedKamusItems - item
        } else {
            selectedKamusItems + item
        }
    }

    val onRemoveItem: (KamusData) -> Unit = { item ->
        selectedKamusItems = selectedKamusItems - item
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 42.dp)
            ) {
                Button(
                    onClick = {
                        val selectedItemsList = selectedKamusItems.toList()

                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("kamus_items", selectedItemsList)

                        navController.navigate("tti_result_screen")

                    },
                    enabled = selectedKamusItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SignLinkTeal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Terjemahkan (${selectedKamusItems.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                title = "Teks ke Bahasa Isyarat",
                description = "Pilih kata untuk diubah jadi bahasa isyarat (Halaman $currentPage)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedKamusItems.isNotEmpty()) {
                SelectedItemsCard(
                    selectedItems = selectedKamusItems,
                    onRemoveItem = onRemoveItem,
                    modifier = Modifier.fillMaxWidth(0.95f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(
                    items = kamusList,
                    key = { item -> item.id }
                ) { item ->
                    KamusListItem(
                        item = item,
                        isSelected = selectedKamusItems.contains(item),
                        onClick = { onItemSelected(item) }
                    )
                }

                if (isLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SignLinkTeal)
                        }
                    }
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
                    color = DarkText
                )

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

@Composable
fun SelectedItemsCard(
    selectedItems: Set<KamusData>,
    onRemoveItem: (KamusData) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Pilihan Anda:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkText,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                selectedItems.forEach { item ->
                    SelectedWordChip(
                        item = item,
                        onRemove = { onRemoveItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedWordChip(
    item: KamusData,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SignLinkTeal,
        contentColor = Color.White,
        modifier = Modifier.heightIn(min = 32.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.arti.replace("_", " "),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Hapus",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun KamusListItem(
    item: KamusData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cardColor = if (isSelected) SignLinkYellow else SignLinkTeal
    val textColor = if (isSelected) DarkText else Color.White

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) SignLinkTeal else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.arti.replace("_", " "),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}