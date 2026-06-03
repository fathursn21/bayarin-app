package id.ac.pnm.bayarin_app.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun logout() {
        auth = Firebase.auth

        auth.signOut()

        _uiState.update { currentState ->
            currentState.copy(
                isLogout = true
            )
        }

    }

}