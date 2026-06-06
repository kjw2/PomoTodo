package com.example.pomotodo.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.pomotodo.About
import com.example.pomotodo.PomoTodoApplication
import com.example.pomotodo.data.TodoItem
import com.example.pomotodo.theme.PomoTodoTheme

@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val application = context.applicationContext as PomoTodoApplication
  val model: MainScreenViewModel = viewModel {
    MainScreenViewModel(
      repository = application.repository,
      timerController = application.timerController,
    )
  }
  val state by model.uiState.collectAsStateWithLifecycle()
  var newTodoTitle by remember { mutableStateOf("") }

  MainScreenContent(
    state = state,
    newTodoTitle = newTodoTitle,
    onNewTodoTitleChange = { newTodoTitle = it },
    onAddTodo = {
      model.addTodo(newTodoTitle)
      newTodoTitle = ""
    },
    onToggleTodo = model::toggleTodo,
    onDeleteTodo = model::deleteTodo,
    onSelectTodo = model::selectTodo,
    onSelectDuration = model::selectDuration,
    onStartTimer = model::startTimer,
    onPauseTimer = model::pauseTimer,
    onResetTimer = model::resetTimer,
    onDismissCompletion = model::dismissCompletionAlert,
    onAboutClick = { onItemClick(About) },
    modifier = modifier,
  )
}

@Composable
internal fun MainScreenContent(
  state: MainScreenUiState,
  newTodoTitle: String,
  onNewTodoTitleChange: (String) -> Unit,
  onAddTodo: () -> Unit,
  onToggleTodo: (TodoItem) -> Unit,
  onDeleteTodo: (TodoItem) -> Unit,
  onSelectTodo: (Long?) -> Unit,
  onSelectDuration: (Int) -> Unit,
  onStartTimer: () -> Unit,
  onPauseTimer: () -> Unit,
  onResetTimer: () -> Unit,
  onDismissCompletion: () -> Unit,
  onAboutClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  if (state.timer.showCompletionAlert) {
    CompletionDialog(onDismiss = onDismissCompletion)
  }

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    item { HeaderSection(onAboutClick = onAboutClick) }
    item {
      TimerSection(
        state = state,
        onSelectDuration = onSelectDuration,
        onStartTimer = onStartTimer,
        onPauseTimer = onPauseTimer,
        onResetTimer = onResetTimer,
      )
    }
    item {
      AddTodoSection(
        title = newTodoTitle,
        onTitleChange = onNewTodoTitleChange,
        onAddTodo = onAddTodo,
      )
    }
    if (state.errorMessage != null) {
      item {
        Text(
          text = state.errorMessage,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
    item { TodoListHeader(todos = state.todos, selectedTodoId = state.timer.selectedTodoId) }
    if (state.todos.isEmpty()) {
      item { EmptyTodoHint() }
    } else {
      items(items = state.todos, key = { it.id }) { todo ->
        TodoRow(
          todo = todo,
          selected = state.timer.selectedTodoId == todo.id,
          onToggleTodo = { onToggleTodo(todo) },
          onDeleteTodo = { onDeleteTodo(todo) },
          onSelectTodo = {
            onSelectTodo(if (state.timer.selectedTodoId == todo.id) null else todo.id)
          },
        )
      }
    }
  }
}

@Composable
private fun HeaderSection(onAboutClick: () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
          Icon(
            imageVector = Icons.Filled.Timer,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(9.dp).size(22.dp),
          )
        }
        Text(
          text = "PomoTodo",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
        )
      }
      IconButton(onClick = onAboutClick, modifier = Modifier.testTag("about_button")) {
        Icon(imageVector = Icons.Filled.Info, contentDescription = "정보")
      }
    }
    Text(
      text = "25분/50분 집중과 할 일을 한 화면에서 관리합니다.",
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun TimerSection(
  state: MainScreenUiState,
  onSelectDuration: (Int) -> Unit,
  onStartTimer: () -> Unit,
  onPauseTimer: () -> Unit,
  onResetTimer: () -> Unit,
) {
  val selectedTodo = state.todos.firstOrNull { it.id == state.timer.selectedTodoId }

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    TimerPresetSelector(timer = state.timer, onSelectDuration = onSelectDuration)
    TimerDial(timer = state.timer)
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = selectedTodo?.title ?: "연결된 할 일 없음",
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = "할 일을 탭하면 이번 집중 세션에 연결됩니다.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
      )
    }
    TimerControls(
      timer = state.timer,
      onStartTimer = onStartTimer,
      onPauseTimer = onPauseTimer,
      onResetTimer = onResetTimer,
    )
  }
}

@Composable
private fun TimerPresetSelector(
  timer: TimerState,
  onSelectDuration: (Int) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    TIMER_PRESETS.forEachIndexed { index, preset ->
      val selected = timer.totalSeconds == preset.durationSeconds
      val enabled = timer.status != TimerStatus.Running
      val buttonModifier =
        Modifier
          .widthIn(min = 94.dp)
          .testTag("timer_preset_${preset.durationSeconds}")

      if (selected) {
        Button(
          onClick = { onSelectDuration(preset.durationSeconds) },
          enabled = enabled,
          modifier = buttonModifier,
          shape =
            when (index) {
              0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
              TIMER_PRESETS.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
              else -> RoundedCornerShape(0.dp)
            },
        ) {
          Text(preset.label)
        }
      } else {
        OutlinedButton(
          onClick = { onSelectDuration(preset.durationSeconds) },
          enabled = enabled,
          modifier = buttonModifier,
          shape =
            when (index) {
              0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
              TIMER_PRESETS.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
              else -> RoundedCornerShape(0.dp)
            },
        ) {
          Text(preset.label)
        }
      }
    }
  }
}

@Composable
private fun TimerDial(timer: TimerState) {
  val trackColor = MaterialTheme.colorScheme.surfaceVariant
  val progressColor =
    when (timer.status) {
      TimerStatus.Completed -> MaterialTheme.colorScheme.tertiary
      TimerStatus.Running -> MaterialTheme.colorScheme.primary
      TimerStatus.Paused -> MaterialTheme.colorScheme.secondary
      TimerStatus.Idle -> MaterialTheme.colorScheme.primary
    }

  Box(
    modifier =
      Modifier
        .widthIn(max = 290.dp)
        .fillMaxWidth(0.82f)
        .aspectRatio(1f)
        .testTag("timer_dial"),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
      val strokeWidth = 20.dp.toPx()
      val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
      val topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2, strokeWidth / 2)
      drawArc(
        color = trackColor,
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
      )
      drawArc(
        color = progressColor,
        startAngle = -90f,
        sweepAngle = timer.elapsedFraction.coerceIn(0f, 1f) * 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
      )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text(
        text = timer.timeText,
        modifier = Modifier.testTag("timer_time"),
        fontSize = 54.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp,
      )
      Text(
        text = timer.status.label,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.titleSmall,
      )
    }
  }
}

@Composable
private fun TimerControls(
  timer: TimerState,
  onStartTimer: () -> Unit,
  onPauseTimer: () -> Unit,
  onResetTimer: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Button(
      onClick = if (timer.status == TimerStatus.Running) onPauseTimer else onStartTimer,
      modifier = Modifier.widthIn(min = 128.dp).testTag("timer_primary_action"),
    ) {
      Icon(
        imageVector = if (timer.status == TimerStatus.Running) Icons.Filled.Pause else Icons.Filled.PlayArrow,
        contentDescription = null,
      )
      Spacer(Modifier.width(8.dp))
      Text(if (timer.status == TimerStatus.Running) "일시정지" else "시작")
    }
    Spacer(Modifier.width(10.dp))
    OutlinedButton(onClick = onResetTimer, modifier = Modifier.widthIn(min = 104.dp)) {
      Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
      Spacer(Modifier.width(8.dp))
      Text("초기화")
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTodoSection(
  title: String,
  onTitleChange: (String) -> Unit,
  onAddTodo: () -> Unit,
) {
  val canAdd = title.trim().isNotEmpty()

  OutlinedTextField(
    value = title,
    onValueChange = onTitleChange,
    modifier = Modifier.fillMaxWidth().testTag("todo_input"),
    singleLine = true,
    label = { Text("할 일") },
    placeholder = { Text("예: 기획서 초안 작성") },
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    keyboardActions = KeyboardActions(onDone = { if (canAdd) onAddTodo() }),
    trailingIcon = {
      IconButton(
        onClick = onAddTodo,
        enabled = canAdd,
        modifier = Modifier.testTag("add_todo_button"),
      ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "할 일 추가")
      }
    },
  )
}

@Composable
private fun TodoListHeader(todos: List<TodoItem>, selectedTodoId: Long?) {
  val completedCount = todos.count { it.isCompleted }
  val selectedText =
    todos.firstOrNull { it.id == selectedTodoId }?.title ?: "선택 없음"

  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(text = "오늘의 할 일", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
      Text(
        text = "$completedCount/${todos.size}",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.titleMedium,
      )
    }
    Text(
      text = "집중 대상: $selectedText",
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.bodySmall,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun EmptyTodoHint() {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
    shape = RoundedCornerShape(8.dp),
  ) {
    Text(
      text = "첫 할 일을 추가하고 시작 버튼을 누르세요.",
      modifier = Modifier.padding(16.dp),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun TodoRow(
  todo: TodoItem,
  selected: Boolean,
  onToggleTodo: () -> Unit,
  onDeleteTodo: () -> Unit,
  onSelectTodo: () -> Unit,
) {
  val borderColor =
    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

  Surface(
    modifier =
      Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
        .clickable(onClick = onSelectTodo)
        .testTag("todo_row_${todo.id}"),
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(8.dp),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Checkbox(checked = todo.isCompleted, onCheckedChange = { onToggleTodo() })
      Icon(
        imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
        contentDescription = null,
        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = todo.title,
          style = MaterialTheme.typography.bodyLarge,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
        )
        Text(
          text = "완료한 집중 세션 ${todo.pomodoroCount}회",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.bodySmall,
        )
      }
      IconButton(onClick = onDeleteTodo) {
        Icon(imageVector = Icons.Filled.Delete, contentDescription = "할 일 삭제")
      }
    }
  }
}

@Composable
private fun CompletionDialog(onDismiss: () -> Unit) {
  AlertDialog(
    modifier = Modifier.testTag("completion_alert"),
    onDismissRequest = onDismiss,
    icon = { Icon(imageVector = Icons.Filled.Timer, contentDescription = null) },
    title = { Text("포커스 완료!") },
    text = { Text("집중 세션이 완료되었습니다. 타이머가 멈췄고 연결된 할 일에 세션이 기록됩니다.") },
    confirmButton = { FilledTonalButton(onClick = onDismiss) { Text("확인") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("닫기") } },
  )
}

private val TimerStatus.label: String
  get() =
    when (this) {
      TimerStatus.Idle -> "대기"
      TimerStatus.Running -> "진행 중"
      TimerStatus.Paused -> "일시정지"
      TimerStatus.Completed -> "완료"
    }

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  PomoTodoTheme {
    MainScreenContent(
      state =
        MainScreenUiState(
          todos =
            listOf(
              TodoItem(id = 1, title = "기획서 초안 작성", isCompleted = false, pomodoroCount = 1),
              TodoItem(id = 2, title = "디자인 피드백 정리", isCompleted = true, pomodoroCount = 2),
            ),
          timer = TimerState(remainingSeconds = 18 * 60, status = TimerStatus.Running, selectedTodoId = 1),
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

@Preview(showBackground = true, widthDp = 340)
@Composable
fun MainScreenPortraitPreview() {
  PomoTodoTheme {
    MainScreenContent(
      state = MainScreenUiState(),
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
