package com.example.signlink.screens.kamus

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.signlink.data.utils.utils.uriToFile
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.viewmodel.KamusViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKamusScreen(
    navController: NavController,
    viewmodel: KamusViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf<Char?>(null) }
    var arti by remember { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    var categoryError by remember { mutableStateOf<String?>(null) }
    var artiError by remember { mutableStateOf<String?>(null) }
    var videoError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewmodel.isLoading.collectAsState()

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            videoUri = uri
            videoError = null
        }
    )

    LaunchedEffect(Unit) {
        viewmodel.errorMessage.collectLatest { error ->
            error?.let {
                Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show()
                viewmodel.clearError()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewmodel.successMessage.collectLatest { success ->
            success?.let {
                Toast.makeText(context, "Berhasil: $it", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                viewmodel.clearSuccess()
            }
        }
    }

    fun validateForm(): Boolean {
        categoryError = null
        artiError = null
        videoError = null
        var isValid = true

        if (selectedCategory == null) {
            categoryError = "Kategori harus dipilih."
            isValid = false
        }

        if (arti.isBlank()) {
            artiError = "Arti (Kata Isyarat) tidak boleh kosong."
            isValid = false
        } else if (selectedCategory != null) {
            val firstLetterOfArti = arti.trim().uppercase().firstOrNull()
            if (firstLetterOfArti != null && firstLetterOfArti != selectedCategory) {
                artiError = "Huruf awal kata isyarat ('$firstLetterOfArti') harus sesuai dengan Kategori ('$selectedCategory')."
                isValid = false
            }
        }

        if (videoUri == null) {
            videoError = "Video isyarat harus diunggah."
            isValid = false
        }

        return isValid
    }

    fun handleSubmit() {
        if (!validateForm()) {
            Toast.makeText(context, "Mohon lengkapi dan perbaiki semua bidang yang wajib diisi.", Toast.LENGTH_LONG).show()
            return
        }

        if (isLoading) return

        try {
            val file = uriToFile(videoUri!!, context)

            viewmodel.addKamus(
                context = context,
                arti = arti.trim(),
                kategori = selectedCategory.toString(),
                videoFile = file
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal memproses file video. Pastikan format video valid.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Kata Isyarat Baru", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Dropdown Kategori (A-Z)
            Text(
                text = "Kategori",
                modifier = Modifier.fillMaxWidth(),
                color = DarkText,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            CategoryDropdown(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    categoryError = null
                    if (arti.isNotBlank()) artiError = null
                },
                isError = categoryError != null
            )
            if (categoryError != null) {
                Text(
                    text = categoryError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Arti (Kata Isyarat)",
                modifier = Modifier.fillMaxWidth(),
                color = DarkText,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = arti,
                onValueChange = { arti = it; artiError = null },
                placeholder = { Text("Contoh: 'Selamat Pagi' atau 'Air'") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = artiError != null,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SignLinkTeal,
                    errorBorderColor = Color.Red,
                )
            )
            if (artiError != null) {
                Text(
                    text = artiError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Video Bahasa Isyarat (Maks. 1)",
                modifier = Modifier.fillMaxWidth(),
                color = DarkText,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            VideoUploadButton(
                videoUri = videoUri,
                onClick = { videoPickerLauncher.launch("video/*") },
                isError = videoError != null
            )

            if (videoError != null) {
                Text(
                    text = videoError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = ::handleSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
                shape = RoundedCornerShape(50),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Kamus Baru", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryDropdown(
    selectedCategory: Char?,
    onCategorySelected: (Char) -> Unit,
    isError: Boolean
) {
    val categories = remember { ('A'..'Z').toList() }
    var expanded by remember { mutableStateOf(false) }
    val borderColor = when {
        isError -> Color.Red
        expanded -> SignLinkTeal
        else -> Color.LightGray
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { expanded = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = selectedCategory?.toString() ?: "Pilih Kategori",
            color = if (selectedCategory == null) Color.Gray else DarkText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            Icons.Filled.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd),
            tint = DarkText
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.toString(), fontWeight = FontWeight.Medium) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun VideoUploadButton(
    videoUri: Uri?,
    onClick: () -> Unit,
    isError: Boolean
) {
    val buttonColor = if (isError) Color.Red else SignLinkTeal

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, buttonColor, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (videoUri != null) DarkText else Color.Gray,
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FileUpload,
            contentDescription = "Upload Video",
            tint = buttonColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = videoUri?.lastPathSegment ?: "Pilih video dari galeri...",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}