package todolist.al.ui.components.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import todolist.al.data.model.Task

@Composable
fun TaskList(
    tasks: List<Task>,
    onToggle: (Int) -> Unit,
    onEdit: (Task) -> Unit,
    onDelete: (Int) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var pendingDeleteTaskId by remember { mutableStateOf<Int?>(null) }

    val activeTasks = tasks.filter { !it.isDone && it.parentId == null }
    val completedTasks = tasks.filter { it.isDone && it.parentId == null }

    if (showDeleteDialog && pendingDeleteTaskId != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                pendingDeleteTaskId = null
            },
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
        if (activeTasks.isNotEmpty()) {
            item {
                Text(
                    "Active Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(activeTasks, key = { "main_${it.id}" }) { task ->
                TaskListItem(
                    task = task,
                    onToggle = onToggle,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onShowDialog = {
                        pendingDeleteTaskId = it
                        showDeleteDialog = true
                    },
                    allTasks = tasks
                )
            }
        }

        if (completedTasks.isNotEmpty()) {
            item {
                Text(
                    "Completed Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(completedTasks, key = { "done_${it.id}" }) { task ->
                TaskListItem(
                    task = task,
                    onToggle = onToggle,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onShowDialog = {
                        pendingDeleteTaskId = it
                        showDeleteDialog = true
                    },
                    allTasks = tasks
                )
            }
        }
    }
}
