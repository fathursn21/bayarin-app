package id.ac.pnm.bayarin_app.data.model

data class Notifications(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val groupId: String = ""
)