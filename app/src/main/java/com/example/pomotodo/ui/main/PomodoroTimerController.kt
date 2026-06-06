package com.example.pomotodo.ui.main

import com.example.pomotodo.data.PomoTodoRepository
import com.example.pomotodo.notifications.PomodoroNotifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PomodoroTimerController(
  private val repository: PomoTodoRepository,
  private val notifier: PomodoroNotifier,
  private val scope: CoroutineScope,
  private val focusDurationSeconds: Int = FOCUS_DURATION_SECONDS,
  private val tickMillis: Long = 1_000L,
  private val timerDispatcher: CoroutineDispatcher = Dispatchers.Default,
  private val dataDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
  private val timerStateFlow =
    MutableStateFlow(
      TimerState(
        totalSeconds = focusDurationSeconds,
        remainingSeconds = focusDurationSeconds,
      )
    )
  private var timerJob: Job? = null
  private var completionEventCounter = 0L

  val timerState: StateFlow<TimerState> = timerStateFlow.asStateFlow()

  fun selectTodo(todoId: Long?) {
    timerStateFlow.update { it.copy(selectedTodoId = todoId) }
  }

  fun selectDuration(durationSeconds: Int) {
    if (timerStateFlow.value.status == TimerStatus.Running) return

    timerJob?.cancel()
    timerJob = null
    timerStateFlow.update {
      it.copy(
        totalSeconds = durationSeconds,
        remainingSeconds = durationSeconds,
        status = TimerStatus.Idle,
        showCompletionAlert = false,
      )
    }
    notifier.cancelTimerProgress()
  }

  fun startTimer() {
    val current = timerStateFlow.value
    if (current.status == TimerStatus.Running) return

    val nextRemainingSeconds =
      if (current.status == TimerStatus.Completed || current.remainingSeconds <= 0) {
        current.totalSeconds
      } else {
        current.remainingSeconds
      }

    timerStateFlow.update {
      it.copy(
        remainingSeconds = nextRemainingSeconds,
        status = TimerStatus.Running,
        showCompletionAlert = false,
      )
    }
    showProgressNotification(timerStateFlow.value)

    timerJob?.cancel()
    timerJob = scope.launch(timerDispatcher) { runTimer() }
  }

  fun pauseTimer() {
    if (timerStateFlow.value.status != TimerStatus.Running) return
    timerJob?.cancel()
    timerJob = null
    timerStateFlow.update { it.copy(status = TimerStatus.Paused) }
    notifier.cancelTimerProgress()
  }

  fun resetTimer() {
    timerJob?.cancel()
    timerJob = null
    timerStateFlow.update {
      it.copy(
        remainingSeconds = it.totalSeconds,
        status = TimerStatus.Idle,
        showCompletionAlert = false,
      )
    }
    notifier.cancelTimerProgress()
  }

  fun dismissCompletionAlert() {
    timerStateFlow.update { it.copy(showCompletionAlert = false) }
  }

  private suspend fun runTimer() {
    while (timerStateFlow.value.remainingSeconds > 0 && timerStateFlow.value.status == TimerStatus.Running) {
      delay(tickMillis)
      if (timerStateFlow.value.status != TimerStatus.Running) return

      val nextRemainingSeconds = timerStateFlow.value.remainingSeconds - 1
      if (nextRemainingSeconds <= 0) {
        completeTimer()
        return
      }

      timerStateFlow.update { it.copy(remainingSeconds = nextRemainingSeconds) }
      showProgressNotification(timerStateFlow.value)
    }
  }

  private suspend fun completeTimer() {
    timerJob = null
    completionEventCounter += 1

    val selectedTodoId = timerStateFlow.value.selectedTodoId
    timerStateFlow.update {
      it.copy(
        remainingSeconds = 0,
        status = TimerStatus.Completed,
        showCompletionAlert = true,
        completionEventId = completionEventCounter,
      )
    }

    notifier.cancelTimerProgress()
    notifier.showFocusCompleted(minutes = timerStateFlow.value.totalSeconds / 60)

    if (selectedTodoId != null) {
      withContext(dataDispatcher) { repository.recordPomodoro(selectedTodoId) }
    }
  }

  private fun showProgressNotification(timer: TimerState) {
    notifier.showTimerProgress(
      totalSeconds = timer.totalSeconds,
      remainingSeconds = timer.remainingSeconds,
    )
  }
}
