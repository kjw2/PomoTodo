package com.example.pomotodo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomotodo.data.PomoTodoRepository
import com.example.pomotodo.data.TodoItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val FOCUS_DURATION_SECONDS = 25 * 60
const val LONG_FOCUS_DURATION_SECONDS = 50 * 60

data class TimerPreset(val label: String, val durationSeconds: Int)

val TIMER_PRESETS =
  listOf(
    TimerPreset(label = "25분", durationSeconds = FOCUS_DURATION_SECONDS),
    TimerPreset(label = "50분", durationSeconds = LONG_FOCUS_DURATION_SECONDS),
  )

class MainScreenViewModel(
  private val repository: PomoTodoRepository,
  private val timerController: PomodoroTimerController,
  private val dataDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
  val uiState: StateFlow<MainScreenUiState> =
    combine(repository.todos, timerController.timerState) { todos, timer ->
        MainScreenUiState(todos = todos, timer = timer)
      }
      .catch { throwable ->
        emit(
          MainScreenUiState(
            timer = timerController.timerState.value,
            errorMessage = throwable.message ?: "데이터를 불러오지 못했습니다.",
          )
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainScreenUiState(timer = timerController.timerState.value),
      )

  fun addTodo(title: String) {
    viewModelScope.launch(dataDispatcher) { repository.addTodo(title) }
  }

  fun toggleTodo(todo: TodoItem) {
    viewModelScope.launch(dataDispatcher) { repository.toggleTodo(todo.id, !todo.isCompleted) }
  }

  fun deleteTodo(todo: TodoItem) {
    viewModelScope.launch(dataDispatcher) {
      repository.deleteTodo(todo.id)
      if (timerController.timerState.value.selectedTodoId == todo.id) timerController.selectTodo(null)
    }
  }

  fun selectTodo(todoId: Long?) {
    timerController.selectTodo(todoId)
  }

  fun selectDuration(durationSeconds: Int) {
    timerController.selectDuration(durationSeconds)
  }

  fun startTimer() {
    timerController.startTimer()
  }

  fun pauseTimer() {
    timerController.pauseTimer()
  }

  fun resetTimer() {
    timerController.resetTimer()
  }

  fun dismissCompletionAlert() {
    timerController.dismissCompletionAlert()
  }
}

data class MainScreenUiState(
  val todos: List<TodoItem> = emptyList(),
  val timer: TimerState = TimerState(),
  val errorMessage: String? = null,
)

data class TimerState(
  val totalSeconds: Int = FOCUS_DURATION_SECONDS,
  val remainingSeconds: Int = FOCUS_DURATION_SECONDS,
  val status: TimerStatus = TimerStatus.Idle,
  val selectedTodoId: Long? = null,
  val showCompletionAlert: Boolean = false,
  val completionEventId: Long = 0,
) {
  val remainingFraction: Float
    get() = if (totalSeconds <= 0) 0f else remainingSeconds / totalSeconds.toFloat()

  val elapsedFraction: Float
    get() = 1f - remainingFraction

  val timeText: String
    get() {
      val minutes = remainingSeconds / 60
      val seconds = remainingSeconds % 60
      return "%02d:%02d".format(minutes, seconds)
    }
}

enum class TimerStatus {
  Idle,
  Running,
  Paused,
  Completed,
}
