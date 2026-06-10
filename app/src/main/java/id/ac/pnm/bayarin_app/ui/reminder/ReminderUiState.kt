package id.ac.pnm.bayarin_app.ui.reminder

data class ReminderData(
    val id: String,
    val name: String,
    val initial: String,
    val groupName: String,
    val amount: String,
    val time: String,
    val telp: String
)

sealed interface ReminderUiState {
    object Loading : ReminderUiState
    data class Success(val reminders: List<ReminderData>) : ReminderUiState
    data class Error(val message: String) : ReminderUiState
}