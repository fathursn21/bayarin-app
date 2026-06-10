import id.ac.pnm.bayarin_app.data.model.Notifications

sealed interface NotificationUiState {
    object Loading : NotificationUiState
    data class Success(val list: List<Notifications>) : NotificationUiState
    data class Error(val message: String) : NotificationUiState
}