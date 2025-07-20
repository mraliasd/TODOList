package todolist.al.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import todolist.al.data.local.TaskDatabaseHelper
import todolist.al.data.model.Task
import todolist.al.util.AlarmUtils

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = TaskDatabaseHelper(application)

    private val _tasks = mutableStateListOf<Task>()
    val tasks: SnapshotStateList<Task> get() = _tasks

    init {
        refreshTasks()
    }

    fun refreshTasks() {
        _tasks.clear()
        _tasks.addAll(dbHelper.getAllTasks())
    }

    fun addTask(task: Task) {
        dbHelper.insertTask(task)
        refreshTasks()
    }

    fun deleteTask(taskId: Int) {
        dbHelper.deleteTask(taskId)
        refreshTasks()
    }

    fun updateTask(updatedTask: Task) {
        val index = _tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            _tasks[index] = updatedTask
            dbHelper.updateTask(updatedTask)
        }
        refreshTasks()
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
        }
    }
}
