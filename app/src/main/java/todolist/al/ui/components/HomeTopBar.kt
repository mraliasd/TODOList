package todolist.al.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import todolist.al.R
import todolist.al.data.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onSortClick: (SortOption) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    var showSortDialog by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(SortOption.entries.first()) }

    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSortClick(selectedOption)
                        showSortDialog = false
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Text(
                    text = "Sort Tasks",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    SortOption.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOption = option
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = option == selectedOption,
                                onClick = { selectedOption = option }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Divider()
                    }
                }
            }
        )
    }

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
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable { showSortDialog = true }
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
