package todolist.al.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import todolist.al.data.model.Task
import todolist.al.receiver.NotificationReceiver
import java.time.ZoneId

object AlarmUtils {

    fun setAlarm(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderTime = task.reminder ?: return
        val now = System.currentTimeMillis()

        val times = listOf(
            "EXACT_TIME" to reminderTime,
            "ONE_HOUR_BEFORE" to reminderTime.minusHours(1),
            "TEN_MIN_BEFORE" to reminderTime.minusMinutes(10)
        )

        for ((tag, time) in times) {
            val millis = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            if (millis > now) {
                val intent = Intent(context, NotificationReceiver::class.java).apply {
                    putExtra("taskTitle", task.title)
                    putExtra("taskId", "${task.id}_$tag")
                    putExtra("timeTag", tag)
                    putExtra("priority", task.priority.name)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    "${task.id}_$tag".hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    millis,
                    pendingIntent
                )
            }
        }
    }

    fun cancelAlarms(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val tags = listOf("EXACT_TIME", "ONE_HOUR_BEFORE", "TEN_MIN_BEFORE")

        for (tag in tags) {
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                "${task.id}_$tag".hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
