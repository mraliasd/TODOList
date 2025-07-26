package todolist.al.ui.components.task

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import todolist.al.data.model.*
import todolist.al.util.AlarmUtils
import todolist.al.viewmodel.TaskViewModel
import todolist.al.widget.TodoListWidget
import java.time.LocalDateTime

@Composable
fun AddTaskBottomSheet(
    navController: NavHostController,
    onAdd: (Task) -> Unit,
    onDismiss: () -> Unit,
    existingTask: Task? = null,
    viewModel: TaskViewModel,
    allTasks: List<Task> = emptyList()
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(TextFieldValue(existingTask?.title ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(existingTask?.description ?: "")) }
    var selectedDate by remember { mutableStateOf(existingTask?.dueDate ?: LocalDateTime.now()) }
    var priority by remember { mutableStateOf(existingTask?.priority ?: TaskPriority.NORMAL) }
    var category by remember { mutableStateOf(existingTask?.category ?: TaskCategory.WORK) }
    var isCategoryDialogOpen by remember { mutableStateOf(false) }
    var isReminderEnabled by remember { mutableStateOf(existingTask?.reminder != null) }

    data class SubTaskInput(var title: String, var priority: TaskPriority)

    val subTasks = remember { mutableStateListOf<SubTaskInput>() }

    LaunchedEffect(existingTask, allTasks) {
        subTasks.clear()
        existingTask?.let { task ->
            val relatedSubTasks = allTasks.filter { it.parentId == task.id }
                .map { SubTaskInput(it.title, it.priority) }
            subTasks.addAll(relatedSubTasks)
        }
    }

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

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
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

                Spacer(modifier = Modifier.height(12.dp))

                // نمایش ساب‌تسک‌ها
                subTasks.forEachIndexed { index, subtask ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = subtask.title,
                                onValueChange = { subTasks[index] = subtask.copy(title = it) },
                                label = { Text("Subtask ${index + 1}") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { subTasks.removeAt(index) }) {
                                Icon(Icons.Default.Remove, contentDescription = "Delete Subtask")
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        TaskPrioritySelector(
                            selected = subtask.priority,
                            onPrioritySelected = { subTasks[index] = subtask.copy(priority = it) }
                        )
                    }
                }

                TextButton(
                    onClick = { subTasks.add(SubTaskInput("", TaskPriority.NORMAL)) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("+ Add Subtask")
                }

                Spacer(modifier = Modifier.height(8.dp))

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
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Button(
                    onClick = {
                        if (title.text.isNotBlank()) {
                            val reminderTime = if (isReminderEnabled) selectedDate else null
                            val newId = existingTask?.id ?: (viewModel.tasks.maxOfOrNull { it.id } ?: 0) + 1

                            val taskToSave = Task(
                                id = newId,
                                title = title.text,
                                description = description.text,
                                dueDate = selectedDate,
                                priority = priority,
                                category = category,
                                reminder = reminderTime,
                                parentId = existingTask?.parentId
                            )

                            if (existingTask != null) {
                                onAdd(taskToSave)
                            } else {
                                viewModel.addTask(taskToSave)
                            }

                            // حذف ساب‌تسک‌های قبلی در صورت ویرایش
                            if (existingTask != null) {
                                val oldSubtasks = viewModel.tasks.filter { it.parentId == existingTask.id }
                                oldSubtasks.forEach { viewModel.deleteTask(it.id) }
                            }

                            // افزودن ساب‌تسک‌های جدید
                            subTasks
                                .filter { it.title.isNotBlank() }
                                .forEach {
                                    viewModel.addTask(
                                        Task(
                                            title = it.title,
                                            parentId = newId,
                                            priority = it.priority
                                        )
                                    )
                                }

                            if (isReminderEnabled && reminderTime != null) {
                                AlarmUtils.setAlarm(context, taskToSave)
                            }

                            onDismiss()
                            navController.navigate("home")
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
                                .padding(8.dp)
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

