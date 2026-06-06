package com.example.pomotodo.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import com.example.pomotodo.data.TodoItem
import com.example.pomotodo.theme.PomoTodoTheme
import org.junit.Rule
import org.junit.Test

/** UI tests for [MainScreenContent]. */
class MainScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun timerAndTodoList_areDisplayed() {
    composeTestRule.setContent {
      PomoTodoTheme {
        MainScreenContent(
          state = DEFAULT_STATE,
          newTodoTitle = "",
          onNewTodoTitleChange = {},
          onAddTodo = {},
          onToggleTodo = {},
          onDeleteTodo = {},
          onSelectTodo = {},
          onSelectDuration = {},
          onStartTimer = {},
          onPauseTimer = {},
          onResetTimer = {},
          onDismissCompletion = {},
          onAboutClick = {},
          modifier = Modifier.padding(16.dp),
        )
      }
    }

    composeTestRule.onNodeWithTag("timer_time").assertTextEquals("25:00")
    composeTestRule.onNodeWithText("PomoTodo").assertIsDisplayed()
    composeTestRule.onNodeWithTag("about_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("기획서 초안 작성").assertIsDisplayed()
    composeTestRule.onNodeWithText("50분").assertIsDisplayed()
    composeTestRule.onNodeWithText("시작").assertIsDisplayed()
  }

  @Test
  fun completionAlert_isDisplayedWhenTimerCompletes() {
    composeTestRule.setContent {
      PomoTodoTheme {
        MainScreenContent(
          state =
            DEFAULT_STATE.copy(
              timer =
                TimerState(
                  remainingSeconds = 0,
                  status = TimerStatus.Completed,
                  showCompletionAlert = true,
                )
            ),
          newTodoTitle = "",
          onNewTodoTitleChange = {},
          onAddTodo = {},
          onToggleTodo = {},
          onDeleteTodo = {},
          onSelectTodo = {},
          onSelectDuration = {},
          onStartTimer = {},
          onPauseTimer = {},
          onResetTimer = {},
          onDismissCompletion = {},
          onAboutClick = {},
          modifier = Modifier.padding(16.dp),
        )
      }
    }

    composeTestRule.onNodeWithTag("completion_alert").assertIsDisplayed()
    composeTestRule.onNodeWithText("포커스 완료!").assertIsDisplayed()
    composeTestRule.onNodeWithText("집중 세션이 완료되었습니다.", substring = true).assertIsDisplayed()
  }
}

private val DEFAULT_STATE =
  MainScreenUiState(
    todos =
      listOf(
        TodoItem(id = 1, title = "기획서 초안 작성", isCompleted = false, pomodoroCount = 0),
        TodoItem(id = 2, title = "리서치 정리", isCompleted = true, pomodoroCount = 2),
      )
  )
