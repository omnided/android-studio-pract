package ua.edu.mobile.smartlife.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// Типобезпечні маршрути: бібліотека навігації сама перетворює їх на адреси і назад
@Serializable object LoginRoute
@Serializable object HomeRoute
@Serializable object RecordsRoute
@Serializable object ProfileRoute
@Serializable data class RecordDetailsRoute(val recordId: Long)

/** Пункт нижньої панелі навігації. */
data class TopLevelRoute<T : Any>(val label: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("Головна", HomeRoute, Icons.Filled.Home),
    TopLevelRoute("Журнал", RecordsRoute, Icons.AutoMirrored.Filled.List),
    TopLevelRoute("Профіль", ProfileRoute, Icons.Filled.Person),
)
