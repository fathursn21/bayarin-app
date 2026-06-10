package id.ac.pnm.bayarin_app.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.pnm.bayarin_app.data.model.Notifications
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifikasi", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF8F9FA))
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is NotificationUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2473ED))
                }
            }
            is NotificationUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is NotificationUiState.Success -> {
                if (state.list.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text("Belum ada pemberitahuan baru", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(4.dp)) }
                        items(state.list) { notif ->
                            NotificationItemRow(
                                notif = notif,
                                onItemClick = { viewModel.markAsRead(notif.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItemRow(
    notif: Notifications,
    onItemClick: () -> Unit
) {
    val isUnread = !notif.isRead
    val backgroundColor = if (isUnread) Color(0xFFE8F0FE) else Color.White
    val timeFormatted = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date(notif.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isUnread) Color(0xFF2473ED) else Color(0xFFE8EDF4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (notif.title.contains("Grup")) Icons.Default.Notifications else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isUnread) Color.White else Color(0xFF5C6E9A)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notif.title,
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notif.message,
                    fontSize = 12.sp,
                    color = if (isUnread) Color.Black else Color(0xFF5F6368),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = timeFormatted,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            if (isUnread) {
                Spacer(modifier = Modifier.width(6.dp))
                // Titik indikator penanda Belum Dibaca
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF2473ED), CircleShape)
                )
            }
        }
    }
}