package id.ac.pnm.bayarin_app.ui.detailTransaksiGroup

data class DetailTransaksiGroupUiState(
    val groupName: String = "",
    val groupSubInfo: String = "",
    val totalAmount: Long = 0L,
    val payerName: String = "Unknown",
    val payerInitial: String = "U",
    val memberBills: List<MemberBillStatus> = emptyList(),
    val isCreator: Boolean = false,
    val isLoading: Boolean = false,
    val isReminderSuccess: Boolean = false,
    val errorMessage: String = ""
)