package todolist.al.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import todolist.al.data.local.TaskDatabaseHelper
import todolist.al.data.model.SortOption
import todolist.al.data.model.Task
import todolist.al.util.AlarmUtils
import todolist.al.widget.TaskChangeBroadcaster

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = TaskDatabaseHelper(application)

    private val _tasks = mutableStateListOf<Task>()
    val tasks: SnapshotStateList<Task> get() = _tasks

    private var currentSort: SortOption = SortOption.TIME

    init {
        loadTasks(currentSort)
    }

    fun loadTasks(sortOption: SortOption = SortOption.TIME) {
        currentSort = sortOption
        val sortedList = when (sortOption) {
            SortOption.TIME -> dbHelper.getTasksSortedByTime()
            SortOption.TITLE -> dbHelper.getTasksSortedByTitle()
            SortOption.PRIORITY -> dbHelper.getTasksSortedByPriority()
        }
        _tasks.clear()
        _tasks.addAll(sortedList)
    }

    fun refreshTasks() {
        loadTasks(currentSort)
    }

    fun addTask(task: Task) {
        dbHelper.insertTask(task)
        refreshTasks()
        TaskChangeBroadcaster.notifyChange(getApplication())
    }

    fun deleteTask(taskId: Int) {
        dbHelper.deleteTask(taskId)
        refreshTasks()
        TaskChangeBroadcaster.notifyChange(getApplication())

    }

    fun updateTask(updatedTask: Task) {
        dbHelper.updateTask(updatedTask)
        refreshTasks()
        TaskChangeBroadcaster.notifyChange(getApplication())

    }

    fun toggleTaskStatus(taskId: Int, context: Context) {
        val task = _tasks.find { it.id == taskId }
        task?.let {
            val updated = it.copy(isDone = !it.isDone)
            if (it.isDone) {
                AlarmUtils.cancelAlarms(context, task)
            }
            dbHelper.updateTask(updated)
            refreshTasks()
            TaskChangeBroadcaster.notifyChange(getApplication())

        }
    }
}
