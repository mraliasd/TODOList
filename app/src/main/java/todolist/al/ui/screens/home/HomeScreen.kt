package todolist.al.ui.screens.home

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import todolist.al.ui.components.task.TaskList
import todolist.al.ui.components.EmptyTaskPlaceholder
import todolist.al.ui.components.HomeBottomBar
import todolist.al.ui.components.HomeTopBar
import todolist.al.viewmodel.TaskViewModel
import todolist.al.viewmodel.TaskViewModelFactory
import java.time.LocalDate


@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(context))

    val allTasks = viewModel.tasks

    val today = LocalDate.now()
    val todayTasks by remember(allTasks) {
        derivedStateOf {
            allTasks.filter { it.dueDate?.toLocalDate() == today }
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar()
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("task")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            HomeBottomBar(
                onHomeClick = { /* Already here */ },
                onCalendarClick = { navController.navigate("calendar") },
                selectedScreen = "home"
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
                .fillMaxSize()
        ) {
            if (todayTasks.isEmpty()) {
                EmptyTaskPlaceholder()
            } else {
                TaskList(
                    tasks = todayTasks,
                    onToggle = { taskId -> viewModel.toggleTaskStatus(taskId, context) },
                    onEdit = { task -> navController.navigate("task/${task.id}") },
                    onDelete = { taskId -> viewModel.deleteTask(taskId) }
                )
            }
        }
    }
}
