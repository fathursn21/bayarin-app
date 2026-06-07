package id.ac.pnm.bayarin_app.data.model

import com.google.firebase.database.Exclude

data class Notes(
    @get:Exclude
    val id : String = "",
    val expense : Boolean = true,
    val nominal: Long = 0,
    val category: String = "",
    val date: Long = 0,
    val note: String = "",
    val createdAt: Long = 0
)
