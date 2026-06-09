package id.ac.pnm.bayarin_app.ui.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.ac.pnm.bayarin_app.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    registerViewModel: RegisterViewModel = viewModel()
) {
    val registerUiState by registerViewModel.uiState.collectAsState()

    // Efek Gradient untuk Background (Biru muda ke putih)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFEBF4FA), Color.White),
        startY = 0f,
        endY = 1500f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // --- 1. TEKS DAFTAR ---
            Text(
                text = "Daftar Akun",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Buat akun baru untuk mulai kelola keuanganmu.",
                fontSize = 14.sp,
                color = Color(0xFF5F6368),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. KOTAK FORM (CARD) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // INPUT NAMA
                    Text(text = "Nama Lengkap", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = registerViewModel.userTypeName,
                        onValueChange = { registerViewModel.updateTypeName(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nama", tint = Color(0xFFBDBDBD)) },
                        placeholder = { Text("Masukkan nama Anda", color = Color(0xFFBDBDBD), fontSize = 14.sp) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // INPUT EMAIL
                    Text(text = "Email", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = registerViewModel.userTypeEmail,
                        onValueChange = { registerViewModel.updateTypeEmail(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFFBDBDBD)) },
                        placeholder = { Text("pengguna@gmail.com", color = Color(0xFFBDBDBD), fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // INPUT PASSWORD
                    Text(text = "Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = registerViewModel.userTypePassword,
                        onValueChange = { registerViewModel.updateTypePassword(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFFBDBDBD)) },
                        placeholder = { Text("••••••••", color = Color(0xFFBDBDBD), fontSize = 14.sp) },
                        singleLine = true,
                        visualTransformation = if (registerUiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // INPUT NOMOR TELEPON
                    Text(text = "Nomor Telepon", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = registerViewModel.userTypeTelp,
                        onValueChange = { registerViewModel.updateTypeTelp(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Default.Call, contentDescription = "Telepon", tint = Color(0xFFBDBDBD)) },
                        placeholder = { Text("081xxxxxxxxx", color = Color(0xFFBDBDBD), fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // TOMBOL DAFTAR
                    Button(
                        onClick = { registerViewModel.regiterUser(registerViewModel.userTypeName, registerViewModel.userTypeEmail, registerViewModel.userTypeTelp, registerViewModel.userTypePassword) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                    ) {
                        Text(text = "Daftar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 3. TEKS SUDAH PUNYA AKUN ---
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Sudah punya akun? ", fontSize = 14.sp, color = Color(0xFF5F6368))
                Text(
                    text = "Login di sini",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2F80ED),
                    modifier = Modifier.clickable {navController.navigate(Routes.LOGIN)}
                )
            }
        }
    }
}