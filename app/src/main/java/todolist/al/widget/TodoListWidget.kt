package todolist.al.widget

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.*
import todolist.al.MainActivity
import todolist.al.R
import todolist.al.data.local.TaskDatabaseHelper
import java.time.format.DateTimeFormatter

class TodoListWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            updateWidget(context, manager, id)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        schedulePeriodicUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelPeriodicUpdate(context)
    }

    @SuppressLint("RemoteViewLayout")
    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        CoroutineScope(Dispatchers.IO).launch {
            val db = TaskDatabaseHelper(context)
            val allTasks = db.getAllTasks().filter { !it.isDone }
            val mainTasks = allTasks.filter { it.parentId == null }.take(5)

            withContext(Dispatchers.Main) {
                views.removeAllViews(R.id.widget_task_container)

                mainTasks.forEach { task ->
                    val blockLayoutId = if (isRTL(task.title)) {
                        R.layout.widget_task_item_rtl
                    } else {
                        R.layout.widget_task_item_ltr
                    }

                    val blockView = RemoteViews(context.packageName, blockLayoutId)


                    blockView.setTextViewText(R.id.widget_task_title, task.title)
                    val timeFormatted = task.dueDate?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
                    blockView.setTextViewText(R.id.widget_task_time, timeFormatted)

                    val subTasks = allTasks.filter { it.parentId == task.id }

                    subTasks.forEach { subtask ->
                        val layoutId = if (isRTL(subtask.title)) {
                            R.layout.widget_task_item_rtl
                        } else {
                            R.layout.widget_task_item_ltr
                        }

                        val subView = RemoteViews(context.packageName, layoutId)
                        subView.setTextViewText(R.id.widget_task_title, "• ${subtask.title}")
                        val subTime = subtask.dueDate?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
                        subView.setTextViewText(R.id.widget_task_time, subTime)

                        blockView.addView(R.id.widget_subtask_container, subView)
                    }

                    views.addView(R.id.widget_task_container, blockView)
                }

                manager.updateAppWidget(widgetId, views)
            }
        }
    }




    private fun isRTL(text: String): Boolean {
        val firstLetter = text.firstOrNull { it.isLetter() } ?: return false
        val dir = Character.getDirectionality(firstLetter)
        return dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT || dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
    }


    private fun schedulePeriodicUpdate(context: Context) {
        val intent = Intent(context, WidgetUpdateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val interval = 5 * 60 * 1000L

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval,
            interval,
            pendingIntent
        )
    }

    private fun cancelPeriodicUpdate(context: Context) {
        val intent = Intent(context, WidgetUpdateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, TodoListWidget::class.java))
            TodoListWidget().onUpdate(context, manager, ids)
        }
    }
}
