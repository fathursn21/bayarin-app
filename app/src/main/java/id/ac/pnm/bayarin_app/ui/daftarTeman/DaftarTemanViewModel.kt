package id.ac.pnm.bayarin_app.ui.daftarTeman

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import id.ac.pnm.bayarin_app.data.model.Users
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "DaftarTemanViewModel"

class DaftarTemanViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(DaftarTemanUiState())
    val uiState: StateFlow<DaftarTemanUiState> = _uiState.asStateFlow()

    private val currentUid get() = auth.currentUser?.uid.orEmpty()

    init {
        fetchDaftarTeman()
    }

    fun fetchDaftarTeman() {
        if (currentUid.isEmpty()) return
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }

        viewModelScope.launch {
            try {
                //semua ID teman dari node /users/{uid}/friends
                val friendsSnapshot = dbRef.child("users")
                    .child(currentUid)
                    .child("friends")
                    .get()
                    .await()

                val friendIds = friendsSnapshot.children.mapNotNull { it.key }

                if (friendIds.isEmpty()) {
                    _uiState.update { it.copy(friendsList = emptyList(), isLoading = false) }
                    return@launch
                }

                //data detail tiap user berdasarkan ID
                val deferredFriends = friendIds.map { id ->
                    async {
                        dbRef.child("users").child(id).get().await().getValue(Users::class.java)
                    }
                }

                //filter data yang null
                val fullFriendsList = deferredFriends.awaitAll().filterNotNull()

                _uiState.update {
                    it.copy(friendsList = fullFriendsList, isLoading = false)
                }
                Log.d(TAG, "fetchDaftarTeman: Berhasil memuat ${fullFriendsList.size} profil teman")

            } catch (e: Exception) {
                Log.w(TAG, "fetchDaftarTeman: Gagal", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal memuat teman: ${e.message}")
                }
            }
        }
    }

    fun hapusTeman(targetUid: String) {
        if (currentUid.isEmpty() || targetUid.isEmpty()) return

        viewModelScope.launch {
            try {
                // memberikan nilai null pada updateChildren
                val updates = hashMapOf<String, Any?>(
                    "/users/$currentUid/friends/$targetUid" to null,
                    "/users/$targetUid/friends/$currentUid" to null
                )

                dbRef.updateChildren(updates).await()

                //memperbarui state UI secara lokal tanpa reload dari server
                _uiState.update { currentState ->
                    currentState.copy(
                        friendsList = currentState.friendsList.filter { it.id != targetUid }
                    )
                }
                Log.d(TAG, "hapusTeman: Berhasil menghapus teman dengan ID: $targetUid")

            } catch (e: Exception) {
                Log.w(TAG, "hapusTeman: Gagal", e)
                _uiState.update {
                    it.copy(errorMessage = "Gagal menghapus teman: ${e.message}")
                }
            }
        }
    }
}