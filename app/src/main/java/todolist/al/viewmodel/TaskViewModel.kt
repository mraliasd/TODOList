package todolist.al.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import todolist.al.data.local.TaskDatabaseHelper
import todolist.al.data.model.*
import todolist.al.util.AlarmUtils
import todolist.al.widget.TaskChangeBroadcaster
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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

        if (task.recurringType != RecurringType.NONE) {
            val nextTasks = generateRecurringInstances(task)
            nextTasks.forEach { dbHelper.insertTask(it) }
        }

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

                if (updated.recurringType != RecurringType.NONE) {
                    val nextTasks = generateRecurringInstances(updated)
                    nextTasks.forEach { nextTask -> addTask(nextTask) }
                }
            }

            dbHelper.updateTask(updated)
            refreshTasks()
            TaskChangeBroadcaster.notifyChange(getApplication())
        }
    }

    private fun generateRecurringInstances(task: Task): List<Task> {
        val now = LocalDateTime.now()
        val endDate = task.recurringEndDate ?: now.plusMonths(3)

        return when (task.recurringType) {
            RecurringType.DAILY -> {
                val list = mutableListOf<Task>()
                var nextDate = task.dueDate?.plusDays(1) ?: return emptyList()
                val baseTime = task.originalTime ?: nextDate.toLocalTime()

                while (nextDate.isBefore(endDate)) {
                    list.add(task.copy(
                        id = 0,
                        isDone = false,
                        dueDate = LocalDateTime.of(nextDate.toLocalDate(), baseTime),
                        createdAt = now
                    ))
                    nextDate = nextDate.plusDays(1)
                }

                list
            }

            RecurringType.WEEKLY -> {
                val list = mutableListOf<Task>()
                var nextDate = task.dueDate?.plusWeeks(1) ?: return emptyList()
                val baseTime = task.originalTime ?: nextDate.toLocalTime()

                while (nextDate.isBefore(endDate)) {
                    list.add(task.copy(
                        id = 0,
                        isDone = false,
                        dueDate = LocalDateTime.of(nextDate.toLocalDate(), baseTime),
                        createdAt = now
                    ))
                    nextDate = nextDate.plusWeeks(1)
                }

                list
            }

            RecurringType.MONTHLY -> {
                val list = mutableListOf<Task>()
                var nextDate = task.dueDate?.plusMonths(1) ?: return emptyList()
                val baseTime = task.originalTime ?: nextDate.toLocalTime()

                while (nextDate.isBefore(endDate)) {
                    list.add(task.copy(
                        id = 0,
                        isDone = false,
                        dueDate = LocalDateTime.of(nextDate.toLocalDate(), baseTime),
                        createdAt = now
                    ))
                    nextDate = nextDate.plusMonths(1)
                }

                list
            }

            RecurringType.CUSTOM -> {
                when (task.recurringMode) {
                    CustomRecurringMode.INTERVAL -> {
                        val interval = task.recurringInterval ?: return emptyList()
                        val baseDate = task.dueDate?.toLocalDate() ?: return emptyList()
                        val baseTime = task.originalTime ?: task.dueDate?.toLocalTime() ?: LocalTime.now()

                        val list = mutableListOf<Task>()
                        var currentDate = baseDate.plusDays(interval.toLong())

                        while (currentDate.atTime(baseTime).isBefore(endDate)) {
                            list.add(task.copy(
                                id = 0,
                                isDone = false,
                                dueDate = LocalDateTime.of(currentDate, baseTime),
                                createdAt = now
                            ))
                            currentDate = currentDate.plusDays(interval.toLong())
                        }

                        list
                    }

                    CustomRecurringMode.WEEKDAYS -> {
                        val baseTime = task.originalTime ?: task.dueDate?.toLocalTime() ?: LocalTime.now()
                        val startDate = (task.dueDate ?: now).toLocalDate()
                        val times = task.recurringTimes.ifEmpty { listOf(baseTime) }

                        val list = mutableListOf<Task>()
                        var current = startDate

                        while (current.atTime(baseTime).isBefore(endDate)) {
                            if (task.recurringDays.contains(current.dayOfWeek)) {
                                times.forEach { time ->
                                    val dateTime = LocalDateTime.of(current, time)
                                    if (dateTime.isAfter(now) && dateTime.isBefore(endDate)) {
                                        list.add(task.copy(
                                            id = 0,
                                            isDone = false,
                                            dueDate = dateTime,
                                            createdAt = now
                                        ))
                                    }
                                }
                            }
                            current = current.plusDays(1)
                        }

                        list
                    }

                    CustomRecurringMode.SINGLE_DAY -> {
                        val baseDay = task.dueDate?.toLocalDate() ?: return emptyList()
                        val times = task.recurringTimes.ifEmpty {
                            listOf(
                                task.originalTime ?: task.dueDate.toLocalTime() ?: LocalTime.now()
                            )
                        }

                        times.mapNotNull { time ->
                            val dateTime = LocalDateTime.of(baseDay, time)
                            if (dateTime.isAfter(now)) {
                                task.copy(
                                    id = 0,
                                    isDone = false,
                                    dueDate = dateTime,
                                    createdAt = now
                                )
                            } else null
                        }
                    }

                    null -> emptyList()
                }
            }

            RecurringType.NONE -> emptyList()
        }
    }



    fun getSubtasks(parentId: Int): List<Task> {
        return dbHelper.getAllTasks().filter { it.parentId == parentId }
    }



}
