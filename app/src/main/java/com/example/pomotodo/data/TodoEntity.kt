package com.example.pomotodo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
  @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
  @ColumnInfo(name = "pomodoro_count") val pomodoroCount: Int = 0,
)
