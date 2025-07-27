package todolist.al.ui.components.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import todolist.al.data.model.CustomRecurringMode
import todolist.al.data.model.RecurringType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun RecurringOptions(
    recurringType: RecurringType,
    onTypeChange: (RecurringType) -> Unit,
    recurringMode: CustomRecurringMode?,
    onModeChange: (CustomRecurringMode?) -> Unit,
    recurringInterval: Int?,
    onIntervalChange: (Int?) -> Unit,
    selectedDays: List<DayOfWeek>,
    onDaysChange: (List<DayOfWeek>) -> Unit,
    selectedTimes: List<LocalTime>,
    onTimesChange: (List<LocalTime>) -> Unit,
    recurringEndDate: LocalDate?,
    onEndDateChange: (LocalDate?) -> Unit
) {
    val context = LocalContext.current
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
                    modifier = Modifier.clickable { onTypeChange(type) }
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

            Text("Custom Mode", style = MaterialTheme.typography.bodyMedium)

            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                CustomRecurringMode.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onModeChange(mode) }
                    ) {
                        RadioButton(
                            selected = recurringMode == mode,
                            onClick = { onModeChange(mode) }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (recurringMode) {
                CustomRecurringMode.INTERVAL -> {
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
                }

                CustomRecurringMode.WEEKDAYS -> {
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
                    RepeatTimePicker(selectedTimes, onTimesChange)
                }

                CustomRecurringMode.SINGLE_DAY -> {
                    RepeatTimePicker(selectedTimes, onTimesChange)
                }

                null -> {}
            }
        }

        if (
            recurringType in listOf(RecurringType.DAILY, RecurringType.WEEKLY, RecurringType.MONTHLY) ||
            (recurringType == RecurringType.CUSTOM && recurringMode in listOf(CustomRecurringMode.INTERVAL, CustomRecurringMode.WEEKDAYS))
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            val calendar = Calendar.getInstance()
            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        onEndDateChange(LocalDate.of(year, month + 1, day))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.minDate = System.currentTimeMillis()
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.weight(1f)
                ) {
                    val displayText = recurringEndDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        ?: "Select End Date"
                    Text(displayText)
                }
                if (recurringEndDate != null) {
                    IconButton(onClick = { onEndDateChange(null) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear End Date")
                    }
                }
            }
        }

    }
}

@Composable
private fun RepeatTimePicker(
    selectedTimes: List<LocalTime>,
    onTimesChange: (List<LocalTime>) -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingTime by remember { mutableStateOf(LocalTime.now()) }

    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val newTime = LocalTime.of(hour, minute)
                if (newTime !in selectedTimes) {
                    onTimesChange(selectedTimes + newTime)
                }
                showTimePicker = false
            },
            pendingTime.hour,
            pendingTime.minute,
            true
        ).show()
    }

    Text("Repeat at Times:", style = MaterialTheme.typography.bodyMedium)
    Column(modifier = Modifier.fillMaxWidth()) {
        selectedTimes.sorted().forEachIndexed { index, time ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
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
            pendingTime = LocalTime.now().withSecond(0).withNano(0)
            showTimePicker = true
        }) {
            Text("+ Add Time")
        }
    }
}
