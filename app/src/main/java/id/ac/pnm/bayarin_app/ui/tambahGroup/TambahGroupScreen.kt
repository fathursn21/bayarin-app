package id.ac.pnm.bayarin_app.ui.tambahGroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FriendUser(
    val id: String,
    val name: String,
    val username: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahGroupScreen(
    onNavigateBack: () -> Unit,
    onGroupCreated: (groupName: String, members: List<FriendUser>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val selectedFriends = remember { mutableStateListOf<FriendUser>() }

    val friendList = remember {
        listOf(
            FriendUser("1", "Denis", "@denisbeban"),
            FriendUser("2", "Sopo", "@sopo"),
            FriendUser("3", "Adit", "@adit"),
            FriendUser("4", "Sopo", "@sopongiro"),
            FriendUser("5", "Pak Hadji", "@hadji"),
            FriendUser("6", "Ucup", "@ucup")
        )
    }

    // Filter daftar teman berdasarkan pencarian
    val filteredFriends = friendList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.username.contains(searchQuery, ignoreCase = true)
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
                        if (groupName.isNotBlank() && selectedFriends.isNotEmpty()) {
                            onGroupCreated(groupName, selectedFriends)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A58CA),
                        disabledContainerColor = Color.LightGray
                    ),
                    enabled = groupName.isNotBlank() && selectedFriends.isNotEmpty()
                ) {
                    Text("Buat Grup", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            // 1. Input Nama Grup
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Nama Grup") },
                placeholder = { Text("Cth: Patungan Ngopag, Beli Server Minecraft, dll") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Tampilan Teman yang Terpilih
            if (selectedFriends.isNotEmpty()) {
                Text(
                    text = "Anggota Terpilih (${selectedFriends.size})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(selectedFriends) { friend ->
                        SelectedFriendChip(friend = friend, onRemove = { selectedFriends.remove(it) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            //Pencarian Teman
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari nama atau username...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Daftar Teman
            Text(
                text = "Daftar Teman",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredFriends) { friend ->
                    val isSelected = selectedFriends.contains(friend)
                    FriendListItem(
                        friend = friend,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedFriends.remove(friend)
                            } else {
                                selectedFriends.add(friend)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedFriendChip(friend: FriendUser, onRemove: (FriendUser) -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE7F1FF),
        modifier = Modifier.clickable { onRemove(friend) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = friend.name.split(" ").first(),
                color = Color(0xFF0A58CA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hapus",
                tint = Color(0xFF0A58CA),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun FriendListItem(friend: FriendUser, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        //Placeholder Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFE0E0E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
        }

        Spacer(modifier = Modifier.width(16.dp))

        //Info User
        Column(modifier = Modifier.weight(1f)) {
            Text(text = friend.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = friend.username, color = Color.Gray, fontSize = 14.sp)
        }

        //Indikator terpilih
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Terpilih",
                tint = Color(0xFF0A58CA)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Transparent, CircleShape)
            )
        }
    }
}