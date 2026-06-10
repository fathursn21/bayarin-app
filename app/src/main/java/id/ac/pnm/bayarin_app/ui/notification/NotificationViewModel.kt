package id.ac.pnm.bayarin_app.ui.notification

import NotificationUiState
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.ac.pnm.bayarin_app.data.model.Notifications
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow



class NotificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val currentUid get() = auth.currentUser?.uid.orEmpty()
    private var listener: ValueEventListener? = null

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        if (currentUid.isEmpty()) {
            _uiState.value = NotificationUiState.Error("Pengguna belum login")
            return
        }

        listener = dbRef.child("users").child(currentUid).child("notifications")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifList = mutableListOf<Notifications>()
                    for (child in snapshot.children) {
                        val notif = child.getValue(Notifications::class.java)
                        if (notif != null) {
                            notifList.add(notif)
                        }
                    }
                    // Balik urutan list agar notifikasi paling baru berada di paling atas
                    _uiState.value = NotificationUiState.Success(notifList.reversed())
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.value = NotificationUiState.Error(error.message)
                }
            })
    }

    fun markAsRead(notifId: String) {
        if (currentUid.isEmpty() || notifId.isEmpty()) return
        dbRef.child("users").child(currentUid).child("notifications")
            .child(notifId).child("isRead").setValue(true)
    }

    override fun onCleared() {
        super.onCleared()
        listener?.let {
            dbRef.child("users").child(currentUid).child("notifications").removeEventListener(it)
        }
    }
}