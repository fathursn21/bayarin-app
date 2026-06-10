package id.ac.pnm.bayarin_app.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import id.ac.pnm.bayarin_app.data.model.Users
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    // 1. Menyiapkan "Kabel" ke Firebase Auth dan Realtime Database
    private var auth: FirebaseAuth = Firebase.auth
    private var database: DatabaseReference = Firebase.database.reference

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // 2. Fungsi init akan otomatis dijalankan saat halaman Profile dibuka
    init {
        fetchUserData()
    }

    // 3. Logika inti untuk menarik data dari Firebase
    private fun fetchUserData() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            // Mencari data di tabel "users" yang cocok dengan UID yang sedang login
            database.child("users").child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Mengubah data mentah Firebase menjadi bentuk 'Users'
                    val user = snapshot.getValue(Users::class.java)

                    if (user != null) {
                        // Memasukkan data ke wadah UiState yang sudah Anda buat kemarin!
                        _uiState.update { currentState ->
                            currentState.copy(
                                user = user
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("ProfileViewModel", "Gagal mengambil data user", error.toException())
                }
            })
        }
    }

    fun logout() {
        auth.signOut()

        _uiState.update { currentState ->
            currentState.copy(
                isLogout = true
            )
        }
    }
}