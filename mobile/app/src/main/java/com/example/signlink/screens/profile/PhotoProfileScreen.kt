package com.example.signlink.screens.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.signlink.data.utils.AuthUtil
import com.example.signlink.data.utils.utils.uriToFile
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.viewmodel.CustomerViewModel
import kotlinx.coroutines.flow.collectLatest
import java.io.File

/**
 * Halaman untuk menampilkan foto profil dalam layar penuh, mirip WhatsApp.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoProfileScreen(
    navController: NavController,
    customerViewModel: CustomerViewModel,
) {
    val context = LocalContext.current
    var imageUrl by remember { mutableStateOf(AuthUtil.getProfile(context)) }

    var showUploadDialog by remember { mutableStateOf(false) }

    if (showUploadDialog) {
        SelectImageDialog(
            onDismiss = { showUploadDialog = false },
            onImageSelectedAndUpload = { file ->
                showUploadDialog = false

                customerViewModel.addAvatar(context, file)
            }
        )
    }

    LaunchedEffect(Unit) {
        customerViewModel.errorMessage.collectLatest { error ->
            error?.let {
                Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show()
                customerViewModel.clearError()
            }
        }
    }

    LaunchedEffect(Unit) {
        customerViewModel.successMessage.collectLatest { success ->
            success?.let {
                imageUrl = AuthUtil.getProfile(context)
                Toast.makeText(context, "Berhasil: $it", Toast.LENGTH_SHORT).show()
                customerViewModel.clearSuccess()
            }
        }
    }

    fun handleDelete() {
        try {

            customerViewModel.deleteAvatar(
                context
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal menghapus foto.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Foto Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showUploadDialog = true }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Foto",
                            tint = Color.White
                        )
                    }
                    // Ikon Delete
                    IconButton(onClick = { handleDelete() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus Foto",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SignLinkTeal,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            imageUrl?.let {
                if (it.isEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Icon",
                        tint = SignLinkTeal,
                        modifier = Modifier.size(40.dp)
                    )

                } else {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Foto Profil Pengguna",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


@Composable
fun SelectImageDialog(
    onDismiss: () -> Unit,
    onImageSelectedAndUpload: (File) -> Unit
) {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val file = uriToFile(uri, context)
            onImageSelectedAndUpload(file)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Foto Profil") },
        text = {
            Column {

                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal)
                ) {
                    Text("Pilih dari Galeri")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        confirmButton = {},
        shape = RoundedCornerShape(12.dp)
    )
}
