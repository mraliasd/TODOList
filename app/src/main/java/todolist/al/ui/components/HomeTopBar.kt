package todolist.al.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import todolist.al.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    val colorScheme = MaterialTheme.colorScheme

    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TODO List",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sort",
                tint = colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        actions = {
            Image(
                painter = painterResource(id = R.drawable.app_profile),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(52.dp)
                    .padding(end = 16.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surface
        )
    )
}
