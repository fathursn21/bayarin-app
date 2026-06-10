package id.ac.pnm.bayarin_app.ui.group

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.ac.pnm.bayarin_app.data.model.Groups
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "GroupViewModel"

class GroupViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val currentUid get() = auth.currentUser?.uid.orEmpty()

    private val _uiState = MutableStateFlow(GroupUiState())
    val uiState: StateFlow<GroupUiState> = _uiState.asStateFlow()

    private var userGroupsListener: ValueEventListener? = null

    init {
        listenToUserGroups()
    }

    private fun listenToUserGroups() {
        if (currentUid.isEmpty()) return
        _uiState.update { it.copy(isLoading = true) }

        //daftar ID grup yang diikuti user di /users/{uid}/groups secara real time
        userGroupsListener = dbRef.child("users")
            .child(currentUid)
            .child("groups")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val groupIds = snapshot.children.mapNotNull { it.key }

                    if (groupIds.isEmpty()) {
                        _uiState.update { it.copy(groups = emptyList(), isLoading = false) }
                        return
                    }

                    //Ambil detail data tiap grup berdasarkan ID
                    viewModelScope.launch {
                        try {
                            val deferredGroups = groupIds.map { id ->
                                async {
                                    dbRef.child("groups").child(id).get().await()
                                        .getValue(Groups::class.java)
                                }
                            }
                            val groupsList = deferredGroups.awaitAll().filterNotNull()
                                .sortedByDescending { it.createdAt } // Urutkan dari yang terbaru

                            _uiState.update { it.copy(groups = groupsList, isLoading = false) }
                        } catch (e: Exception) {
                            Log.w(TAG, "Gagal memuat detail grup", e)
                            _uiState.update { it.copy(isLoading = false, errorMessage = e.message.orEmpty()) }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        // Hapus listener agar tidak terjadi kebocoran memori
        userGroupsListener?.let {
            dbRef.child("users").child(currentUid).child("groups").removeEventListener(it)
        }
    }
}