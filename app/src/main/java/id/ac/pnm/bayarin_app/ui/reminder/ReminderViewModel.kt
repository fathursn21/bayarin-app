package id.ac.pnm.bayarin_app.ui.reminder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

class ReminderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ReminderUiState>(ReminderUiState.Loading)
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val currentUid get() = auth.currentUser?.uid.orEmpty()

    init {
        fetchPendingReminders()
    }

    private fun fetchPendingReminders() {
        if (currentUid.isEmpty()) {
            _uiState.value = ReminderUiState.Error("Pengguna belum login")
            return
        }

        //Ambil semua grup untuk pengguna status admin
        dbRef.child("groups").orderByChild("createdBy").equalTo(currentUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Coroutine untuk fetch data user data nama & telp
                    viewModelScope.launch {
                        _uiState.value = ReminderUiState.Loading
                        val reminderList = mutableListOf<ReminderData>()

                        for (groupSnapshot in snapshot.children) {
                            val groupId = groupSnapshot.key ?: continue
                            val groupName = groupSnapshot.child("name").getValue(String::class.java) ?: "Grup Patungan"

                            val membersSnapshot = groupSnapshot.child("members")
                            val statusesSnapshot = groupSnapshot.child("statuses")

                            for (member in membersSnapshot.children) {
                                val memberId = member.key ?: continue
                                if (memberId == currentUid) continue

                                // Cek status pembayaran
                                val status = statusesSnapshot.child(memberId).getValue(String::class.java) ?: "BELUM_BAYAR"

                                if (status != "SUDAH_BAYAR") {
                                    val nominal = member.getValue(Long::class.java) ?: 0L

                                    try {
                                        // Ambil informasi nomor telepon dan nama dari node users
                                        val userSnapshot = dbRef.child("users").child(memberId).get().await()
                                        val userName = userSnapshot.child("name").getValue(String::class.java) ?: "Anggota"
                                        val userTelp = userSnapshot.child("telp").getValue(String::class.java) ?: ""
                                        val initial = if (userName.isNotBlank()) userName.first().uppercase() else "?"

                                        val reminderData = ReminderData(
                                            id = "${groupId}_$memberId",
                                            name = userName,
                                            initial = initial,
                                            groupName = groupName,
                                            amount = formatRupiah(nominal),
                                            time = if (status == "PENGINGAT_TERKIRIM") "Menunggu" else "Belum Bayar",
                                            telp = userTelp
                                        )

                                        reminderList.add(reminderData)
                                    } catch (e: Exception) {
                                        Log.e("ReminderVM", "Gagal mengambil data user $memberId", e)
                                    }
                                }
                            }
                        }

                        // Update tampilan dengan data yang sudah difilter
                        _uiState.value = ReminderUiState.Success(reminderList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.value = ReminderUiState.Error(error.message)
                }
            })
    }

    //Format nomor telepon agar bisa dibaca oleh API WhatsApp.
    fun formatPhoneForWhatsApp(phone: String): String {
        var formatted = phone.replace("-", "").replace(" ", "").replace("+", "")
        if (formatted.startsWith("0")) {
            formatted = "62" + formatted.substring(1)
        }
        return formatted
    }

    //Format Rupiah untuk mengubah Long menjadi teks Rp.

    private fun formatRupiah(value: Long): String {
        val localeID = Locale("id", "ID")
        val formatter = NumberFormat.getNumberInstance(localeID)
        return "Rp${formatter.format(value)}"
    }

    fun sendInternalNotification(reminder: ReminderData, onSuccess: () -> Unit) {
        //format id reminder "groupId_memberId"
        val parts = reminder.id.split("_")
        if (parts.size != 2) return

        val groupId = parts[0]
        val memberId = parts[1]

        val dbRef = FirebaseDatabase.getInstance().reference

        // Generate ID Notifikasi
        val notifId = dbRef.child("users").child(memberId).child("notifications").push().key.orEmpty()

        // Susun struktur data notifikasi
        val notification = mapOf(
            "id" to notifId,
            "title" to "Tagihan Grup: ${reminder.groupName}",
            "message" to "Yuk bayar patungan sebesar ${reminder.amount} ke admin grup.",
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "groupId" to groupId
        )

        //update ke semua anggota
        val updates = hashMapOf<String, Any>()
        updates["/groups/$groupId/statuses/$memberId"] = "PENGINGAT_TERKIRIM"
        updates["/users/$memberId/notifications/$notifId"] = notification

        // Eksekusi ke Firebase
        dbRef.updateChildren(updates).addOnSuccessListener {
            onSuccess()
        }
    }
}