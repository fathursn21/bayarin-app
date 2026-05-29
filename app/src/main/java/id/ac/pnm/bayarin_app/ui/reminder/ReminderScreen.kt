package id.ac.pnm.bayarin_app.ui.reminder

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.ui.navigation.Routes

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
fun ReminderScreen(
    navController: NavController
) {
    //data dummy
    val reminderList = listOf(
        ReminderData("Mas Edwin", "M", "Kos Mojoarum", "Rp250.000", "2 hari yang lalu"),
        ReminderData("Rusdi", "R", "Kopag Han...", "Rp45.000", "5 hari yang lalu"),
        ReminderData("Agus Mas", "A", "Bundle Kur...", "Rp900.000", "5 hari yang lalu"),
        ReminderData("Kevin Rafael", "K", "Project Kampus", "Rp75.000", "1 minggu yang lalu"),
        ReminderData("Fathur", "F", "Patungan Futsal", "Rp20.000", "2 minggu yang lalu")
    )

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
                            imageVector = Icons.Outlined.Notifications,
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
                        text = "Pengingat",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kirim notifikasi ke teman mu yang nunggak",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }

            //daftar pengingat
            items(reminderList) { reminder ->
                ReminderCardItem(reminder = reminder)
            }
        }
    }
}

@Composable
fun ReminderCardItem(reminder: ReminderData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //Info Profil & Tagihan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Avatar + Nama + Grup
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AvatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = reminder.initial,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextDark
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = reminder.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextDark
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
                                text = reminder.groupName,
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                //Nominal & Waktu
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = reminder.amount,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = ExpenseRed
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reminder.time,
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = BlueLightBg
            )

            //Tombol WhatsApp & Notifikasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //WhatsApp
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WAGreenBg)
                        .clickable { /* TODO: Buka WA */ }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "WhatsApp",
                        tint = WAGreenText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp",
                        color = WAGreenText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                //Kirim Notifikasi
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NotifBlueBg)
                        .clickable { /*Notifikasi*/ }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Kirim Notifikasi",
                        tint = NotifBlueText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Kirim Notifikasi",
                        color = NotifBlueText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
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
            onClick = { /* TODO */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Pengingat") },
            label = { Text("Pengingat") },
            selected = true,
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
            onClick = { /* TODO */ }
        )
    }
}
