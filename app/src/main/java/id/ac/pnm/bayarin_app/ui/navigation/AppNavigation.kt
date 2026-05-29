package id.ac.pnm.bayarin_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.ac.pnm.bayarin_app.ui.auth.login.LoginScreen
import id.ac.pnm.bayarin_app.ui.auth.register.RegisterScreen
import id.ac.pnm.bayarin_app.ui.group.GroupScreen
import id.ac.pnm.bayarin_app.ui.home.DashboardScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
//        startDestination = Routes.LOGIN
        startDestination = Routes.GROUP
    ) {
         composable(Routes.LOGIN) {
             LoginScreen(navController)
         }

        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }

        composable(Routes.HOME) {
            DashboardScreen()
        }
        composable(Routes.GROUP) {
            GroupScreen()
        }
    }
}