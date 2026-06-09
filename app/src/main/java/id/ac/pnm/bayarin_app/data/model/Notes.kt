package id.ac.pnm.bayarin_app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

@Entity(tableName = "notes")
data class Notes(
    @PrimaryKey
    @get:Exclude
    val id : String = "",

    @get:Exclude
    @ColumnInfo(name = "user_id")
    val userId : String = "",

    val expense : Boolean = true,
    val nominal: Long = 0,
    val category: String = "",
    val date: Long = 0,
    val note: String = "",
    val createdAt: Long = 0,
    val isSynced: Int = 0
)
