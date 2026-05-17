package id.ac.pnm.bayarin_app.ui.auth.register

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import id.ac.pnm.bayarin_app.data.model.Users
import id.ac.pnm.bayarin_app.ui.auth.login.LoginScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    var userTypeName by mutableStateOf("")
        private set

    fun updateTypeName(userType: String){
        userTypeName = userType
    }

    var userTypeEmail by mutableStateOf("")
        private set

    fun updateTypeEmail(userType: String){
        userTypeEmail = userType
    }

    var userTypeTelp by mutableStateOf("")
        private set

    fun updateTypeTelp(userType: String){
        userTypeTelp = userType
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

    fun regiterUser(userName : String, userEmail : String, userTelp : String, userPassword : String){
        if (userName.isNotEmpty() && userEmail.isNotEmpty() && userTelp.isNotEmpty()){

            auth = Firebase.auth
            database = Firebase.database.reference

            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")

                        val uid = auth.currentUser?.uid

                        val user = Users(
                            id = uid ?: "",
                            name = userName,
                            email = userEmail,
                            telp = userTelp
                        )

                        database.child("users").child(uid ?: "").setValue(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "database.child:success")
                            } else {
                                Log.w(TAG, "database.child:failure", task.exception)
                            }
                        }

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    }
                }

        } else if (userName.isEmpty()){
            _uiState.update { currentState ->
                currentState.copy(
                    isInputNameEmpty = true,
                )
            }
        } else if (userEmail.isEmpty()){
            _uiState.update { currentState ->
                currentState.copy(
                    isInputEmailEmpty = true,
                )
            }
        } else if (userTelp.isEmpty()){
            _uiState.update { currentState ->
                currentState.copy(
                    IsInputTelpEmpty = true,
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isInputNameEmpty = true,
                    isInputEmailEmpty = true,
                    IsInputTelpEmpty = true,
                )
            }
        }

    }

}