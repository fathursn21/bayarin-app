package id.ac.pnm.bayarin_app.data.model

data class Groups(
    val id: String = "",
    val name: String = "",
    val createdBy: String = "",
    val createdAt: Long = 0L,
    val members: Map<String, Long> = emptyMap()
)