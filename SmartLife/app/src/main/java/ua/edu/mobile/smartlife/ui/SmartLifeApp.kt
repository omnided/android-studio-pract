package ua.edu.mobile.smartlife.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ua.edu.mobile.smartlife.ui.auth.LoginScreen
import ua.edu.mobile.smartlife.ui.details.RecordDetailsScreen
import ua.edu.mobile.smartlife.ui.home.HomeScreen
import ua.edu.mobile.smartlife.ui.navigation.HomeRoute
import ua.edu.mobile.smartlife.ui.navigation.LoginRoute
import ua.edu.mobile.smartlife.ui.navigation.ProfileRoute
import ua.edu.mobile.smartlife.ui.navigation.RecordDetailsRoute
import ua.edu.mobile.smartlife.ui.navigation.RecordsRoute
import ua.edu.mobile.smartlife.ui.navigation.topLevelRoutes
import ua.edu.mobile.smartlife.ui.profile.ProfileScreen
import ua.edu.mobile.smartlife.ui.records.RecordsScreen

/** Корінь UI: граф навігації і нижня панель. Екрани отримують лише лямбди, а не navController. */
@Composable
fun SmartLifeApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    // Нижня панель видна лише на трьох головних вкладках
    val showBottomBar = topLevelRoutes.any { item ->
        currentDestination?.hasRoute(item.route::class) == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelRoutes.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    // одна копія кожної вкладки в стеку, стан вкладок зберігається
                                    popUpTo(HomeRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
        // системні відступи обробляють самі екрани (TopAppBar) і NavigationBar
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoginRoute,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<LoginRoute> {
                LoginScreen(onLoggedIn = {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true } // «Назад» не веде на вхід
                    }
                })
            }
            composable<HomeRoute> {
                HomeScreen(onRecordClick = { id -> navController.navigate(RecordDetailsRoute(id)) })
            }
            composable<RecordsRoute> {
                RecordsScreen(onRecordClick = { id -> navController.navigate(RecordDetailsRoute(id)) })
            }
            composable<ProfileRoute> {
                ProfileScreen(onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                })
            }
            composable<RecordDetailsRoute> {
                RecordDetailsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
