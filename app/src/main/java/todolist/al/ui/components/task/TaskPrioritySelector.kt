package todolist.al.ui.components.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import todolist.al.data.model.TaskPriority

@Composable
fun TaskPrioritySelector(
    selected: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskPriority.entries.forEach { priority ->
                val color = priority.color

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if(selected == priority) color else color.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable { onPrioritySelected(priority) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(priority.name.first().toString(), color= Color.Black)
                }
            }
        }
    }
}