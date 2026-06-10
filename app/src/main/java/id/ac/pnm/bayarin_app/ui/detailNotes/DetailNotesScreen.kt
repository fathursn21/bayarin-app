package id.ac.pnm.bayarin_app.ui.detailNotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.ui.detailTransaksiGroup.BackgroundGray
import id.ac.pnm.bayarin_app.ui.detailTransaksiGroup.BillStatus
import id.ac.pnm.bayarin_app.ui.detailTransaksiGroup.BluePrimary
import id.ac.pnm.bayarin_app.ui.detailTransaksiGroup.DetailTransaksiGroupViewModel
import id.ac.pnm.bayarin_app.ui.detailTransaksiGroup.MemberBillItemRow
import id.ac.pnm.bayarin_app.ui.detailTransaksiGroup.TextGray
import id.ac.pnm.bayarin_app.ui.detailTransaksiGroup.formatRupiah
import id.ac.pnm.bayarin_app.ui.home.getCategoryIcon
import id.ac.pnm.bayarin_app.ui.newnotes.NewNotesViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val BluePrimary = Color(0xFF2473ED)
val BlueLight = Color(0xFFE8F0FE)
val BackgroundGray = Color(0xFFF8F9FA)
val TextGray = Color(0xFF5F6368)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailNotesScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    newNotesViewModel: NewNotesViewModel = viewModel(),
    noteId: String
) {
    // Mengamati UI State dari ViewModel
    val newNotesUiState by newNotesViewModel.uiState.collectAsState()

    LaunchedEffect(noteId) {
        newNotesViewModel.loadDetailNotes(noteId)
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bayarin",
                        color = BluePrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Navigasi ke Halaman Notifikasi jika ada */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = BluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundGray)
            )
        }
    ) { paddingValues ->
        when {
            newNotesUiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }
            newNotesUiState.error.isNotEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(text = newNotesUiState.error, color = Color.Red, fontSize = 14.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    //Card Informasi Pembayaran
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Icon(
                                    imageVector = getCategoryIcon(newNotesUiState.detailNotes.category),
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(90.dp)
                                        .padding(end = 12.dp, bottom = 4.dp)
                                )

                                Column(modifier = Modifier.padding(20.dp)) {

                                    Text(
                                        text = "Total Transaksi",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = formatRupiah(newNotesUiState.detailNotes.nominal),
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )

                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                DetailRow(
                                    title = "Jenis Transaksi",
                                    value = if (newNotesUiState.detailNotes.expense)
                                        "Pengeluaran"
                                    else
                                        "Pemasukan"
                                )

                                HorizontalDivider()

                                DetailRow(
                                    title = "Kategori",
                                    value = newNotesUiState.detailNotes.category
                                )

                                HorizontalDivider()

                                DetailRow(
                                    title = "Tanggal",
                                    value = formatDate(
                                        newNotesUiState.detailNotes.date
                                    )
                                )

                                HorizontalDivider()

                                DetailRow(
                                    title = "Catatan",
                                    value = if (
                                        newNotesUiState.detailNotes.note.isBlank()
                                    ) "-"
                                    else
                                        newNotesUiState.detailNotes.note
                                )

                                HorizontalDivider()

                                DetailRow(
                                    title = "Dibuat Pada",
                                    value = formatDateTime(
                                        newNotesUiState.detailNotes.createdAt
                                    )
                                )

                            }
                        }
                    }


                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

}

@Composable
fun DetailRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = TextGray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            fontSize = 14.sp
        )
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("id", "ID")
    )

    return sdf.format(Date(timestamp))
}

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat(
        "dd MMM yyyy HH:mm",
        Locale("id", "ID")
    )

    return sdf.format(Date(timestamp))
}

fun formatRupiah(value: Long): String {
    val localeID = Locale("id", "ID")
    val formatter = NumberFormat.getNumberInstance(localeID)
    return "Rp ${formatter.format(value)}"
}