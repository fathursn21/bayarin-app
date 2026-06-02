package id.ac.pnm.bayarin_app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Warna background abu-abu terang
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // --- 1. BAGIAN HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bayarin",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D47A1)
            )
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    tint = Color(0xFF0D47A1)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

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
                    Text(
                        text = "F",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5F6368)
                    )
                }

                SmallFloatingActionButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp),
                    shape = CircleShape,
                    containerColor = Color(0xFF0D47A1),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Fathur",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Jarak dari nama ke menu card

        // --- 3. BAGIAN MENU CARD ---

        // Card 1: Riwayat & Pengaturan
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Tanpa bayangan
        ) {
            Column {
                MenuItem(
                    icon = Icons.Default.List,
                    iconTint = Color(0xFF5C6E9A),
                    iconBgColor = Color(0xFFE8EDF4),
                    title = "Riwayat Patungan"
                )
                // Garis pembatas (Divider)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color(0xFFF0F0F0)
                )
                MenuItem(
                    icon = Icons.Default.Settings,
                    iconTint = Color(0xFF5C6E9A),
                    iconBgColor = Color(0xFFE8EDF4),
                    title = "Pengaturan Akun"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: Pusat Bantuan
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            MenuItem(
                icon = Icons.Default.Info,
                iconTint = Color(0xFF0D47A1), // Biru lebih terang
                iconBgColor = Color(0xFFE3EDFA),
                title = "Pusat Bantuan"
            )
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
                iconTint = Color(0xFFD32F2F), // Merah
                iconBgColor = Color(0xFFFFEBEE), // Merah muda transparan
                title = "Keluar",
                titleColor = Color(0xFFD32F2F),
                showArrow = false // Sesuai desain, tombol keluar tidak pakai panah
            )
        }
    }
}

// --- FUNGSI BANTUAN UNTUK ITEM MENU ---
// Kita pisah di sini agar kodenya rapi dan bisa dipakai berulang
@Composable
fun MenuItem(
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    title: String,
    titleColor: Color = Color(0xFF1A1A1A),
    showArrow: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lingkaran background untuk ikon
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

        // Teks Menu
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor,
            modifier = Modifier.weight(1f) // Membuat teks memakan sisa ruang agar panah terdorong ke kanan
        )

        // Ikon Panah Kanan
        if (showArrow) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD)
            )
        }
    }
}