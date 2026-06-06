package com.example.pomotodo.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.pomotodo.MainActivity
import com.example.pomotodo.R

class AndroidPomodoroNotifier(private val context: Context) : PomodoroNotifier {
  init {
    createNotificationChannel()
  }

  @SuppressLint("MissingPermission")
  override fun showTimerProgress(totalSeconds: Int, remainingSeconds: Int) {
    if (!canPostNotifications()) return

    val elapsedSeconds = (totalSeconds - remainingSeconds).coerceIn(0, totalSeconds)
    val notification =
      NotificationCompat.Builder(context, PROGRESS_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_pomotodo)
        .setContentTitle("PomoTodo 진행 중")
        .setContentText("${formatTime(remainingSeconds)} 남음")
        .setSubText("${totalSeconds / 60}분 집중")
        .setContentIntent(createContentIntent())
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setSilent(true)
        .setProgress(totalSeconds, elapsedSeconds, false)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    NotificationManagerCompat.from(context).notify(TIMER_PROGRESS_NOTIFICATION_ID, notification)
  }

  override fun cancelTimerProgress() {
    NotificationManagerCompat.from(context).cancel(TIMER_PROGRESS_NOTIFICATION_ID)
  }

  @SuppressLint("MissingPermission")
  override fun showFocusCompleted(minutes: Int) {
    if (!canPostNotifications()) return

    val notification =
      NotificationCompat.Builder(context, COMPLETE_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_pomotodo)
        .setContentTitle("포커스 완료!")
        .setContentText("${minutes}분 집중 세션이 완료되었습니다.")
        .setContentIntent(createContentIntent())
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    NotificationManagerCompat.from(context).notify(FOCUS_COMPLETE_NOTIFICATION_ID, notification)
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val channel =
      NotificationChannel(
        COMPLETE_CHANNEL_ID,
        "PomoTodo 완료 알림",
        NotificationManager.IMPORTANCE_DEFAULT,
      ).apply {
        description = "뽀모도로 집중 세션이 끝났을 때 알림을 보냅니다."
      }
    val progressChannel =
      NotificationChannel(
        PROGRESS_CHANNEL_ID,
        "PomoTodo 진행 상태",
        NotificationManager.IMPORTANCE_LOW,
      ).apply {
        description = "진행 중인 집중 세션의 남은 시간과 진행률을 보여줍니다."
      }

    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannels(listOf(channel, progressChannel))
  }

  private fun createContentIntent(): PendingIntent {
    val intent =
      Intent(context, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    return PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
  }

  private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
  }

  private fun canPostNotifications(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
      ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

  private companion object {
    const val COMPLETE_CHANNEL_ID = "pomotodo_focus_complete"
    const val PROGRESS_CHANNEL_ID = "pomotodo_timer_progress"
    const val FOCUS_COMPLETE_NOTIFICATION_ID = 2500
    const val TIMER_PROGRESS_NOTIFICATION_ID = 2501
  }
}
