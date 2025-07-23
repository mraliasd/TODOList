package todolist.al.ui.screens.home

import android.app.Application
import androidx.compose.foundation.layout.*
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
import todolist.al.data.model.SortOption
import java.time.LocalDate

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(context))

    val allTasks = viewModel.tasks
    var sortOption by remember { mutableStateOf(SortOption.TIME) }

    val today = LocalDate.now()
    val todayTasks by remember(allTasks, sortOption) {
        derivedStateOf {
            val filtered = allTasks.filter { it.dueDate?.toLocalDate() == today }
            when (sortOption) {
                SortOption.TITLE -> filtered.sortedBy { it.title.lowercase() }
                SortOption.TIME -> filtered.sortedBy { it.dueDate }
                SortOption.PRIORITY -> filtered.sortedBy { it.priority.ordinal }
            }
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                onSortClick = {
                    sortOption = it
                    viewModel.loadTasks(it)
                }
            )
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
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxSize()
        ) {
            if (todayTasks.isEmpty()) {
                EmptyTaskPlaceholder()
            } else {
                TaskList(
                    tasks = viewModel.tasks,
                    onToggle = { taskId -> viewModel.toggleTaskStatus(taskId, context) },
                    onEdit = { task -> viewModel.updateTask(task) },
                    onDelete = { id -> viewModel.deleteTask(id) }
                )

            }
        }
    }
}
