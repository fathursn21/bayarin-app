package id.ac.pnm.bayarin_app.ui.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import id.ac.pnm.bayarin_app.ui.auth.register.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
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

    fun loginUser(userUsername : String, userPassword : String){

    }
}