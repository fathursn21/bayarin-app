package id.ac.pnm.bayarin_app.ui.daftarNotes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.ui.home.BlueLight
import id.ac.pnm.bayarin_app.ui.home.BluePrimary
import id.ac.pnm.bayarin_app.ui.home.ExpenseRed
import id.ac.pnm.bayarin_app.ui.home.ExpenseRedLight
import id.ac.pnm.bayarin_app.ui.home.TextGray
import id.ac.pnm.bayarin_app.ui.home.TransactionItem
import id.ac.pnm.bayarin_app.ui.home.formatDate
import id.ac.pnm.bayarin_app.ui.home.formatRupiah
import id.ac.pnm.bayarin_app.ui.home.getCategoryIcon
import id.ac.pnm.bayarin_app.ui.navigation.Routes
import id.ac.pnm.bayarin_app.ui.newnotes.NewNotesViewModel

val BluePrimary = Color(0xFF0056D2)
val BlueLightBg = Color(0xFFF4F7FC)
val ExpenseRed = Color(0xFFC62828)
val TextGray = Color(0xFF70757A)
val TextDark = Color(0xFF0F172A)

//warna tombol
val WAGreenBg = Color(0xFFE8F5E9)
val WAGreenText = Color(0xFF2E7D32)
val NotifBlueBg = Color(0xFFE2EAFC)
val NotifBlueText = Color(0xFF1E3A8A)
val AvatarBg = Color(0xFFE2E8F0)

data class ReminderData(
    val name: String,
    val initial: String,
    val groupName: String,
    val amount: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarNotesScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    newNotesViewModel : NewNotesViewModel = viewModel()
) {
    val newNotesUiState by newNotesViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        newNotesViewModel.sync()
        newNotesViewModel.loadNotes(0)
    }

    Scaffold(
        containerColor = BlueLightBg,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlueLightBg)
            )
        },
        bottomBar = {
            ReminderBottomNavBar(navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "Daftar Transaksi",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "list daftar pengeluaranmu",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }

            //daftar item transaksi
            when {
                newNotesUiState.isLoading -> {
                    item { CircularProgressIndicator() }
                }

                newNotesUiState.error != "" -> {
                    item { Text(newNotesUiState.error) }
                } else -> {

                if (newNotesUiState.notes.size == 0){
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Kamu belum mencatat apapun", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }

                items(newNotesUiState.notes){ notes ->

                    TransactionItem(
                        id = notes.id,
                        title = notes.category,
                        time = formatDate(notes.date),
                        amount = formatRupiah(notes.nominal),
                        isIncome = !notes.expense,
                        icon = getCategoryIcon(notes.category),
                        note = notes.note,
                        onClick = { noteId ->
                            navController.navigate("${Routes.DETAIL_NOTES}/$noteId")
                        }
                    )
                }
            }
            }
        }
    }
}

@Composable
fun ReminderBottomNavBar(
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
            onClick = {
                navController.navigate(Routes.HOME) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
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
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                indicatorColor = NotifBlueBg
            )
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
