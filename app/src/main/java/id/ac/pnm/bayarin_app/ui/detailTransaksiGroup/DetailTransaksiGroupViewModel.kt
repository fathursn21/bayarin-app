package id.ac.pnm.bayarin_app.ui.detailTransaksiGroup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.ac.pnm.bayarin_app.ContextApplication
import id.ac.pnm.bayarin_app.data.AppDatabase
import id.ac.pnm.bayarin_app.data.model.Groups
import id.ac.pnm.bayarin_app.data.model.Notes
import id.ac.pnm.bayarin_app.data.model.Users
import id.ac.pnm.bayarin_app.data.repository.NotesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val TAG = "DetailTransaksiGroupViewModel"

class DetailTransaksiGroupViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val currentUid get() = auth.currentUser?.uid.orEmpty()

    private val _uiState = MutableStateFlow(DetailTransaksiGroupUiState())
    val uiState: StateFlow<DetailTransaksiGroupUiState> = _uiState.asStateFlow()

    private val roomDb = AppDatabase.getDatabase(ContextApplication.instance)
    private val notesRepository = NotesRepository(
        roomDb.notesDao()
    )

    private var groupDetailListener: ValueEventListener? = null
    private var targetGroupId: String = ""

    fun loadGroupDetails(groupId: String) {
        if (groupId.isEmpty()) return
        targetGroupId = groupId
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }

        //realtime perubahan data di /groups/{groupId}
        groupDetailListener = dbRef.child("groups").child(groupId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val group = snapshot.getValue(Groups::class.java)
                    if (group == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Grup tidak ditemukan") }
                        return
                    }

                    fetchFullMembersAndPayerDetails(group)
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            })
    }

    private fun fetchFullMembersAndPayerDetails(group: Groups) {
        viewModelScope.launch {
            try {
                val payerDeferred = async {
                    dbRef.child("users").child(group.createdBy).get().await().getValue(Users::class.java)
                }

                val statusSnapshot = dbRef.child("groups").child(group.id).child("statuses").get().await()
                val statusMap = statusSnapshot.children.associate { it.key.orEmpty() to it.value.toString() }

                val deferredMembers = group.members.keys.map { memberId ->
                    async {
                        val userProfile = dbRef.child("users").child(memberId).get().await().getValue(Users::class.java)
                        val nominalBill = group.members[memberId] ?: 0L

                        val dbStatusStr = statusMap[memberId] ?: "BELUM_BAYAR"
                        val billStatus = try { BillStatus.valueOf(dbStatusStr) } catch (e: Exception) { BillStatus.BELUM_BAYAR }

                        if (userProfile != null) {
                            MemberBillStatus(
                                id = userProfile.id,
                                name = userProfile.name,
                                initial = userProfile.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                                amount = nominalBill,
                                status = billStatus,
                                isAdmin = (memberId == group.createdBy)
                            )
                        } else null
                    }
                }

                val membersList = deferredMembers.awaitAll().filterNotNull()
                val payerProfile = payerDeferred.await()
                val totalAmount = group.members.values.sum()
                val dateString = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date(group.createdAt))

                _uiState.update {
                    it.copy(
                        groupName = group.name,
                        groupSubInfo = "$dateString • ${group.members.size} Anggota",
                        totalAmount = totalAmount,
                        payerName = if (group.createdBy == currentUid) "Kamu" else payerProfile?.name ?: "Unknown",
                        payerInitial = payerProfile?.name?.firstOrNull()?.toString()?.uppercase() ?: "U",
                        memberBills = membersList,
                        isCreator = group.createdBy == currentUid, // --- CEK APAKAH USER ADALAH PEMBUAT GRUP ---
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal memproses data detail grup", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memproses data: ${e.message}") }
            }
        }
    }

    fun tandaiSudahBayar(memberId: String) {
        if (targetGroupId.isEmpty()) return

        viewModelScope.launch {
            try {
                // Update spesifik node status anggota di grup ini menjadi SUDAH_BAYAR
                dbRef.child("groups").child(targetGroupId)
                    .child("statuses").child(memberId)
                    .setValue(BillStatus.SUDAH_BAYAR.name)
                    .await()

                //  ambil data group berdsarkan targetGroupId
                val groupSnapshot = dbRef.child("groups")
                    .child(targetGroupId)
                    .get()
                    .await()

//                ambil nama group dari snapshot group data
                val groupName = groupSnapshot
                    .child("name")
                    .getValue(String::class.java)
                    .orEmpty()

//                ambil data nominal meber berdasarkan meber id nya dari snapshot group
                val nominalMember = (groupSnapshot
                    .child("members")
                    .child(memberId)
                    .value as? Number)
                    ?.toLong() ?: 0L

                val notesExpense = Notes(
                    id = UUID.randomUUID().toString(),
                    userId = memberId,
                    expense = true,
                    nominal = nominalMember,
                    category = "Group",
                    date = System.currentTimeMillis(),
                    note = groupName,
                    createdAt = System.currentTimeMillis(),
                    isSynced = 0
                )

                notesRepository.createNotes(notesExpense)

                val notesIncome = Notes(
                    id = UUID.randomUUID().toString(),
                    userId = currentUid,
                    expense = false,
                    nominal = nominalMember,
                    category = "Group",
                    date = System.currentTimeMillis(),
                    note = groupName,
                    createdAt = System.currentTimeMillis(),
                    isSynced = 0
                )

                notesRepository.createNotes(notesIncome)

            } catch (e: Exception) {
                Log.e(TAG, "Gagal menandai sudah bayar", e)
            }
        }
    }

    //notifikasi
    fun kirimPengingatGrup() {
        if (targetGroupId.isEmpty()) return
        val currentBills = _uiState.value.memberBills
        val currentGroupName = _uiState.value.groupName

        viewModelScope.launch {
            try {
                val updates = hashMapOf<String, Any>()

                currentBills.forEach { member ->
                    // Hanya kirim notifikasi ke anggota yang statusnya BELUM_BAYAR & bukan diri sendiri
                    if (member.status == BillStatus.BELUM_BAYAR && member.id != currentUid) {

                        // Generate ID unik untuk notifikasi baru
                        val notifId = dbRef.child("users").child(member.id).child("notifications").push().key.orEmpty()

                        // Buat objek notifikasi
                        val notification = mapOf(
                            "id" to notifId,
                            "title" to "Tagihan Grup: $currentGroupName",
                            "message" to "Yuk bayar patungan sebesar ${formatRupiah(member.amount)} ke admin grup.",
                            "timestamp" to System.currentTimeMillis(),
                            "isRead" to false,
                            "groupId" to targetGroupId
                        )

                        // Update status grup menjadi PENGINGAT_TERKIRIM
                        updates["/groups/$targetGroupId/statuses/${member.id}"] = BillStatus.PENGINGAT_TERKIRIM.name

                        // Masukkan notifikasi ke path user penerima
                        updates["/users/${member.id}/notifications/$notifId"] = notification
                    }
                }

                if (updates.isNotEmpty()) {
                    dbRef.updateChildren(updates).await() // Eksekusi update serentak
                    _uiState.update { it.copy(isReminderSuccess = true) }
                }

            } catch (e: Exception) {
                Log.w(TAG, "Kirim pengingat gagal", e)
                _uiState.update { it.copy(errorMessage = "Gagal mengirim pengingat: ${e.message}") }
            }
        }
    }

    fun resetReminderFlag() {
        _uiState.update { it.copy(isReminderSuccess = false) }
    }

    override fun onCleared() {
        super.onCleared()
        groupDetailListener?.let {
            dbRef.child("groups").child(targetGroupId).removeEventListener(it)
        }
    }
}