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

        _uiState.update { currentState ->
            currentState.copy(
                isInputNameEmpty = userName.isEmpty(),
                isInputEmailEmpty = userEmail.isEmpty(),
                IsInputTelpEmpty = userTelp.isEmpty(),
                IsInputPasswordEmpty = userPassword.isEmpty(),
                errorMessage = "",
                isLoading = true
            )
        }

        if (userName.isNotEmpty() && userEmail.isNotEmpty() && userTelp.isNotEmpty() && userPassword.isNotEmpty()) {

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

                                // Update state pindah ke Home
                                _uiState.update { it.copy(isRegisterSuccess = true, isLoading = false) }
                            } else {
                                Log.w(TAG, "database.child:failure", dbTask.exception)
                                _uiState.update { it.copy(errorMessage = "Gagal menyimpan data pengguna", isLoading = false) }
                            }
                        }

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        _uiState.update { it.copy(errorMessage = task.exception?.localizedMessage ?: "Pendaftaran gagal", isLoading = false) }
                    }
                }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}