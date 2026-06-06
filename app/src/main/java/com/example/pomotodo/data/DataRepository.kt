package com.example.pomotodo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class TodoItem(
  val id: Long,
  val title: String,
  val isCompleted: Boolean,
  val pomodoroCount: Int,
)

interface PomoTodoRepository {
  val todos: Flow<List<TodoItem>>

  suspend fun addTodo(title: String)

  suspend fun toggleTodo(id: Long, isCompleted: Boolean)

  suspend fun deleteTodo(id: Long)

  suspend fun recordPomodoro(id: Long)
}

class RoomPomoTodoRepository(private val todoDao: TodoDao) : PomoTodoRepository {
  override val todos: Flow<List<TodoItem>> = todoDao.observeTodos().map { entities -> entities.map(TodoEntity::toModel) }

  override suspend fun addTodo(title: String) {
    val cleanTitle = title.trim()
    if (cleanTitle.isNotEmpty()) {
      todoDao.insert(TodoEntity(title = cleanTitle))
    }
  }

  override suspend fun toggleTodo(id: Long, isCompleted: Boolean) {
    todoDao.setCompleted(id = id, isCompleted = isCompleted)
  }

  override suspend fun deleteTodo(id: Long) {
    todoDao.delete(id)
  }

  override suspend fun recordPomodoro(id: Long) {
    todoDao.incrementPomodoroCount(id)
  }
}

private fun TodoEntity.toModel(): TodoItem =
  TodoItem(
    id = id,
    title = title,
    isCompleted = isCompleted,
    pomodoroCount = pomodoroCount,
  )
