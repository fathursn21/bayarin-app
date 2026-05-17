package id.ac.pnm.bayarin_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.ac.pnm.bayarin_app.ui.auth.login.LoginScreen
import id.ac.pnm.bayarin_app.ui.auth.register.RegisterScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
         composable(Routes.LOGIN) {
             LoginScreen(navController)
         }

        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }
    }
}