package id.ac.pnm.bayarin_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.ac.pnm.bayarin_app.ui.auth.login.LoginScreen
import id.ac.pnm.bayarin_app.ui.auth.register.RegisterScreen
import id.ac.pnm.bayarin_app.ui.profile.ProfileScreen
import id.ac.pnm.bayarin_app.ui.newnotes.NewNotesScreen

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

        composable(Routes.PROFILE) {
            ProfileScreen()
        }

        composable(Routes.NEW_NOTES) {
            NewNotesScreen()
        }
    }
}