package com.example.pomotodo

import android.app.Application
import com.example.pomotodo.data.PomoTodoDatabase
import com.example.pomotodo.data.PomoTodoRepository
import com.example.pomotodo.data.RoomPomoTodoRepository
import com.example.pomotodo.notifications.AndroidPomodoroNotifier
import com.example.pomotodo.ui.main.PomodoroTimerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class PomoTodoApplication : Application() {
  private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  val repository: PomoTodoRepository by lazy {
    RoomPomoTodoRepository(PomoTodoDatabase.getDatabase(this).todoDao())
  }

  val timerController: PomodoroTimerController by lazy {
    PomodoroTimerController(
      repository = repository,
      notifier = AndroidPomodoroNotifier(this),
      scope = applicationScope,
    )
  }
}
