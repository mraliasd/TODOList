package todolist.al.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NotInterested
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDateTime

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val dueDate: LocalDateTime? = null,
    val category: TaskCategory? = null,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val reminder: LocalDateTime? = null,
    val parentId: Int? = null
)

enum class TaskPriority(val color: Color) {
    HIGH(Color(0xFFE57373)),
    LOW(Color(0xFF81C784)),
    NORMAL(Color(0xFFFFF176));

}


enum class TaskCategory(
    val label: String,
    val color: Color,
    val icon: ImageVector
) {
    INVALID("INVALID", Color.White, Icons.Outlined.NotInterested),
    HOME("HOME", Color(0xFFB2FF59), Icons.Outlined.Home),
    WORK("WORK", Color(0xFFFF8A65), Icons.Outlined.Work),
    PERSONAL("PERSONAL", Color(0xFF4DD0E1), Icons.Outlined.Person),
    STUDY("STUDY", Color(0xFF69F0AE), Icons.Outlined.School),
    HEALTH("HEALTH", Color(0xFF82B1FF), Icons.Outlined.Healing),
    SHOPPING("SHOPPING", Color(0xFFE91E63), Icons.Outlined.ShoppingCart)
}
