package id.ac.pnm.bayarin_app.ui.tambahTeman

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.pnm.bayarin_app.data.model.Users

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahTemanScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDaftarTeman: () -> Unit,
    viewModel: TambahTemanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadFriendIds()
    }
    //pesan sukses
    LaunchedEffect(uiState.isAddSuccess) {
        if (uiState.isAddSuccess) {
            snackbarHostState.showSnackbar(uiState.successMessage)
            viewModel.resetSuccessFlag()
        }
    }

    // pesan error
    LaunchedEffect(uiState.isErrorSearch, uiState.isErrorAddFriend) {
        if (uiState.isErrorSearch || uiState.isErrorAddFriend) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            viewModel.resetErrorFlag()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Tambah Teman", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(onClick = onNavigateToDaftarTeman) {
                        Text("Daftar Teman", color = Color(0xFF1976D2), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar state ViewModel
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari nama atau email teman...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari", tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hasil Pencarian",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            //tampilan berdasarkan state
            when {
                uiState.isSearching -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    }
                }
                uiState.searchResults.isEmpty() && viewModel.searchQuery.isNotEmpty() -> {
                    Text("Tidak ada pengguna ditemukan", color = Color.Gray, fontSize = 14.sp)
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.searchResults) { user ->
                            // Cek status pertemanan dari set yang ada di uiState
                            val isFriend = uiState.friendIds.contains(user.id)
                            val isPending = uiState.pendingIds.contains(user.id)

                            TemanItemCard(
                                user = user,
                                isFriend = isFriend,
                                isPending = isPending,
                                onAddClick = { viewModel.tambahTeman(user) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemanItemCard(
    user: Users,
    isFriend: Boolean,
    isPending: Boolean,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1976D2))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name ?: "Kosong",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = user.email ?: "",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Tombol Tambah menyesuaikan dengan state
            Button(
                onClick = onAddClick,
                enabled = !isFriend && !isPending,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    disabledContainerColor = Color.LightGray
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = when {
                        isFriend -> "Berteman"
                        isPending -> "Proses..."
                        else -> "Tambah"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isFriend || isPending) Color.DarkGray else Color.White
                )
            }
        }
    }
}

