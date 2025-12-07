package com.example.signlink.screens.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.signlink.ui.theme.*
import com.example.signlink.viewmodel.CustomerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest


/**
 * Halaman Edit Profil
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    customerViewModel: CustomerViewModel,
    navController: NavController,
    initialName: String,
    initialEmail: String,
    initialProfile: String,
    initialGoogleID: String
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading by customerViewModel.isLoading.collectAsState()

    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var profile by remember { mutableStateOf(initialProfile) }
    val isGoogleLogin = initialGoogleID.isNotBlank()

    DisposableEffect(Unit) {
        customerViewModel.clearUpdateProfileResult()

        onDispose {
            customerViewModel.clearUpdateProfileResult()
        }
    }

    LaunchedEffect(Unit) {
        customerViewModel.errorMessage.collectLatest { error ->
            error?.let {
                delay(1500)
                Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show()
                customerViewModel.clearError()
            }
        }
    }

    LaunchedEffect(Unit) {
        customerViewModel.successMessage.collectLatest { success ->
            success?.let {
                delay(1500)
                Toast.makeText(context, "Berhasil: $it", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                customerViewModel.clearSuccess()
            }
        }
    }

    fun handleSubmit() {
        if (isLoading) return
        val emailToSend = if (isGoogleLogin) initialEmail else email

        try {

            customerViewModel.updateProfile(
                context,
                name,
                emailToSend
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal memproses file video. Pastikan format video valid.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
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
            // Bagian Foto Profil
            EditProfileImage(profile) {
                // TODO: Logika untuk mengubah/mengunggah foto profil
                coroutineScope.launch {
                    Toast.makeText(context, "Fungsi ubah foto profil belum diimplementasikan.", Toast.LENGTH_LONG).show()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            EditProfileForm(
                name = name,
                onNameChange = { name = it },
                email = email,
                onEmailChange = { email = it },
                isEmailDisabled = isGoogleLogin
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = ::handleSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
                shape = RoundedCornerShape(50),
                enabled = !isLoading &&
                        name.isNotBlank() &&
                        email.isNotBlank() &&
                        (name != initialName || email != initialEmail),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

/**
 * Komponen untuk menampilkan dan mengedit foto profil.
 */
@Composable
fun EditProfileImage(
    profileUrl: String,
    onEditClicked: () -> Unit
) {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(SignLinkTeal.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (profileUrl.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Edit Foto Profil",
                    tint = SignLinkTeal,
                    modifier = Modifier.size(60.dp)
                )
            } else {
                AsyncImage(
                    model = profileUrl,
                    contentDescription = "User Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        FloatingActionButton(
            onClick = onEditClicked,
            modifier = Modifier.size(36.dp),
            containerColor = SignLinkTeal,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Ubah Foto",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
