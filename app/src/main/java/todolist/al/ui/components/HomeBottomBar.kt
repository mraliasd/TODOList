package todolist.al.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeBottomBar(
    onHomeClick: () -> Unit,
    onCalendarClick: () -> Unit,
    selectedScreen: String = "home"
) {

    var homeSelected: Boolean = false
    var calendarSelected: Boolean = false
    if (selectedScreen == "home") {
        homeSelected = true
    } else if (selectedScreen == "calendar") {
        calendarSelected = true
    }

    NavigationBar(
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = homeSelected,
            onClick = onHomeClick
        )

        Spacer(modifier = Modifier.weight(1f))

        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Calendar") },
            label = { Text("Calendar") },
            selected = calendarSelected,
            onClick = onCalendarClick
        )
    }
}