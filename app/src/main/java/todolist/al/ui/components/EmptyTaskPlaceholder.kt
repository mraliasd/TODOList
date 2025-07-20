package todolist.al.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import todolist.al.R


@Composable
fun EmptyTaskPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_task),
            contentDescription = "Empty Task",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 24.dp)
        )
        Text(
            text = "No Tasks Available",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}