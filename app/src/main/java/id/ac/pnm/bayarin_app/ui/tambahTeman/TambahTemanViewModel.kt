package id.ac.pnm.bayarin_app.ui.tambahTeman

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import id.ac.pnm.bayarin_app.data.model.Users
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "TambahTemanViewModel"

class TambahTemanViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(TambahTemanUiState())
    val uiState: StateFlow<TambahTemanUiState> = _uiState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >= 2) searchUsers(query) else clearResults()
    }

    private val currentUid get() = auth.currentUser?.uid.orEmpty()

    init {
        loadFriendIds()
    }


    fun loadFriendIds() {
        if (currentUid.isEmpty()) return
        _uiState.update { it.copy(isLoadingFriends = true) }

        viewModelScope.launch {
            try {
                val snapshot = dbRef.child("users")
                    .child(currentUid)
                    .child("friends")
                    .get()
                    .await()

                // Ambil key (ID teman) dari setiap child
                val ids = snapshot.children.mapNotNull { it.key }.toSet()

                _uiState.update { it.copy(friendIds = ids, isLoadingFriends = false) }

                Log.d(TAG, "loadFriendIds: berhasil memuat ${ids.size} teman")
            } catch (e: Exception) {
                Log.w(TAG, "loadFriendIds: gagal", e)
                _uiState.update {
                    it.copy(
                        isLoadingFriends = false,
                        isErrorSearch = true,
                        errorMessage = "Gagal memuat daftar teman: ${e.message}"
                    )
                }
            }
        }
    }


    private fun clearResults() {
        _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
    }

    private fun searchUsers(query: String) {
        _uiState.update { it.copy(isSearching = true, isErrorSearch = false) }

        viewModelScope.launch {
            try {
                val lower = query.lowercase()

                // Cari by nameLower
                val byNameSnapshot = dbRef.child("users")
                    .orderByChild("nameLowerCase")
                    .startAt(lower)
                    .endAt(lower + "\uf8ff")
                    .get()
                    .await()

                // Cari by email
                val byEmailSnapshot = dbRef.child("users")
                    .orderByChild("email")
                    .equalTo(query.lowercase())
                    .get()
                    .await()

                val byNameList = byNameSnapshot.children.mapNotNull { it.getValue(Users::class.java) }
                val byEmailList = byEmailSnapshot.children.mapNotNull { it.getValue(Users::class.java) }

                val combined = (byNameList + byEmailList)
                    .distinctBy { it.id }
                    .filter { it.id != currentUid }

                _uiState.update { it.copy(searchResults = combined, isSearching = false) }
                Log.d(TAG, "searchUsers: ditemukan ${combined.size} user")
            } catch (e: Exception) {
                Log.w(TAG, "searchUsers: gagal", e)
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        isErrorSearch = true,
                        errorMessage = "Pencarian gagal: ${e.message}"
                    )
                }
            }
        }
    }


    fun tambahTeman(targetUser: Users) {
        if (currentUid.isEmpty() || targetUser.id.isEmpty()) return

        _uiState.update { it.copy(pendingIds = it.pendingIds + targetUser.id) }

        viewModelScope.launch {
            try {
                //profil user sendiri terlebih dahulu untuk tahu nama pengirim notif
                val myProfileSnapshot = dbRef.child("users").child(currentUid).get().await()
                val myName = myProfileSnapshot.child("name").getValue(String::class.java) ?: "Seseorang"

                val friendData = mapOf("addedAt" to ServerValue.TIMESTAMP)

                //Generate ID Notifikasi untuk user target
                val notifId = dbRef.child("users").child(targetUser.id).child("notifications").push().key.orEmpty()

                //objek data notifikasi pertemanan baru
                val notificationData = mapOf(
                    "id" to notifId,
                    "title" to "Pertemanan Baru",
                    "message" to "$myName telah menambahkan Anda sebagai teman.",
                    "timestamp" to System.currentTimeMillis(),
                    "isRead" to false,
                    "groupId" to ""
                )

                val updates = hashMapOf<String, Any>(
                    "/users/$currentUid/friends/${targetUser.id}" to friendData,
                    "/users/${targetUser.id}/friends/$currentUid" to friendData,
                    // Masukkan notifikasi ke akun teman yang ditambahkan
                    "/users/${targetUser.id}/notifications/$notifId" to notificationData
                )

                dbRef.updateChildren(updates).await()

                _uiState.update {
                    it.copy(
                        friendIds      = it.friendIds + targetUser.id,
                        pendingIds     = it.pendingIds - targetUser.id,
                        isAddSuccess   = true,
                        successMessage = "${targetUser.name} berhasil ditambahkan!"
                    )
                }
                Log.d(TAG, "tambahTeman: berhasil menambah ${targetUser.name} dan mengirim notifikasi")
            } catch (e: Exception) {
                Log.w(TAG, "tambahTeman: gagal", e)
                _uiState.update {
                    it.copy(
                        pendingIds       = it.pendingIds - targetUser.id,
                        isErrorAddFriend = true,
                        errorMessage     = "Gagal menambah teman: ${e.message}"
                    )
                }
            }
        }
    }


    fun resetSuccessFlag() {
        _uiState.update { it.copy(isAddSuccess = false, successMessage = "") }
    }

    fun resetErrorFlag() {
        _uiState.update {
            it.copy(
                isErrorSearch    = false,
                isErrorAddFriend = false,
                errorMessage     = ""
            )
        }
    }
}