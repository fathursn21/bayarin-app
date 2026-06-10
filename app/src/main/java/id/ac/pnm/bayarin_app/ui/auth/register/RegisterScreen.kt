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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
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

    LaunchedEffect(registerUiState.isRegisterSuccess) {
        if (registerUiState.isRegisterSuccess) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.REGISTER) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // --- MENGGUNAKAN errorMessage ---
                    if (registerUiState.errorMessage.isNotEmpty()) {
                        Text(
                            text = registerUiState.errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // INPUT NAMA
                    Text(text = "Nama Lengkap", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = registerViewModel.userTypeName,
                        onValueChange = { registerViewModel.updateTypeName(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorContainerColor = Color(0xFFFFEBEE)
                        ),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nama", tint = Color(0xFFBDBDBD)) },
                        placeholder = { Text("Masukkan nama Anda", color = Color(0xFFBDBDBD), fontSize = 14.sp) },
                        singleLine = true,
                        isError = registerUiState.isInputNameEmpty
                    )
                    if (registerUiState.isInputNameEmpty) {
                        Text("Nama tidak boleh kosong", color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // INPUT EMAIL
                    Text(text = "Email", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = registerViewModel.userTypeEmail,
                        onValueChange = { registerViewModel.updateTypeEmail(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorContainerColor = Color(0xFFFFEBEE)
                        ),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFFBDBDBD)) },
                        placeholder = { Text("pengguna@gmail.com", color = Color(0xFFBDBDBD), fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = registerUiState.isInputEmailEmpty
                    )
                    if (registerUiState.isInputEmailEmpty) {
                        Text("Email tidak boleh kosong", color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // INPUT PASSWORD
                    Text(text = "Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = registerViewModel.userTypePassword,
                        onValueChange = { registerViewModel.updateTypePassword(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorContainerColor = Color(0xFFFFEBEE)
                        ),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFFBDBDBD)) },
                        placeholder = { Text("••••••••", color = Color(0xFFBDBDBD), fontSize = 14.sp) },
                        singleLine = true,
                        visualTransformation = if (registerUiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = registerUiState.IsInputPasswordEmpty,
                        trailingIcon = {
                            val image = if (registerUiState.passwordVisible) Icons.Filled.Face else Icons.Filled.Info
                            IconButton(onClick = { registerViewModel.updateVisiblePassword(!registerUiState.passwordVisible) }) {
                                Icon(imageVector = image, contentDescription = if (registerUiState.passwordVisible) "Sembunyikan password" else "Tampilkan password")
                            }
                        }
                    )
                    if (registerUiState.IsInputPasswordEmpty) {
                        Text("Password tidak boleh kosong", color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // INPUT NOMOR TELEPON
                    Text(text = "Nomor Telepon", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = registerViewModel.userTypeTelp,
                        onValueChange = { registerViewModel.updateTypeTelp(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorContainerColor = Color(0xFFFFEBEE)
                        ),
                        leadingIcon = { Icon(Icons.Default.Call, contentDescription = "Telepon", tint = Color(0xFFBDBDBD)) },
                        placeholder = { Text("081xxxxxxxxx", color = Color(0xFFBDBDBD), fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = registerUiState.IsInputTelpEmpty // --- MENGGUNAKAN IsInputTelpEmpty ---
                    )
                    if (registerUiState.IsInputTelpEmpty) {
                        Text("Nomor telepon tidak boleh kosong", color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // TOMBOL DAFTAR & LOADING
                    Button(
                        onClick = {
                            // Pastikan nama fungsinya di ViewModel adalah registerUser (huruf s ada)
                            registerViewModel.registerUser(
                                registerViewModel.userTypeName,
                                registerViewModel.userTypeEmail,
                                registerViewModel.userTypeTelp,
                                registerViewModel.userTypePassword
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                        enabled = !registerUiState.isLoading // --- MENGGUNAKAN isLoading agar tombol disable saat loading ---
                    ) {
                        if (registerUiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = "Daftar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

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