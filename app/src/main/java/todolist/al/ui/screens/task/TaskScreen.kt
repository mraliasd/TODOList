package todolist.al.ui.screens.task

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import todolist.al.viewmodel.TaskViewModel
import todolist.al.viewmodel.TaskViewModelFactory
import todolist.al.ui.components.task.*
import todolist.al.ui.components.EmptyTaskPlaceholder
import todolist.al.ui.components.HomeBottomBar

@Composable
fun TaskScreen(
    navController: NavHostController,
    onTaskAdded: () -> Unit = {},
    taskId: Int? = null
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(context))

    val tasks = viewModel.tasks
    var isBottomSheetVisible by remember { mutableStateOf(true) }

    val taskToEdit = remember(taskId, tasks) {
        taskId?.let { id -> tasks.find { it.id == id } }
    }

    Scaffold(
        topBar = { TaskTopBar() },
        bottomBar = {
            HomeBottomBar(
                onHomeClick = { navController.navigate("home") },
                onCalendarClick = { navController.navigate("calendar") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (tasks.isEmpty()) {
                EmptyTaskPlaceholder()
            } else {
                TaskList(
                    tasks = tasks,
                    onToggle = { id -> viewModel.toggleTaskStatus(id, context) },
                    onEdit = { task -> navController.navigate("task/${task.id}") },
                    onDelete = { id -> viewModel.deleteTask(id) }
                )
            }

            if (isBottomSheetVisible) {
                AddTaskBottomSheet(
                    existingTask = taskToEdit,
                    onAdd = { task ->
                        if (taskToEdit != null) {
                            viewModel.updateTask(task)
                        } else {
                            viewModel.addTask(task)
                        }
                        isBottomSheetVisible = false
                        onTaskAdded()
                    },
                    onDismiss = {
                        isBottomSheetVisible = false
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
