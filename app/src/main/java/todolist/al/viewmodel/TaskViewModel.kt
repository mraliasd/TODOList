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
            if (updated.isDone) {
                AlarmUtils.cancelAlarms(context, updated)
            }
            dbHelper.updateTask(updated)
            refreshTasks()
            TaskChangeBroadcaster.notifyChange(getApplication())
        }
    }

    fun getSubtasks(parentId: Int): List<Task> {
        return dbHelper.getAllTasks().filter { it.parentId == parentId }
    }

    fun getMainTasks(): List<Task> {
        return dbHelper.getAllTasks().filter { it.parentId == null }
    }

    fun getTaskById(taskId: Int): Task? {
        return dbHelper.getAllTasks().find { it.id == taskId }
    }

    fun addSubtask(subtask: Task) {
        dbHelper.insertTask(subtask)
        refreshTasks()
        TaskChangeBroadcaster.notifyChange(getApplication())
    }

    fun updateSubtask(subtask: Task) {
        dbHelper.updateTask(subtask)
        refreshTasks()
        TaskChangeBroadcaster.notifyChange(getApplication())
    }

    fun deleteSubtask(subtaskId: Int) {
        dbHelper.deleteTask(subtaskId)
        refreshTasks()
        TaskChangeBroadcaster.notifyChange(getApplication())
    }
}