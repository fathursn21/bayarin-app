package id.ac.pnm.bayarin_app.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.ui.navigation.Routes
import id.ac.pnm.bayarin_app.ui.newnotes.NewNotesViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val BluePrimary = Color(0xFF2473ED)
val BlueLight = Color(0xFFE8F0FE)
val BackgroundGray = Color(0xFFF8F9FA)
val TextGray = Color(0xFF5F6368)
val ExpenseRed = Color(0xFFD32F2F)
val ExpenseRedLight = Color(0xFFFFEBEE)
val IncomeGreen = Color(0xFF34A853)
val BadgeGreen = Color(0xFFE6F4EA)
data class TransactionData(
    val title: String,
    val time: String,
    val amount: String,
    val isIncome: Boolean,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    newNotesViewModel : NewNotesViewModel = viewModel(),
) {

    val newNotesUiState by newNotesViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        newNotesViewModel.sync()
        newNotesViewModel.loadNotes()
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bayarin",
                        color = BluePrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = BluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGray)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.NEW_NOTES) },
                containerColor = BluePrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        },
        bottomBar = {
            HomeBottomNavBar(navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item { SummaryCard() }
            item {
                QuickActionsRow(
                    onAddFriendClick = {
                        navController.navigate(Routes.TAMBAH_TEMAN)
                    }
                )
            }
            item { ExpenseChartCard() }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Riwayat Transaksi Terbaru", fontWeight = FontWeight.Medium, color = Color.DarkGray)
                    Text(text = "Lihat Semua", color = BluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            when {
                newNotesUiState.isLoading -> {
                    item { CircularProgressIndicator() }
                }

                newNotesUiState.error != "" -> {
                    item { Text(newNotesUiState.error) }
                } else -> {
                    items(newNotesUiState.notes){ notes ->
                        TransactionItem(
                            title = notes.category,
                            time = formatDate(notes.date),
                            amount = formatRupiah(notes.nominal),
                            isIncome = !notes.expense,
                            icon = getCategoryIcon(notes.category)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}

@Composable
fun SummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BluePrimary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "RINCIAN BULAN INI",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(text = "Pemasukan", color = Color.White, fontSize = 12.sp)
                    }
                    Text(text = "Rp 15.000.000", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(text = "Pengeluaran", color = Color.White, fontSize = 12.sp)
                    }
                    Text(text = "Rp 2.550.000", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(
    onAddFriendClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionItem(
            icon = Icons.Default.Person,
            title = "Tambah\nTeman",
            onClick = onAddFriendClick
        )
        ActionItem(
            icon = Icons.Default.Person,
            title = "Buat\nGrup",
            onClick = { /* TODO */ }
        )
        ActionItem(
            icon = Icons.Default.MailOutline,
            title = "Tagih\nTeman",
            onClick = { /* TODO */ }
        )
    }
}

@Composable
fun ActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .width(100.dp)
            .height(90.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = BluePrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ExpenseChartCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Pengeluaran Bulan Ini", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BadgeGreen)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "HEMAT", color = IncomeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Detail", color = BluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)) {
                val path = Path()
                val width = size.width
                val height = size.height
                val stepX = width / 5

                path.moveTo(0f, height * 0.7f)
                path.lineTo(stepX * 1, height * 0.4f)
                path.lineTo(stepX * 2, height * 0.8f)
                path.lineTo(stepX * 3, height * 0.1f)
                path.lineTo(stepX * 4, height * 0.6f)
                path.lineTo(width, height * 0.3f)

                drawPath(
                    path = path,
                    color = BluePrimary,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("M1", fontSize = 12.sp, color = TextGray)
                Text("M2", fontSize = 12.sp, color = TextGray)
                Text("M3", fontSize = 12.sp, color = TextGray)
                Text("M4", fontSize = 12.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                Text("M5", fontSize = 12.sp, color = TextGray)
            }
        }
    }
}

@Composable
fun TransactionItem(title: String, time: String, amount: String, isIncome: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isIncome) BlueLight else ExpenseRedLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isIncome) BluePrimary else ExpenseRed
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = time, color = TextGray, fontSize = 12.sp)
            }
            Text(
                text = amount,
                color = if (isIncome) BluePrimary else ExpenseRed,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun HomeBottomNavBar(
    navController: NavController
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                indicatorColor = BlueLight
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Group") },
            label = { Text("Group") },
            selected = false,
            onClick = {
                navController.navigate(Routes.GROUP) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Tambah") },
            label = { Text("Tambah") },
            selected = false,
            onClick = {
                navController.navigate(Routes.NEW_NOTES) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Pengingat") },
            label = { Text("Pengingat") },
            selected = false,
            onClick = {
                navController.navigate(Routes.REMINDER) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = {
                navController.navigate(Routes.PROFILE) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}


fun formatRupiah(value: Long): String {
    if (value == 0L) return ""

    return "Rp " + NumberFormat
        .getNumberInstance(Locale("id", "ID"))
        .format(value)
}

fun formatDate(timestamp: Long): String {

    val formatter = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("id", "ID")
    )

    return formatter.format(Date(timestamp))
}

fun getCategoryIcon(category: String): ImageVector {
    return when(category) {
        "Belanja" -> Icons.Default.ShoppingCart
        "Tagihan" -> Icons.Default.List
        "Kesehatan" -> Icons.Default.Favorite
        "Lain lain" -> Icons.Default.Add
        "Gaji" -> Icons.Default.AccountBox
        "Bonus" -> Icons.Default.Star
        "Jualan" -> Icons.Default.ShoppingCart
        "Hadiah" -> Icons.Default.ThumbUp
        "Makan" -> Icons.Default.ShoppingCart
        "Transport" -> Icons.Default.Place
        "Hiburan" -> Icons.Default.Face
        "Kos" -> Icons.Default.Home
        else -> Icons.Default.Add
    }
}