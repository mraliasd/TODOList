package todolist.al.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DatePickerButton(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current

    Button(onClick = {
        val today = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(date)
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        ).show()
    }) {
        Text("Pick Date: ${selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
    }
}