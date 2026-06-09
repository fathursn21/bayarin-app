package id.ac.pnm.bayarin_app.ui.tambahGroup

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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "TambahGroupViewModel"

class TambahGroupViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val currentUid get() = auth.currentUser?.uid.orEmpty()

    private val _uiState = MutableStateFlow(TambahGroupUiState())
    val uiState: StateFlow<TambahGroupUiState> = _uiState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    init {
        loadActualFriends()
    }

    // semua data teman asli
    private fun loadActualFriends() {
        if (currentUid.isEmpty()) return
        _uiState.update { it.copy(isLoadingFriends = true) }

        viewModelScope.launch {
            try {
                val friendsSnapshot = dbRef.child("users")
                    .child(currentUid)
                    .child("friends")
                    .get()
                    .await()

                val friendIds = friendsSnapshot.children.mapNotNull { it.key }

                if (friendIds.isEmpty()) {
                    _uiState.update { it.copy(isLoadingFriends = false) }
                    return@launch
                }

                //detail data profil tiap teman
                val deferredFriends = friendIds.map { id ->
                    async {
                        dbRef.child("users").child(id).get().await().getValue(Users::class.java)
                    }
                }
                val fullFriendsList = deferredFriends.awaitAll().filterNotNull()

                _uiState.update {
                    it.copy(
                        allFriends = fullFriendsList,
                        filteredFriends = fullFriendsList,
                        isLoadingFriends = false
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "loadActualFriends: Gagal", e)
                _uiState.update { it.copy(isLoadingFriends = false, errorMessage = e.message.orEmpty()) }
            }
        }
    }

    //Hfilter pencarian di pop-up dialog
    fun updateSearchQuery(query: String) {
        searchQuery = query
        _uiState.update { currentState ->
            val filtered = if (query.isEmpty()) {
                currentState.allFriends
            } else {
                currentState.allFriends.filter {
                    it.name.contains(query, ignoreCase = true) || it.email.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(filteredFriends = filtered)
        }
    }

    //tambah/hapus teman ke list anggota grup
    fun toggleFriendSelection(user: Users) {
        _uiState.update { currentState ->
            val isSelected = currentState.selectedFriends.contains(user)
            val updatedSelected = if (isSelected) {
                currentState.selectedFriends - user
            } else {
                currentState.selectedFriends + user
            }
            currentState.copy(selectedFriends = updatedSelected)
        }
    }

    //Simpan grup baru ke Firebase Realtime Database
    fun createGroup(groupName: String, nominals: Map<String, Long>) {
        val selected = _uiState.value.selectedFriends
        if (currentUid.isEmpty() || groupName.isBlank() || selected.isEmpty()) return

        _uiState.update { it.copy(isCreatingGroup = true) }

        viewModelScope.launch {
            try {
                val groupId = dbRef.child("groups").push().key.orEmpty()

                // Struktur dat
                val membersMap = hashMapOf<String, Any>()
                membersMap[currentUid] = 0L
                selected.forEach { membersMap[it.id] = nominals[it.id] ?: 0L }

                val groupData = hashMapOf(
                    "id" to groupId,
                    "name" to groupName,
                    "createdBy" to currentUid,
                    "createdAt" to ServerValue.TIMESTAMP,
                    "members" to membersMap
                )

                val updates = hashMapOf<String, Any>()
                updates["/groups/$groupId"] = groupData
                updates["/users/$currentUid/groups/$groupId"] = true
                selected.forEach { friend ->
                    updates["/users/${friend.id}/groups/$groupId"] = true
                }

                dbRef.updateChildren(updates).await()
                _uiState.update { it.copy(isCreatingGroup = false, isSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isCreatingGroup = false, errorMessage = e.message.orEmpty()) }
            }
        }
    }

    fun resetSuccessFlag() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}