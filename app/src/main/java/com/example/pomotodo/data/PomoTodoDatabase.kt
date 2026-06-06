package com.example.pomotodo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class PomoTodoDatabase : RoomDatabase() {
  abstract fun todoDao(): TodoDao

  companion object {
    @Volatile private var instance: PomoTodoDatabase? = null

    fun getDatabase(context: Context): PomoTodoDatabase =
      instance
        ?: synchronized(this) {
          instance
            ?: Room.databaseBuilder(
                context.applicationContext,
                PomoTodoDatabase::class.java,
                "pomotodo.db",
              )
              .build()
              .also { instance = it }
        }
  }
}
