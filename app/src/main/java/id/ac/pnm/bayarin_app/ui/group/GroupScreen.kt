package id.ac.pnm.bayarin_app.ui.group

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.data.model.Groups
import id.ac.pnm.bayarin_app.ui.home.formatRupiah
import id.ac.pnm.bayarin_app.ui.navigation.Routes
import id.ac.pnm.bayarin_app.ui.newnotes.NewNotesViewModel

val BluePrimary = Color(0xFF0056D2)
val BlueLightBg = Color(0xFFF4F7FC)
val BlueIconBg = Color(0xFFE2EAFC)
val ExpenseRed = Color(0xFFD32F2F)
val TextGray = Color(0xFF5F6368)
val LightGrayBorder = Color(0xFFEAEAEA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    navController: NavController,
    viewModel: GroupViewModel = viewModel(),
    newNotesViewModel : NewNotesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        newNotesViewModel.sync()
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
                    IconButton(onClick = { navController.navigate(Routes.NOTIFIKASI) }) {
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
            GroupBottomNavBar(navController)
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
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                ) {
                    item {
                        Button(
                            onClick = { navController.navigate(Routes.TAMBAH_GROUP) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Text(
                                text = "Tambah Group",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    if (uiState.groups.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Kamu belum bergabung di grup manapun.", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        // Iterasi list grup asli dari Realtime Database
                        items(uiState.groups) { group ->
                            GroupCardItem(
                                group = group,
                                onClick = {
                                    navController.navigate("${Routes.DETAIL_TRANSAKSI_GROUP}/${group.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCardItem(
    group: Groups,
    onClick: () -> Unit
) {
    val totalNominal = group.members.values.sum()
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, LightGrayBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BlueIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = group.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${group.members.size} Orang",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = LightGrayBorder
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (totalNominal > 0) "TOTAL PATUNGAN" else "BELUM ADA TRANSAKSI",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = formatRupiah(totalNominal),
                    color = if (totalNominal > 0) BluePrimary else TextGray,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun GroupBottomNavBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
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
            selected = true,
            onClick = { /* TODO: Navigasi Group jika diperlukan */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                indicatorColor = BlueIconBg
            )
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

