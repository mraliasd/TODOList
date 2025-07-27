package todolist.al.ui.components.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import todolist.al.data.model.RecurringType
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun RecurringOptions(
    recurringType: RecurringType,
    onTypeChange: (RecurringType) -> Unit,
    recurringInterval: Int?,
    onIntervalChange: (Int?) -> Unit,
    selectedDays: List<DayOfWeek>,
    onDaysChange: (List<DayOfWeek>) -> Unit,
    selectedTimes: List<LocalTime>,
    onTimesChange: (List<LocalTime>) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Repeat Task", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))


        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            RecurringType.entries.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onTypeChange(type) }
                ) {
                    RadioButton(
                        selected = recurringType == type,
                        onClick = { onTypeChange(type) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }


        if (recurringType == RecurringType.CUSTOM) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = recurringInterval?.toString() ?: "",
                onValueChange = { onIntervalChange(it.toIntOrNull()) },
                label = { Text("Interval in days") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Repeat on Days:", style = MaterialTheme.typography.bodyMedium)
            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                DayOfWeek.values().forEach { day ->
                    val selected = selectedDays.contains(day)
                    FilterChip(
                        selected = selected,
                        onClick = {
                            onDaysChange(
                                if (selected) selectedDays - day else selectedDays + day
                            )
                        },
                        label = { Text(day.name.take(3)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            Text("Repeat at Times:", style = MaterialTheme.typography.bodyMedium)
            Column {
                selectedTimes.forEachIndexed { index, time ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = time.format(timeFormatter),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            onTimesChange(selectedTimes.toMutableList().also { it.removeAt(index) })
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Time")
                        }
                    }
                }

                TextButton(onClick = {
                    val now = LocalTime.now()
                    onTimesChange(selectedTimes + now)
                }) {
                    Text("+ Add Time")
                }
            }
        }
    }
}
