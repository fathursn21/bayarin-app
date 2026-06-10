package id.ac.pnm.bayarin_app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.ui.navigation.Routes

// Definisi warna untuk Bottom Nav Bar
val BluePrimary = Color(0xFF0056D2)
val BlueLight = Color(0xFFE8F0FE)
val BlueLightBg = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
) {
    val profileUiState by profileViewModel.uiState.collectAsState()

    if (profileUiState.isLogout) {
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
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
                    IconButton(onClick = {navController.navigate(Routes.NOTIFIKASI)}) {
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
            ProfileBottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {

            // Jarak kecil dari TopBar ke Avatar
            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. BAGIAN AVATAR DAN NAMA ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFE0E5EC), CircleShape)
                    ) {
                        // Mengambil huruf pertama dari nama untuk dijadikan Avatar
                        Text(
                            text = if (profileUiState.user.name.isNotEmpty()) {
                                profileUiState.user.name.take(1).uppercase()
                            } else {
                                "-" // Tampilkan strip jika nama belum termuat
                            },
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5F6368)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Menampilkan nama lengkap dari database
                Text(
                    text = if (profileUiState.user.name.isNotEmpty()) {
                        profileUiState.user.name
                    } else {
                        "Memuat nama..."
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. BAGIAN MENU CARD ---

            // Card 1: Riwayat & Pengaturan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    // Menu 1: Riwayat Patungan
                    MenuItem(
                        icon = Icons.Default.List,
                        iconTint = Color(0xFF5C6E9A),
                        iconBgColor = Color(0xFFE8EDF4),
                        title = "Riwayat Patungan"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color(0xFFF0F0F0)
                    )

                    // Menu 2: Daftar Teman
                    MenuItem(
                        // Menggunakan icon Person untuk Daftar Teman
                        icon = Icons.Default.Person,
                        iconTint = Color(0xFF5C6E9A),
                        iconBgColor = Color(0xFFE8EDF4),
                        title = "Daftar Teman",
                        onClick = {
                            navController.navigate(Routes.DAFTAR_TEMAN) {
                                launchSingleTop = true
                            }
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color(0xFFF0F0F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: Keluar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                MenuItem(
                    icon = Icons.Default.ExitToApp,
                    iconTint = Color(0xFFD32F2F),
                    iconBgColor = Color(0xFFFFEBEE),
                    title = "Keluar",
                    titleColor = Color(0xFFD32F2F),
                    showArrow = false,
                    onClick = { profileViewModel.logout() }
                )
            }
        }
    }
}

// --- FUNGSI BANTUAN UNTUK ITEM MENU ---
@Composable
fun MenuItem(
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    title: String,
    titleColor: Color = Color(0xFF1A1A1A),
    showArrow: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )

        if (showArrow) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD)
            )
        }
    }
}

@Composable
fun ProfileBottomNavBar(
    navController: NavController
) {
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
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BluePrimary,
                selectedTextColor = BluePrimary,
                indicatorColor = BlueLight
            )
        )
    }
}