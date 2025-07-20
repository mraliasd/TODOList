package todolist.al.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier


@Composable
fun AddTaskFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF8875FF),
        contentColor = Color.Black,
        modifier = Modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Task"
        )
    }
}