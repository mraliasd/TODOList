package todolist.al.ui.components.task

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import todolist.al.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Today",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = { /* TODO: Profile click */ }) {
                Surface(
                    shape = CircleShape,
                    tonalElevation = 4.dp
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_profile),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                    )
                }
            }
        }
    )
}