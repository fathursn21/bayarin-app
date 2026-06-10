package id.ac.pnm.bayarin_app.ui.home

data class HomeUiState(
    val income: Long = 0,
    val expense: Long = 0,
    val ratio : ExpenseStatus = ExpenseStatus.HEMAT
)

enum class ExpenseStatus {
    HEMAT,
    CUKUP,
    BOROS
}
