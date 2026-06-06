package com.example.pomotodo.notifications

interface PomodoroNotifier {
  fun showTimerProgress(totalSeconds: Int, remainingSeconds: Int)

  fun cancelTimerProgress()

  fun showFocusCompleted(minutes: Int)
}
