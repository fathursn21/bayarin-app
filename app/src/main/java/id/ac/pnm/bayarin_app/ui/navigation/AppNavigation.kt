package id.ac.pnm.bayarin_app.ui.navigation

import android.R.attr.type
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.ac.pnm.bayarin_app.ui.auth.login.LoginScreen
import id.ac.pnm.bayarin_app.ui.auth.register.RegisterScreen
import id.ac.pnm.bayarin_app.ui.daftarTeman.DaftarTemanScreen
import id.ac.pnm.bayarin_app.ui.detailTransaksiGroup.DetailTransaksiGroupScreen
import id.ac.pnm.bayarin_app.ui.profile.ProfileScreen
import id.ac.pnm.bayarin_app.ui.newnotes.NewNotesScreen
import id.ac.pnm.bayarin_app.ui.group.GroupScreen
import id.ac.pnm.bayarin_app.ui.home.HomeScreen
import id.ac.pnm.bayarin_app.ui.reminder.ReminderScreen
import id.ac.pnm.bayarin_app.ui.tambahGroup.TambahGroupScreen
import id.ac.pnm.bayarin_app.ui.tambahTeman.TambahTemanScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
//         startDestination = Routes.LOGIN
//         startDestination = Routes.PROFILE
        startDestination = Routes.LOGIN
//        startDestination = Routes.HOME
    ) {
         composable(Routes.LOGIN) {
             LoginScreen(navController)
         }

        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }

        composable(Routes.HOME) {
            HomeScreen(navController)
        }

        composable(Routes.GROUP) {
            GroupScreen(navController)
        }

        composable(Routes.REMINDER) {
            ReminderScreen(navController)

        }

        composable(Routes.NEW_NOTES) {
            NewNotesScreen(navController)
        }

        composable(Routes.TAMBAH_GROUP) {
            TambahGroupScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Routes.DAFTAR_TEMAN) {
            DaftarTemanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.TAMBAH_TEMAN){
            TambahTemanScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDaftarTeman = {
                     navController.navigate(Routes.DAFTAR_TEMAN)
                }
            )
        }

        composable(
            route = "${Routes.DETAIL_TRANSAKSI_GROUP}/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""

            DetailTransaksiGroupScreen(
                groupId = groupId,
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}