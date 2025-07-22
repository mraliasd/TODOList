package todolist.al.ui.components.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import todolist.al.data.model.SortOption
import todolist.al.data.model.Task

@Composable
fun TaskList(
    tasks: List<Task>,
    onToggle: (Int) -> Unit,
    onEdit: (Task) -> Unit,
    onDelete: (Int) -> Unit,
    sortOption: SortOption = SortOption.TIME
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var pendingDeleteTaskId by remember { mutableStateOf<Int?>(null) }

    val activeTasks = tasks.filter { !it.isDone }
    val completedTasks = tasks.filter { it.isDone }

    val sortedActiveTasks = when (sortOption) {
        SortOption.TITLE -> activeTasks.sortedBy { it.title }
        SortOption.TIME -> activeTasks.sortedBy { it.dueDate }
        SortOption.PRIORITY -> activeTasks.sortedBy { it.priority }
    }

    val sortedCompletedTasks = when (sortOption) {
        SortOption.TITLE -> completedTasks.sortedBy { it.title }
        SortOption.TIME -> completedTasks.sortedBy { it.dueDate }
        SortOption.PRIORITY -> completedTasks.sortedBy { it.priority }
    }

    if (showDeleteDialog && pendingDeleteTaskId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(pendingDeleteTaskId!!)
                    showDeleteDialog = false
                    pendingDeleteTaskId = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    pendingDeleteTaskId = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        if (sortedActiveTasks.isNotEmpty()) {
            item {
                Text(
                    "Active Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(sortedActiveTasks, key = { it.id }) { task ->
                TaskListItem(task, onToggle, onEdit, onDelete, onShowDialog = {
                    pendingDeleteTaskId = it
                    showDeleteDialog = true
                })
            }
        }

        if (sortedCompletedTasks.isNotEmpty()) {
            item {
                Text(
                    "Completed Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(sortedCompletedTasks, key = { it.id }) { task ->
                TaskListItem(task, onToggle, onEdit, onDelete, onShowDialog = {
                    pendingDeleteTaskId = it
                    showDeleteDialog = true
                })
            }
        }
    }
}
