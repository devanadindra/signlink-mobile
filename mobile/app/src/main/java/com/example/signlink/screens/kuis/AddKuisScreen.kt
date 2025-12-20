package com.example.signlink.screens.kuis

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.signlink.data.models.kuis.KuisReq
import com.example.signlink.data.models.kuis.OpsiKuisReq
import com.example.signlink.data.models.kuis.SoalKuisReq
import com.example.signlink.ui.theme.DarkText
import com.example.signlink.ui.theme.SignLinkTeal
import com.example.signlink.viewmodel.KuisViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.delay

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKuisScreen(
    navController: NavController,
    viewModel: KuisViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var batasWaktuInput by remember { mutableStateOf("10") }
    var listSoalKuis by remember { mutableStateOf(mutableListOf<SoalKuisReq>()) }

    var editingIndex by remember { mutableStateOf<Int?>(null) }

    var currentSoalText by remember { mutableStateOf("") }
    var currentJawabanBenar by remember { mutableStateOf("") }

    val currentOpsiKuis = remember {
        mutableStateListOf(
            OpsiKuisReq(label = "A", text = ""),
            OpsiKuisReq(label = "B", text = ""),
            OpsiKuisReq(label = "C", text = ""),
            OpsiKuisReq(label = "D", text = ""),
        )
    }

    var nameError by remember { mutableStateOf<String?>(null) }
    var batasWaktuError by remember { mutableStateOf<String?>(null) }
    var listSoalError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.errorMessage.collectLatest { error ->
            error?.let {
                delay(1500)
                Toast.makeText(context, "Gagal: $it", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.successMessage.collectLatest { success ->
            success?.let {
                delay(1500)
                Toast.makeText(context, "Berhasil: $it", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
                viewModel.clearSuccess()
            }
        }
    }

    fun validateCurrentSoal(): Boolean {
        if (currentSoalText.trim().isBlank()) {
            Toast.makeText(context, "Teks soal harus diisi.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (currentJawabanBenar.trim().isBlank()) {
            Toast.makeText(context, "Jawaban benar harus diisi.", Toast.LENGTH_SHORT).show()
            return false
        }
        val isOpsiValid = currentOpsiKuis.all { it.text.isNotBlank() }
        if (!isOpsiValid) {
            Toast.makeText(context, "Semua opsi jawaban (A, B, C, D) harus diisi.", Toast.LENGTH_LONG).show()
            return false
        }
        if (currentOpsiKuis.none { it.text.trim() == currentJawabanBenar.trim() }) {
            Toast.makeText(context, "Jawaban benar harus sesuai dengan teks salah satu opsi jawaban.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun resetCurrentSoalFields() {
        currentSoalText = ""
        currentJawabanBenar = ""
        currentOpsiKuis.forEachIndexed { index, opsi ->
            currentOpsiKuis[index] = opsi.copy(text = "")
        }
        editingIndex = null
    }

    fun handleSaveSoal() {
        if (!validateCurrentSoal()) return

        val newSoal = SoalKuisReq(
            soal = currentSoalText.trim(),
            jawabanBenar = currentJawabanBenar.trim(),
            opsiKuis = currentOpsiKuis.toList()
        )

        if (editingIndex != null && editingIndex!! < listSoalKuis.size) {
            listSoalKuis[editingIndex!!] = newSoal
            Toast.makeText(context, "Soal ${editingIndex!! + 1} berhasil diperbarui.", Toast.LENGTH_SHORT).show()
        } else {
            listSoalKuis.add(newSoal)
            Toast.makeText(context, "Soal berhasil ditambahkan.", Toast.LENGTH_SHORT).show()
        }

        resetCurrentSoalFields()
        listSoalError = null
    }

    fun handleEditSoal(index: Int, soal: SoalKuisReq) {
        editingIndex = index
        currentSoalText = soal.soal
        currentJawabanBenar = soal.jawabanBenar
        soal.opsiKuis.forEachIndexed { i, opsi ->
            currentOpsiKuis[i] = opsi
        }
        Toast.makeText(context, "Mode Edit Soal ${index + 1} diaktifkan.", Toast.LENGTH_SHORT).show()
    }

    fun validateForm(): Boolean {
        nameError = null
        batasWaktuError = null
        listSoalError = null
        var valid = true

        if (name.trim().isBlank()) {
            nameError = "Nama kuis tidak boleh kosong."
            valid = false
        }

        val batasWaktuInt = batasWaktuInput.toIntOrNull()
        if (batasWaktuInt == null || batasWaktuInt <= 0) {
            batasWaktuError = "Batas waktu harus berupa angka positif."
            valid = false
        }

        if (listSoalKuis.isEmpty()) {
            listSoalError = "Minimal 1 soal kuis harus ditambahkan."
            valid = false
        }

        return valid
    }

    fun handleSubmit() {
        if (!validateForm()) return

        val batasWaktuInt = batasWaktuInput.toIntOrNull() ?: 0

        val kuisReq = KuisReq(
            name = name.trim(),
            batasWaktu = batasWaktuInt,
            soalKuis = listSoalKuis.toList()
        )

        viewModel.addKuis(
            context = context,
            req = kuisReq
        )
    }

    val actionButtonText = if (editingIndex != null) "Simpan Perubahan Soal" else "Tambahkan Soal ke Daftar"
    val actionButtonIcon = if (editingIndex != null) Icons.Default.Save else Icons.Default.Add

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Modul Kuis", fontWeight = FontWeight.SemiBold, color = DarkText) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
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
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Detail Modul", color = DarkText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))

            InputLabel("Nama Kuis")
            InputTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                isError = nameError != null,
                errorMessage = nameError,
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            InputLabel("Batas Waktu (Menit)")
            InputTextField(
                value = batasWaktuInput,
                onValueChange = { batasWaktuInput = it.filter { c -> c.isDigit() }; batasWaktuError = null },
                isError = batasWaktuError != null,
                errorMessage = batasWaktuError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(Modifier.height(32.dp))

            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.height(32.dp))

            val soalHeader = if (editingIndex != null) "Edit Soal ${editingIndex!! + 1}" else "Tambah Soal Baru"
            Text(soalHeader, color = DarkText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))

            InputLabel("Teks Soal")
            InputTextField(
                value = currentSoalText,
                onValueChange = { currentSoalText = it },
                placeholder = { Text("Contoh: Apa arti dari isyarat 'Ayah'?") },
                singleLine = false
            )
            Spacer(Modifier.height(16.dp))

            InputLabel("Jawaban Benar (Harus Sama dengan salah satu Opsi)")
            InputTextField(
                value = currentJawabanBenar,
                onValueChange = { currentJawabanBenar = it },
                placeholder = { Text("Contoh: Ayah") }
            )
            Spacer(Modifier.height(16.dp))

            Text("Opsi Jawaban", color = DarkText, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))

            Column {
                currentOpsiKuis.forEachIndexed { index, opsi ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            "${opsi.label}. ",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(20.dp),
                            color = DarkText
                        )
                        InputTextField(
                            value = opsi.text,
                            onValueChange = {
                                currentOpsiKuis[index] = opsi.copy(text = it)
                            },
                            placeholder = { Text("Opsi ${opsi.label}") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { handleSaveSoal() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading &&
                        currentSoalText.isNotBlank() &&
                        currentJawabanBenar.isNotBlank() &&
                        currentOpsiKuis.all { it.text.isNotBlank() }
            ) {
                Icon(actionButtonIcon, contentDescription = actionButtonText, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(actionButtonText, color = Color.White)
            }

            if (editingIndex != null) {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { resetCurrentSoalFields() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batalkan Edit", color = Color.Red)
                }
            }

            Spacer(Modifier.height(32.dp))

            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.height(32.dp))

            Text("Daftar Soal Kuis (${listSoalKuis.size})", color = DarkText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))

            if (listSoalKuis.isNotEmpty()) {
                listSoalKuis.forEachIndexed { index, soal ->
                    SoalKuisItem(
                        index = index,
                        soal = soal,
                        isEditing = editingIndex == index,
                        onDelete = {
                            listSoalKuis.removeAt(index)
                            if (editingIndex == index) resetCurrentSoalFields()
                        },
                        onEdit = { handleEditSoal(index, soal) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(30.dp))
            } else {
                Text(
                    "Belum ada soal yang ditambahkan.",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
                if (listSoalError != null) {
                    Text(listSoalError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(30.dp))
            }

            Button(
                onClick = { handleSubmit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SignLinkTeal),
                enabled = !isLoading && listSoalKuis.isNotEmpty() && name.isNotBlank() && (batasWaktuInput.toIntOrNull()
                    ?: 0) > 0 && editingIndex == null
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text("Simpan Modul Kuis", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}


@Composable
fun InputLabel(text: String) {
    Text(text, color = DarkText, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp))
}

@Composable
fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            placeholder = placeholder,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SignLinkTeal,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            )
        )
        if (errorMessage != null) {
            Text(
                errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun SoalKuisItem(
    index: Int,
    soal: SoalKuisReq,
    isEditing: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val cardColor = if (isEditing) SignLinkTeal.copy(alpha = 0.1f) else Color.White

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Soal ${index + 1} ${if (isEditing) " (SEDANG DIEDIT)" else ""}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isEditing) SignLinkTeal else DarkText
                )

                Row {
                    // Tombol Edit
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Soal", tint = SignLinkTeal)
                    }
                    Spacer(Modifier.width(8.dp))
                    // Tombol Hapus
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Soal", tint = Color.Red)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Teks Soal
            Text("Teks Soal:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = DarkText)
            Text(soal.soal, fontSize = 14.sp, color = DarkText.copy(alpha = 0.8f))

            Spacer(Modifier.height(8.dp))

            // Detail Jawaban
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))

            Text("Jawaban Benar: ${soal.jawabanBenar}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF4CAF50))
            Text("Opsi Kuis:", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = DarkText)

            soal.opsiKuis.forEach { opsi ->
                Text(
                    "${opsi.label}. ${opsi.text}",
                    fontSize = 12.sp,
                    color = DarkText.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}