package com.example.signlink.screens.latihan

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.viewmodel.LatihanViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLatihanScreen(
    navController: NavController,
    viewmodel: LatihanViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var soalInput by remember { mutableStateOf("") }
    var listSoal by remember { mutableStateOf(mutableListOf<String>()) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var listSoalError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewmodel.isLoading.collectAsState()

    // listen error
    LaunchedEffect(Unit) {
        viewmodel.errorMessage.collectLatest { error ->
            error?.let {
                delay(1500)
                Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show()
                viewmodel.clearError()
            }
        }
    }

    // listen success
    LaunchedEffect(Unit) {
        viewmodel.successMessage.collectLatest { success ->
            success?.let {
                delay(1500)
                Toast.makeText(context, "Berhasil: $it", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                viewmodel.clearSuccess()
            }
        }
    }

    fun validateForm(): Boolean {
        nameError = null
        listSoalError = null
        var valid = true

        if (name.trim().isBlank()) {
            nameError = "Nama latihan tidak boleh kosong."
            valid = false
        }

        if (listSoal.isEmpty()) {
            listSoalError = "Minimal 1 soal harus ditambahkan."
            valid = false
        }

        return valid
    }

    fun handleSubmit() {
        if (!validateForm()) return

        viewmodel.addLatihan(
            context = context,
            name = name,
            soalLatihan = listSoal
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Modul Latihan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Nama Latihan", color = DarkText, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError != null,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SignLinkTeal,
                    errorBorderColor = Color.Red
                )
            )
            if (nameError != null) {
                Text(
                    nameError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Tambah Soal", color = DarkText, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = soalInput,
                onValueChange = { soalInput = it },
                placeholder = { Text("Masukkan soal...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    if (soalInput.trim().isNotEmpty()) {
                        listSoal.add(soalInput.trim())
                        soalInput = ""
                        listSoalError = null
                    } else {
                        listSoalError = "Soal tidak boleh kosong."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkText,
                    containerColor = SignLinkTeal,
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color(0xFFE0E0E0),
                ),
                shape = RoundedCornerShape(50),
                enabled = !isLoading &&
                        soalInput.isNotBlank()
            ) {
                Text("Tambah Soal", color = Color.White)
            }

            if (listSoalError != null) {
                Text(
                    listSoalError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            if (listSoal.isNotEmpty()) {
                Text(
                    "Daftar Soal:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                listSoal.forEachIndexed { index, soal ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${index + 1}. $soal", modifier = Modifier.weight(1f))
                            Text(
                                "Hapus",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable {
                                    listSoal.removeAt(index)
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))
            }

            OutlinedButton(
                onClick = { handleSubmit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkText,
                    containerColor = SignLinkTeal,
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color(0xFFE0E0E0),
                ),
                enabled = !isLoading &&
                        name.isNotBlank() &&
                        listSoal.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text("Simpan Modul Latihan", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}
