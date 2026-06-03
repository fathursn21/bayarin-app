package id.ac.pnm.bayarin_app.ui.newnotes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.ui.navigation.Routes
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNotesScreen(
    navController : NavController,
    newNotesViewModel : NewNotesViewModel = viewModel(),
) {
    val newNotesUiState by newNotesViewModel.uiState.collectAsState()

    val formatter = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("id", "ID")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // --- 1. BAGIAN HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF4F6F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { navController.navigate(Routes.HOME) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF1A1A1A)
                    )
                }
            }

            Text(
                text = "Bayarin",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D47A1)
            )

            IconButton(onClick = { /* TODO: Notifikasi */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    tint = Color(0xFF0D47A1)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- 2. BAGIAN PENGELUARAN BARU (INPUT NOMINAL) ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PENGELUARAN BARU",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5F6368)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = formatRupiah(newNotesViewModel.userTypeNominal),
                onValueChange = { newNotesViewModel.updateTypeNominal(it) },
                modifier = Modifier.fillMaxWidth(),
                prefix = {
                    Text(
                        text = "Rp",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80868B)
                    )
                },
                placeholder = {
                    Text(
                        text = "0",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD2D2D2)
                    )
                },
                textStyle = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF8F9FA),
                    unfocusedContainerColor = Color(0xFFF8F9FA)
                ),
                shape = RoundedCornerShape(12.dp)
            )

        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. BAGIAN KATEGORI ---
        Text(
            text = "Kategori",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryItem(icon = Icons.Default.ShoppingCart, title = "Makan", isSelected = newNotesViewModel.updateUserCategory == "Makan", { newNotesViewModel.updateSelectCategory("Makan") })
            CategoryItem(icon = Icons.Default.Place, title = "Transport", isSelected = newNotesViewModel.updateUserCategory == "Transport", { newNotesViewModel.updateSelectCategory("Transport") })
            CategoryItem(icon = Icons.Default.Face, title = "Hiburan", isSelected = newNotesViewModel.updateUserCategory == "Hiburan", { newNotesViewModel.updateSelectCategory("Hiburan") })
            CategoryItem(icon = Icons.Default.Home, title = "Kos", isSelected = newNotesViewModel.updateUserCategory == "Kos", { newNotesViewModel.updateSelectCategory("Kos") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryItem(icon = Icons.Default.ShoppingCart, title = "Belanja", isSelected = newNotesViewModel.updateUserCategory == "Belanja", { newNotesViewModel.updateSelectCategory("Belanja") })
            CategoryItem(icon = Icons.Default.List, title = "Tagihan", isSelected = newNotesViewModel.updateUserCategory == "Tagihan", { newNotesViewModel.updateSelectCategory("Tagihan") })
            CategoryItem(icon = Icons.Default.Favorite, title = "Kesehatan", isSelected = newNotesViewModel.updateUserCategory == "Kesehatan", { newNotesViewModel.updateSelectCategory("Kesehatan") })
            CategoryItem(icon = Icons.Default.Add, title = "Lain lain", isSelected = newNotesViewModel.updateUserCategory == "Lain lain", { newNotesViewModel.updateSelectCategory("Lain lain") })
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 4. BAGIAN TANGGAL DAN CATATAN ---
        // Kotak Tanggal

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    newNotesViewModel.updateVisibleDatepicker(true)
                }
                .border(1.dp, Color(0xFFE0E5EC), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Tanggal",
                tint = Color(0xFF5F6368)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = formatter.format(
                    Date(newNotesViewModel.updateUserDate)
                ),
                color = Color(0xFF1A1A1A),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kotak Catatan

        OutlinedTextField(
            value = newNotesViewModel.userTypeNote,
            onValueChange = {
                newNotesViewModel.updateTypeNote(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Catatan (opsional)",
                    color = Color(0xFFBDBDBD)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFFBDBDBD)
                )
            },
            singleLine = true,
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8F9FA),
                unfocusedContainerColor = Color(0xFFF8F9FA),
                focusedBorderColor = Color(0xFFE0E5EC),
                unfocusedBorderColor = Color(0xFFE0E5EC)
            )
        )

        // Mendorong tombol "Tambah" agar selalu berada di posisi paling bawah
        Spacer(modifier = Modifier.weight(1f))

        // --- 5. TOMBOL TAMBAH ---
        Button(
            onClick = { newNotesViewModel.addNotes(newNotesViewModel.userTypeNominal, newNotesViewModel.updateUserCategory, newNotesViewModel.updateUserDate, newNotesViewModel.userTypeNote) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp), // Sudut sangat melengkung menyerupai pil
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)) // Warna Biru
        ) {
            Text(
                text = "Tambah",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    if (newNotesViewModel.showDatepicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis =
                newNotesViewModel.updateUserDate
        )

        DatePickerDialog(
            onDismissRequest = {
                newNotesViewModel.updateVisibleDatepicker(false)
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let {
                            newNotesViewModel.updateSelectedDate(it)
                        }

                        newNotesViewModel.updateVisibleDatepicker(false)
                    }
                ) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        newNotesViewModel.updateVisibleDatepicker(false)
                    }
                ) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

}

// --- FUNGSI BANTUAN UNTUK TOMBOL KATEGORI ---
@Composable
fun CategoryItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFF2F80ED) else Color.White
    val contentColor = if (isSelected) Color.White else Color(0xFF5F6368)
    val borderColor = if (isSelected) Color.Transparent else Color(0xFFE0E5EC)

    Column(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .width(72.dp)
            .height(72.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = contentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun formatRupiah(value: String): String {
    if (value.isEmpty()) return ""

    return NumberFormat
        .getNumberInstance(Locale("id", "ID"))
        .format(value.toLong())
}
