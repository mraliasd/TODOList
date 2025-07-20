package todolist.al.ui.components.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import todolist.al.data.model.Task




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskListItem(
    task: Task,
    onToggle: (Int) -> Unit,
    onEdit: (Task) -> Unit,
    onDelete: (Int) -> Unit,
    onShowDialog: (Int) -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToStart -> {
                    onEdit(task)
                    false
                }

                DismissValue.DismissedToEnd -> {
                    onShowDialog(task.id)
                    false
                }

                else -> true
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(
            DismissDirection.EndToStart,
            DismissDirection.StartToEnd
        ),
        background = {
            val direction = dismissState.dismissDirection
            val icon = when (direction) {
                DismissDirection.EndToStart -> Icons.Default.Edit
                DismissDirection.StartToEnd -> Icons.Default.Delete
                else -> null
            }

            val alignment = when (direction) {
                DismissDirection.EndToStart -> Alignment.CenterEnd
                DismissDirection.StartToEnd -> Alignment.CenterStart
                else -> Alignment.Center
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                icon?.let {
                    val tintColor = when (it) {
                        Icons.Default.Delete -> Color.Red
                        Icons.Default.Edit -> Color(0xFF2196F3)
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Icon(
                        imageVector = it,
                        contentDescription = if (it == Icons.Default.Delete) "Delete" else "Edit",
                        tint = tintColor
                    )
                }
            }
        },
        dismissContent = {
            TaskItem(
                task = task,
                onToggleDone = { onToggle(task.id) }
            )
        }
    )
}