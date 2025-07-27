package todolist.al.ui.components.task

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import todolist.al.data.model.Task
import todolist.al.data.model.TaskCategory
import todolist.al.data.model.TaskPriority
import java.time.format.DateTimeFormatter



@Composable
fun TaskItem(
    task: Task,
    subTasks: List<Task> = emptyList(),
    onToggleDone: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            if (task.isDone) colorScheme.primary else colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable { onToggleDone() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    task.dueDate?.let {
                        Text(
                            text = it.format(DateTimeFormatter.ofPattern("MMM dd • HH:mm")),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                task.category?.let {
                    Spacer(modifier = Modifier.width(12.dp))
                    CategoryChip(it)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(task.priority.color, shape = CircleShape)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    task.reminder?.let {
                        Text(
                            text = "Reminder: ${it.format(DateTimeFormatter.ofPattern("MMM dd • HH:mm"))}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (task.recurringType != null && task.recurringType.name != "NONE") {
                        Text(
                            text = "Repeats: ${task.recurringType.name.lowercase().replaceFirstChar { it.uppercase() }}" +
                                    (if (task.recurringType.name == "CUSTOM" && task.recurringInterval != null)
                                        " every ${task.recurringInterval} days" else ""),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF388E3C)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (subTasks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Subtasks:",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        subTasks.forEach { subtask ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(subtask.priority.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(subtask.title, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun CategoryChip(category: TaskCategory) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0, 0,0, 50),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = category.color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
            )
        }
    }
}

