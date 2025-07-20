package todolist.al

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import todolist.al.navigation.AppNavGraph
import todolist.al.ui.theme.TODOListTheme

@Composable
fun TODOList() {
    TODOListTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavGraph()
        }
    }
}
