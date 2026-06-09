package id.ac.pnm.bayarin_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.ac.pnm.bayarin_app.data.dao.NotesDao
import id.ac.pnm.bayarin_app.data.model.Notes
import kotlin.also
import kotlin.jvm.java

@Database(entities = [Notes::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao() : NotesDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getDatabase(applicationContext : Context) : AppDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "bayarin_app"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }

        }
    }

}