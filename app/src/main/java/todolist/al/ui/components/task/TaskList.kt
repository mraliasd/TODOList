package todolist.al.ui.components.task

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val activeTasks = tasks.filter { !it.isDone }
    val completedTasks = tasks.filter { it.isDone }

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

    LaunchedEffect(tasks) {
        val logText = tasks.joinToString(separator = "\n") { task ->
            "- ${task.title} [${task.priority.name}]"
        }
        Log.d("TaskList", "Updated Task List:\n$logText")
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

            items(activeTasks, key = { it.id }) { task ->
                TaskListItem(task, onToggle, onEdit, onDelete, onShowDialog = {
                    pendingDeleteTaskId = it
                    showDeleteDialog = true
                })
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

            items(completedTasks, key = { it.id }) { task ->
                TaskListItem(task, onToggle, onEdit, onDelete, onShowDialog = {
                    pendingDeleteTaskId = it
                    showDeleteDialog = true
                })
            }
        }
    }
}






