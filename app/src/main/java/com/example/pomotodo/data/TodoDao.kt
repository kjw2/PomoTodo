package com.example.pomotodo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
  @Query("SELECT * FROM todos ORDER BY is_completed ASC, created_at DESC")
  fun observeTodos(): Flow<List<TodoEntity>>

  @Insert
  suspend fun insert(todo: TodoEntity): Long

  @Query("UPDATE todos SET is_completed = :isCompleted WHERE id = :id")
  suspend fun setCompleted(id: Long, isCompleted: Boolean)

  @Query("UPDATE todos SET pomodoro_count = pomodoro_count + 1 WHERE id = :id")
  suspend fun incrementPomodoroCount(id: Long)

  @Query("DELETE FROM todos WHERE id = :id")
  suspend fun delete(id: Long)
}
