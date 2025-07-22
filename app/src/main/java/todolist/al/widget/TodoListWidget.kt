package todolist.al.widget

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
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
        Log.d("TodoListWidget", "Updating widget for ID: $widgetId")
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        CoroutineScope(Dispatchers.IO).launch {
            val db = TaskDatabaseHelper(context)
            val tasks = db.getAllTasks().filter { !it.isDone }.take(5)
            Log.d("TodoListWidget", "Fetched ${tasks.size} tasks for widget")
            withContext(Dispatchers.Main) {
                views.removeAllViews(R.id.widget_task_container)

                tasks.forEach { task ->
                    val itemView = RemoteViews(context.packageName, R.layout.widget_task_item)

                    itemView.setTextViewText(R.id.widget_task_title, task.title)

                    val timeFormatted = task.dueDate?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
                    itemView.setTextViewText(R.id.widget_task_time, timeFormatted)

                    views.addView(R.id.widget_task_container, itemView)
                }


                manager.updateAppWidget(widgetId, views)
            }
        }
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
