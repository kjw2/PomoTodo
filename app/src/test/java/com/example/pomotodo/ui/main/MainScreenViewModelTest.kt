package com.example.pomotodo.ui.main

import com.example.pomotodo.data.PomoTodoRepository
import com.example.pomotodo.data.TodoItem
import com.example.pomotodo.notifications.PomodoroNotifier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun timer_initiallyIdleAtTwentyFiveMinutes() = runTest(mainDispatcherRule.testDispatcher) {
    val viewModel = createViewModel()
    collectUiState(viewModel)

    assertEquals("25:00", viewModel.uiState.value.timer.timeText)
    assertEquals(TimerStatus.Idle, viewModel.uiState.value.timer.status)
  }

  @Test
  fun timer_pauseStopsCountdownUntilRestarted() = runTest(mainDispatcherRule.testDispatcher) {
    val viewModel = createViewModel()
    collectUiState(viewModel)

    viewModel.startTimer()
    advanceTimeBy(3_000)
    runCurrent()
    viewModel.pauseTimer()
    val pausedAt = viewModel.uiState.value.timer.remainingSeconds

    advanceTimeBy(10_000)
    runCurrent()

    assertEquals(FOCUS_DURATION_SECONDS - 3, pausedAt)
    assertEquals(pausedAt, viewModel.uiState.value.timer.remainingSeconds)
    assertEquals(TimerStatus.Paused, viewModel.uiState.value.timer.status)
  }

  @Test
  fun timer_resetReturnsToIdleTwentyFiveMinutes() = runTest(mainDispatcherRule.testDispatcher) {
    val viewModel = createViewModel()
    collectUiState(viewModel)

    viewModel.startTimer()
    advanceTimeBy(5_000)
    runCurrent()
    viewModel.resetTimer()
    runCurrent()

    assertEquals(FOCUS_DURATION_SECONDS, viewModel.uiState.value.timer.remainingSeconds)
    assertEquals(TimerStatus.Idle, viewModel.uiState.value.timer.status)
  }

  @Test
  fun timer_selectFiftyMinutesResetsDuration() = runTest(mainDispatcherRule.testDispatcher) {
    val viewModel = createViewModel()
    collectUiState(viewModel)

    viewModel.selectDuration(LONG_FOCUS_DURATION_SECONDS)
    runCurrent()

    assertEquals("50:00", viewModel.uiState.value.timer.timeText)
    assertEquals(LONG_FOCUS_DURATION_SECONDS, viewModel.uiState.value.timer.totalSeconds)
    assertEquals(TimerStatus.Idle, viewModel.uiState.value.timer.status)
  }

  @Test
  fun timer_afterTwentyFiveMinutesStopsAndShowsCompletionAlert() = runTest(mainDispatcherRule.testDispatcher) {
    val repository =
      FakePomoTodoRepository(
        initialTodos = listOf(TodoItem(id = 1, title = "기획서 초안 작성", isCompleted = false, pomodoroCount = 0))
      )
    val viewModel = createViewModel(repository)
    collectUiState(viewModel)

    viewModel.selectTodo(1)
    viewModel.startTimer()
    advanceTimeBy(FOCUS_DURATION_SECONDS * 1_000L)
    runCurrent()

    assertEquals(0, viewModel.uiState.value.timer.remainingSeconds)
    assertEquals(TimerStatus.Completed, viewModel.uiState.value.timer.status)
    assertTrue(viewModel.uiState.value.timer.showCompletionAlert)
    assertEquals(listOf(1L), repository.recordedPomodoros)
  }

  private fun testDispatcher(): TestDispatcher = mainDispatcherRule.testDispatcher

  private fun kotlinx.coroutines.test.TestScope.createViewModel(
    repository: FakePomoTodoRepository = FakePomoTodoRepository(),
  ): MainScreenViewModel {
    val timerController =
      PomodoroTimerController(
        repository = repository,
        notifier = FakePomodoroNotifier(),
        scope = backgroundScope,
        timerDispatcher = testDispatcher(),
        dataDispatcher = testDispatcher(),
      )
    return MainScreenViewModel(
      repository = repository,
      timerController = timerController,
      dataDispatcher = testDispatcher(),
    )
  }

  private fun kotlinx.coroutines.test.TestScope.collectUiState(viewModel: MainScreenViewModel) {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
    runCurrent()
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
  val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
  override fun starting(description: Description) {
    kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
  }

  override fun finished(description: Description) {
    kotlinx.coroutines.Dispatchers.resetMain()
  }
}

private class FakePomoTodoRepository(initialTodos: List<TodoItem> = emptyList()) : PomoTodoRepository {
  private val todoState = MutableStateFlow(initialTodos)
  override val todos: Flow<List<TodoItem>> = todoState
  val recordedPomodoros = mutableListOf<Long>()

  override suspend fun addTodo(title: String) {
    val nextId = (todoState.value.maxOfOrNull { it.id } ?: 0L) + 1L
    todoState.update { todos ->
      todos + TodoItem(id = nextId, title = title.trim(), isCompleted = false, pomodoroCount = 0)
    }
  }

  override suspend fun toggleTodo(id: Long, isCompleted: Boolean) {
    todoState.update { todos ->
      todos.map { todo -> if (todo.id == id) todo.copy(isCompleted = isCompleted) else todo }
    }
  }

  override suspend fun deleteTodo(id: Long) {
    todoState.update { todos -> todos.filterNot { it.id == id } }
  }

  override suspend fun recordPomodoro(id: Long) {
    recordedPomodoros += id
    todoState.update { todos ->
      todos.map { todo ->
        if (todo.id == id) todo.copy(pomodoroCount = todo.pomodoroCount + 1) else todo
      }
    }
  }
}

private class FakePomodoroNotifier : PomodoroNotifier {
  override fun showTimerProgress(totalSeconds: Int, remainingSeconds: Int) = Unit

  override fun cancelTimerProgress() = Unit

  override fun showFocusCompleted(minutes: Int) = Unit
}
