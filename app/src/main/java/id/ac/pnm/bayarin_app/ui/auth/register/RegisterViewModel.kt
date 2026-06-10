package id.ac.pnm.bayarin_app.ui.auth.register

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import id.ac.pnm.bayarin_app.data.model.Users
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private var database: DatabaseReference = Firebase.database.reference

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    var userTypeName by mutableStateOf("")
        private set

    fun updateTypeName(userType: String) {
        userTypeName = userType
    }

    var userTypeEmail by mutableStateOf("")
        private set

    fun updateTypeEmail(userType: String) {
        userTypeEmail = userType
    }

    var userTypeTelp by mutableStateOf("")
        private set

    fun updateTypeTelp(userType: String) {
        userTypeTelp = userType
    }

    var userTypePassword by mutableStateOf("")
        private set

    fun updateTypePassword(userType: String) {
        userTypePassword = userType
    }

    fun updateVisiblePassword(status: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(passwordVisible = status)
        }
    }

    fun registerUser(userName: String, userEmail: String, userTelp: String, userPassword: String) {

        // 1. Reset error dan mulai efek Loading
        _uiState.update { currentState ->
            currentState.copy(
                isInputNameEmpty = false,
                isInputEmailEmpty = false,
                IsInputTelpEmpty = false,
                IsInputPasswordEmpty = false,
                errorMessage = "",
                isLoading = true
            )
        }

        // 2. PERTAHANAN LAPIS PERTAMA: Validasi Input (Menggunakan isBlank agar spasi ditolak)
        if (userName.isBlank() || userEmail.isBlank() || userTelp.isBlank() || userPassword.isBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isInputNameEmpty = userName.isBlank(),
                    isInputEmailEmpty = userEmail.isBlank(),
                    IsInputTelpEmpty = userTelp.isBlank(),
                    IsInputPasswordEmpty = userPassword.isBlank(),
                    errorMessage = "Semua kolom wajib diisi!",
                    isLoading = false // Matikan loading karena gagal validasi
                )
            }
            return // Hentikan eksekusi, jangan kirim data ke Firebase
        }

        // 3. PERTAHANAN LAPIS KEDUA: Try-Catch dan Firebase Handler
        try {
            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")

                        val uid = auth.currentUser?.uid ?: ""
                        val user = Users(
                            id = uid,
                            name = userName,
                            nameLowerCase = userName.lowercase(),
                            email = userEmail,
                            telp = userTelp
                        )

                        // Simpan data tambahan ke Realtime Database
                        database.child("users").child(uid).setValue(user).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Log.d(TAG, "database.child:success")

                                // Berhasil semua! Update state pindah ke Home
                                _uiState.update { it.copy(isRegisterSuccess = true, isLoading = false) }
                            } else {
                                // CATCH: Gagal menyimpan data ke database
                                val dbException = dbTask.exception
                                Log.w(TAG, "database.child:failure", dbException)
                                _uiState.update {
                                    it.copy(
                                        errorMessage = dbException?.localizedMessage ?: "Gagal menyimpan data pengguna",
                                        isLoading = false
                                    )
                                }
                            }
                        }

                    } else {
                        // CATCH: Gagal membuat akun (misal email sudah dipakai, password kurang dari 6 huruf)
                        val authException = task.exception
                        Log.w(TAG, "createUserWithEmail:failure", authException)
                        _uiState.update {
                            it.copy(
                                errorMessage = authException?.localizedMessage ?: "Pendaftaran gagal. Coba email lain.",
                                isLoading = false
                            )
                        }
                    }
                }
        } catch (e: Exception) {
            // CATCH SISTEM: Menangkap error tak terduga (crash)
            Log.e(TAG, "Register Error", e)
            _uiState.update {
                it.copy(
                    errorMessage = e.localizedMessage ?: "Terjadi kesalahan sistem saat mendaftar.",
                    isLoading = false
                )
            }
        }
    }
}