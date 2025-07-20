package todolist.al.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import todolist.al.data.model.TaskPriority
import todolist.al.util.NotificationUtils

class NotificationReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("taskTitle") ?: "Reminder"
        val timeTag = intent.getStringExtra("timeTag") ?: "UNKNOWN"
        val taskId = intent.getStringExtra("taskId")?.hashCode() ?: 0
        val priorityStr = intent.getStringExtra("priority") ?: "NORMAL"
        val priority = TaskPriority.valueOf(priorityStr)

        val (shouldNotify, withSound) = when (priority) {
            TaskPriority.HIGH -> true to true
            TaskPriority.NORMAL -> when (timeTag) {
                "EXACT_TIME", "TEN_MIN_BEFORE" -> true to true
                else -> true to false
            }
            TaskPriority.LOW -> when (timeTag) {
                "EXACT_TIME" -> true to false
                else -> false to false
            }
        }

        if (shouldNotify) {
            val message = when (timeTag) {
                "ONE_HOUR_BEFORE" -> "1 hour left: $taskTitle"
                "TEN_MIN_BEFORE" -> "10 minutes left: $taskTitle"
                "EXACT_TIME" -> "It's time: $taskTitle"
                else -> taskTitle
            }

            NotificationUtils.showNotification(
                context = context,
                title = "Task Reminder",
                message = message,
                notificationId = taskId,
                withSound = withSound
            )
        }
    }
}