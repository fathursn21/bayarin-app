package id.ac.pnm.bayarin_app.ui.auth.login

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    var userTypeUsername by mutableStateOf("")
        private set

    fun updateTypeUsername(userType: String){
        userTypeUsername = userType
    }

    var userTypePassword by mutableStateOf("")
        private set

    fun updateTypePassword(userType: String){
        userTypePassword = userType
    }

    fun updateVisiblePassword(status : Boolean){
        _uiState.update { currentState ->
            currentState.copy(
                passwordVisible = status
            )
        }
    }

    fun loginUser(userUsername : String, userPassword : String){
        // 1. Reset state error sebelum mencoba login
        _uiState.update { currentState ->
            currentState.copy(
                isInputUsernameEmpty = false,
                isInputPasswordEmpty = false,
                errorMessage = ""
            )
        }

        // 2. PERTAHANAN LAPIS PERTAMA: Validasi Input Kosong
        if (userUsername.isBlank() || userPassword.isBlank()){
            _uiState.update { currentState ->
                currentState.copy(
                    isInputUsernameEmpty = userUsername.isBlank(),
                    isInputPasswordEmpty = userPassword.isBlank(),
                    errorMessage = "Email dan Password tidak boleh kosong!"
                )
            }
            return // Hentikan eksekusi kode di sini, jangan kirim ke Firebase
        }

        // 3. PERTAHANAN LAPIS KEDUA: Try-Catch dan Firebase Handler
        try {
            auth = Firebase.auth
            auth.signInWithEmailAndPassword(userUsername, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        // Jika berhasil
                        Log.d(TAG, "signInWithEmail:success")
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoginSuccess = true,
                                errorMessage = ""
                            )
                        }
                    } else {
                        // CATCH FIREBASE: Menangkap pesan error dari server (misal: sandi salah/email tidak ada)
                        val exception = task.exception
                        Log.w(TAG, "signInWithEmail:failure", exception)

                        val errorMsg = exception?.localizedMessage ?: "Gagal login. Periksa kembali data Anda."

                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoginSuccess = false,
                                errorMessage = errorMsg
                            )
                        }
                    }
                }
        } catch (e: Exception) {
            // CATCH SISTEM: Menangkap error tak terduga (misal aplikasi nge-crash saat mengolah data)
            Log.e(TAG, "Login Error", e)
            _uiState.update { currentState ->
                currentState.copy(
                    isLoginSuccess = false,
                    errorMessage = e.localizedMessage ?: "Terjadi kesalahan sistem."
                )
            }
        }
    }
}