package todolist.al.ui.components.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DateTimePickerCard(
    initialDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    var dateTime by remember { mutableStateOf(initialDateTime ?: LocalDateTime.now()) }

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val calendar = Calendar.getInstance()
                val now = LocalDateTime.now()

                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0)

                        val isToday = selectedDate.toLocalDate() == now.toLocalDate()

                        val initialHour = if (isToday) now.hour else 0
                        val initialMinute = if (isToday) now.minute else 0

                        TimePickerDialog(
                            context,
                            { _: TimePicker, hour: Int, minute: Int ->
                                val updated = selectedDate.withHour(hour).withMinute(minute)

                                if (updated.isBefore(now)) {
                                    return@TimePickerDialog
                                }

                                dateTime = updated
                                onDateTimeSelected(updated)
                            },
                            initialHour,
                            initialMinute,
                            true
                        ).show()
                    },
                    dateTime.year,
                    dateTime.monthValue - 1,
                    dateTime.dayOfMonth
                )

                datePickerDialog.datePicker.minDate = System.currentTimeMillis()

                datePickerDialog.show()
            }
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Due Date & Time",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
