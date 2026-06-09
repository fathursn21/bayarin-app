package id.ac.pnm.bayarin_app.ui.tambahGroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.pnm.bayarin_app.data.model.Users

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahGroupScreen(
    onNavigateBack: () -> Unit,
    viewModel: TambahGroupViewModel = viewModel()
) {
    var groupName by remember { mutableStateOf("") }
    var showSearchDialog by remember { mutableStateOf(false) }

    // State Map untuk menyimpan nominal per user ID (Key: userId, Value: Nominal String)
    val memberNominals = remember { mutableStateMapOf<String, String>() }

    // Mengambil state terpusat dari ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Jika berhasil membuat grup, otomatis kembali ke halaman sebelumnya
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccessFlag()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Grup Baru", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF0A58CA),
                    navigationIconContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = {
                        val convertedNominals = memberNominals.mapValues { (_, value) ->
                            value.toLongOrNull() ?: 0L
                        }
                        viewModel.createGroup(groupName, convertedNominals)
                    },
                    enabled = groupName.isNotBlank() && uiState.selectedFriends.isNotEmpty() && !uiState.isCreatingGroup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A58CA),
                        disabledContainerColor = Color.LightGray
                    ),
                ) {
                    if (uiState.isCreatingGroup) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Buat Grup", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            //Nama Grup
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Nama Grup") },
                placeholder = { Text("Cth: Patungan Ngopi, Server Minecraft, dll") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Baris Judul Anggota Grup & Tombol Tambah
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Anggota Grup (${uiState.selectedFriends.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )

                TextButton(onClick = { showSearchDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Anggota", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Daftar Teman Yang Berhasil Ditambahkan beserta Input Nominal
            if (uiState.selectedFriends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada anggota. Ketuk 'Tambah' untuk mencari.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.selectedFriends) { friend ->
                        // Ambil nilai nominal dari map, jika belum ada berikan string kosong
                        val currentNominal = memberNominals[friend.id] ?: ""

                        AnggotaGrupRowItem(
                            user = friend,
                            nominalValue = currentNominal,
                            onNominalChange = { newValue ->
                                if (newValue.all { it.isDigit() }) {
                                    memberNominals[friend.id] = newValue
                                }
                            },
                            onRemoveClick = {
                                viewModel.toggleFriendSelection(friend)
                                memberNominals.remove(friend.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Pop-up Dialog Tambah Teman
    if (showSearchDialog) {
        AlertDialog(
            onDismissRequest = {
                showSearchDialog = false
                viewModel.updateSearchQuery("")
            },
            title = {
                Text("Cari & Tambah Anggota", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Cari nama atau email...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F3F5),
                            focusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.filteredFriends.isEmpty()) {
                            item {
                                Text("Teman tidak ditemukan", color = Color.Gray, fontSize = 14.sp)
                            }
                        } else {
                            items(uiState.filteredFriends) { friend ->
                                val isSelected = uiState.selectedFriends.contains(friend)
                                FriendListItem(
                                    user = friend,
                                    isSelected = isSelected,
                                    onClick = { viewModel.toggleFriendSelection(friend) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSearchDialog = false
                        viewModel.updateSearchQuery("")
                    }
                ) {
                    Text("Selesai", color = Color(0xFF0A58CA), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun AnggotaGrupRowItem(
    user: Users,
    nominalValue: String,
    onNominalChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            //Info Profil & Tombol Hapus
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE7F1FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0A58CA))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = user.email, color = Color.Gray, fontSize = 12.sp)
                }

                IconButton(onClick = onRemoveClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Hapus Anggota",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //Mengatur Nominal Tagihan
            OutlinedTextField(
                value = nominalValue,
                onValueChange = onNominalChange,
                label = { Text("Nominal Tagihan", fontSize = 12.sp) },
                placeholder = { Text("Masukkan jumlah patungan") },
                prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = Color.DarkGray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Memunculkan keyboard angka
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0A58CA),
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun FriendListItem(user: Users, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE0E0E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = user.email, color = Color.Gray, fontSize = 12.sp)
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Terpilih",
                tint = Color(0xFF0A58CA),
                modifier = Modifier.size(22.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            )
        }
    }
}