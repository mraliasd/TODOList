package todolist.al.ui.screens.calendar

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import todolist.al.viewmodel.TaskViewModel
import todolist.al.viewmodel.TaskViewModelFactory
import todolist.al.ui.components.DatePickerButton
import todolist.al.ui.components.task.TaskList
import todolist.al.ui.components.EmptyTaskPlaceholder
import todolist.al.ui.components.HomeBottomBar
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavHostController
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: TaskViewModel = viewModel(factory = TaskViewModelFactory(context))

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val allTasks = viewModel.tasks.sortedBy { it.dueDate }

    val filteredTasks by remember(searchQuery, selectedDate, allTasks) {
        derivedStateOf {
            allTasks.filter { task ->
                val matchDate = selectedDate?.let { task.dueDate?.toLocalDate() == it } ?: true
                val matchQuery = searchQuery.isBlank() || task.title.contains(searchQuery, ignoreCase = true)
                matchDate && matchQuery
            }
        }
    }

    Scaffold(
        topBar = {
            if (isSearching) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Search tasks...") },
                    singleLine = true
                )
            } else {
                CenterAlignedTopAppBar(
                    title = { Text("Calendar") },
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        },
        bottomBar = { HomeBottomBar(
            onHomeClick = { navController.navigate("home") },
            onCalendarClick = { /* Already here */ },
            selectedScreen = "calendar"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            DatePickerButton(
                selectedDate = selectedDate ?: LocalDate.now(),
                onDateSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredTasks.isEmpty()) {
                EmptyTaskPlaceholder()
            } else {
                TaskList(
                    tasks = filteredTasks,
                    onToggle = { taskId -> viewModel.toggleTaskStatus(taskId, context) },
                    onEdit = { task -> navController.navigate("task/${task.id}") },
                    onDelete = { taskId -> viewModel.deleteTask(taskId) }
                )
            }
        }
    }
}
