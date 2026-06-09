package id.ac.pnm.bayarin_app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.ac.pnm.bayarin_app.data.model.Notes
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes WHERE user_id = :userId ORDER BY date DESC")
    fun getAllNotes(userId : String) : Flow<List<Notes>>

    @Query("SELECT * FROM notes WHERE user_id = :userId ORDER BY date DESC LIMIT :limit")
    fun getLimitNotes(limit : Int, userId : String) : Flow<List<Notes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg notes : Notes)

    @Query("SELECT * FROM notes where isSynced = 0")
    suspend fun getUnsyncedNotes() : List<Notes>

    @Query("UPDATE notes SET isSynced = 1 where id = :id")
    suspend fun markAsSynced(id : String)

}