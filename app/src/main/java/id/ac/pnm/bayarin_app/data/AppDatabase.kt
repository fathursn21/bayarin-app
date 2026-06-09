package com.example.unscramble.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WordsModel::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun WordsDao() : WordsDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getDatabase(applicationContext : Context) : AppDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "words_database"
            )
//                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }

        }
    }

}