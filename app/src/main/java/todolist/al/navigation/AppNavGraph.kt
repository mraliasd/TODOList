package todolist.al.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import todolist.al.ui.screens.calendar.CalendarScreen
import todolist.al.ui.screens.intro.IntroScreen
import todolist.al.ui.screens.onboarding.OnboardingScreen
import todolist.al.ui.screens.home.HomeScreen
import todolist.al.ui.screens.auth.LoginPromptScreen
import todolist.al.ui.screens.task.TaskScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "intro"
    ) {
        composable("intro") {
            IntroScreen(navController)
        }

        composable("onboarding") {
            OnboardingScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("task") {
            TaskScreen(
                navController = navController,
                onTaskAdded = {
                        navController.navigate("home")
                }
            )
        }

        composable("task/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            TaskScreen(
                navController = navController,
                taskId = taskId,
                onTaskAdded = {
                    navController.navigate("home")
                }
            )
        }


        composable("login_prompt") {
            LoginPromptScreen(navController = navController)
        }



        composable("calendar") {
            CalendarScreen(navController)
        }
    }
}
