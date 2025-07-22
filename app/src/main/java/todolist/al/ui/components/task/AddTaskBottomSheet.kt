package todolist.al.ui.components.task

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import todolist.al.data.model.*
import todolist.al.util.AlarmUtils
import todolist.al.widget.TodoListWidget
import java.time.LocalDateTime

@Composable
fun AddTaskBottomSheet(
    onAdd: (Task) -> Unit,
    onDismiss: () -> Unit,
    existingTask: Task? = null
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(TextFieldValue(existingTask?.title ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(existingTask?.description ?: "")) }
    var selectedDate by remember { mutableStateOf(existingTask?.dueDate ?: LocalDateTime.now()) }
    var priority by remember { mutableStateOf(existingTask?.priority ?: TaskPriority.NORMAL) }
    var category by remember { mutableStateOf(existingTask?.category ?: TaskCategory.WORK) }
    var isCategoryDialogOpen by remember { mutableStateOf(false) }
    var isReminderEnabled by remember { mutableStateOf(existingTask?.reminder != null) }

    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (existingTask != null) "Edit Task" else "Add Task",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateTimePickerCard(
                initialDateTime = selectedDate,
                onDateTimeSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TaskPrioritySelector(priority) { priority = it }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Category", style = MaterialTheme.typography.labelMedium)
            OutlinedButton(
                onClick = { isCategoryDialogOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.label,
                    tint = category.color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(category.label)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = isReminderEnabled,
                    onCheckedChange = { isReminderEnabled = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reminder")
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (title.text.isNotBlank()) {
                            val reminderTime = if (isReminderEnabled) selectedDate else null

                            val taskToSave = Task(
                                id = existingTask?.id ?: 0,
                                title = title.text,
                                description = description.text,
                                dueDate = selectedDate,
                                priority = priority,
                                category = category,
                                reminder = reminderTime
                            )
                            onAdd(taskToSave)
                            val appWidgetManager = AppWidgetManager.getInstance(context)
                            val componentName = ComponentName(context, TodoListWidget::class.java)
                            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
                            for (widgetId in widgetIds) {
                                TodoListWidget().onUpdate(context, appWidgetManager, intArrayOf(widgetId))
                            }

                            if (isReminderEnabled && reminderTime != null) {
                                AlarmUtils.setAlarm(context, taskToSave)
                            }

                            onDismiss()
                        }
                    },
                    enabled = title.text.isNotBlank(),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(if (existingTask != null) "Save" else "Add")
                }

                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    }

    if (isCategoryDialogOpen) {
        AlertDialog(
            onDismissRequest = { isCategoryDialogOpen = false },
            confirmButton = {},
            text = {
                Column {
                    TaskCategory.values().forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    category = item
                                    isCategoryDialogOpen = false
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = item.color,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item.label)
                        }
                    }
                }
            }
        )
    }
}
