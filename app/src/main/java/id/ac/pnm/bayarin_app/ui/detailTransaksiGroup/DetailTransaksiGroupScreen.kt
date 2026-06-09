package id.ac.pnm.bayarin_app.ui.detailTransaksiGroup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale

val BluePrimary = Color(0xFF2473ED)
val BlueLight = Color(0xFFE8F0FE)
val BackgroundGray = Color(0xFFF8F9FA)
val TextGray = Color(0xFF5F6368)

data class MemberBillStatus(
    val id: String,
    val name: String,
    val initial: String,
    val amount: Long,
    val status: BillStatus,
    val isAdmin: Boolean = false
)

enum class BillStatus {
    BELUM_BAYAR,
    PENGINGAT_TERKIRIM,
    SUDAH_BAYAR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransaksiGroupScreen(
    groupId: String,
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: DetailTransaksiGroupViewModel = viewModel()
) {
    // Mengamati UI State dari ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(groupId)
    }

    LaunchedEffect(uiState.isReminderSuccess) {
        if (uiState.isReminderSuccess) {
            snackbarHostState.showSnackbar("Pengingat patungan berhasil dikirim!")
            viewModel.resetReminderFlag()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        },
        bottomBar = {
            // Tombol Kirim Pengingat di Bagian Bawah
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { viewModel.kirimPengingatGrup() },
                    enabled = !uiState.isLoading && uiState.memberBills.any { it.status == BillStatus.BELUM_BAYAR },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD0E1FD),
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF0056D2),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kirim Pengingat Ke Group",
                            color = Color(0xFF0056D2),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }
            uiState.errorMessage.isNotEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(text = uiState.errorMessage, color = Color.Red, fontSize = 14.sp)
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

                    //Info Header Grup Patungan
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "GRUP PATUNGAN",
                                        color = TextGray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = uiState.groupName,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = uiState.groupSubInfo,
                                        color = TextGray,
                                        fontSize = 13.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color(0xFFF1F3F9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = Color(0xFF5C6E9A)
                                    )
                                }
                            }
                        }
                    }

                    //Card Informasi Pembayaran
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(90.dp)
                                        .padding(end = 12.dp, bottom = 4.dp)
                                )

                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = uiState.payerInitial,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "TELAH DIBAYAR OLEH",
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                            Text(
                                                text = uiState.payerName,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = "Total Tagihan",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = formatRupiah(uiState.totalAmount),
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Rincian Patungan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    //Iterasi Anggota Grup Patungan dari Firebase
                    items(uiState.memberBills) { bill ->
                        MemberBillItemRow(
                            bill = bill,
                            isCreator = uiState.isCreator,
                            onMarkAsPaid = { viewModel.tandaiSudahBayar(bill.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun MemberBillItemRow(
    bill: MemberBillStatus,
    isCreator: Boolean,
    onMarkAsPaid: () -> Unit
) {
    // State untuk memunculkan dialog konfirmasi pembayaran
    var showConfirmDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // Dialog Konfirmasi Lunas
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Pembayaran", fontWeight = FontWeight.Bold) },
            text = { Text("Tandai ${bill.name} sudah membayar tagihan sebesar ${formatRupiah(bill.amount)}?") },
            confirmButton = {
                TextButton(onClick = {
                    onMarkAsPaid()
                    showConfirmDialog = false
                }) {
                    Text("Ya, Lunas", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EDF4)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bill.initial,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF5C6E9A)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = bill.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )

                    // Munculkan label Admin jika dia adalah pembuat grup
                    if (bill.isAdmin) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = BlueLight
                        ) {
                            Text(
                                text = "Admin",
                                color = BluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                if (!bill.isAdmin) {
                    val prefixText = if (bill.status == BillStatus.SUDAH_BAYAR) "Lunas : " else "Bayar : "
                    val textColor = if (bill.status == BillStatus.SUDAH_BAYAR) BluePrimary else Color.Black

                    Text(
                        text = "$prefixText${formatRupiah(bill.amount)}",
                        fontSize = 13.sp,
                        color = textColor,
                        fontWeight = if (bill.status == BillStatus.SUDAH_BAYAR) FontWeight.Medium else FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Icon Status Pembayaran
                    when (bill.status) {
                        BillStatus.BELUM_BAYAR -> {
                            Surface(
                                shape = RoundedCornerShape(percent = 50),
                                color = Color(0xFFFFEBEE),
                                onClick = { if (isCreator) showConfirmDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Color(0xFFD32F2F),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Belum bayar", color = Color(0xFFD32F2F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        BillStatus.PENGINGAT_TERKIRIM -> {
                            Surface(
                                shape = RoundedCornerShape(percent = 50),
                                color = Color(0xFFE8F0FE),
                                onClick = { if (isCreator) showConfirmDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pengingat terkirim", color = BluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        BillStatus.SUDAH_BAYAR -> {
                            Surface(
                                shape = RoundedCornerShape(percent = 50),
                                color = BluePrimary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sudah bayar", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Kembalikan uang saya",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }
        }
    }
}

fun formatRupiah(value: Long): String {
    val localeID = Locale("id", "ID")
    val formatter = NumberFormat.getNumberInstance(localeID)
    return "Rp ${formatter.format(value)}"
}