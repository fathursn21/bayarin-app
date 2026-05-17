package id.ac.pnm.bayarin_app.ui.auth.login

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import id.ac.pnm.bayarin_app.ui.auth.register.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
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
        if (userUsername.isNotEmpty() && userPassword.isNotEmpty()){

            auth = Firebase.auth

            auth.signInWithEmailAndPassword(userUsername, userPassword).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "signInWithEmail:success")
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                }
            }

        } else if (userUsername.isEmpty()){
            _uiState.update { currentState ->
                currentState.copy(
                    isInputUsernameEmpty = true,
                )
            }
        } else if (userTypePassword.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isInputPasswordEmpty = true,
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isInputUsernameEmpty = true,
                    isInputPasswordEmpty = true,
                )
            }
        }
    }
}