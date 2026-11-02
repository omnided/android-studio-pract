package ua.edu.mobile.smartlife.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.edu.mobile.smartlife.data.FakeData
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType
import ua.edu.mobile.smartlife.data.model.formattedValue
import ua.edu.mobile.smartlife.ui.AppViewModelProvider
import ua.edu.mobile.smartlife.ui.components.AboutDialog
import ua.edu.mobile.smartlife.ui.components.RecordCard
import java.util.Locale

@Composable
fun HomeScreen(
    onRecordClick: (Long) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState, onAddWater = viewModel::addWaterGlass, onRecordClick = onRecordClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onAddWater: () -> Unit,
    onRecordClick: (Long) -> Unit,
) {
    // Відкрите меню не потрібно зберігати після повороту – досить remember
    var menuExpanded by remember { mutableStateOf(false) }
    // Відкритий діалог має пережити поворот – rememberSaveable
    var showAbout by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Life") },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Меню")
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Про застосунок") },
                                leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) },
                                onClick = { menuExpanded = false; showAbout = true },
                            )
                        }
                    }
                },
            )
        },
        // відступ знизу дає нижня панель навігації в SmartLifeApp
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("Вітаю, ${FakeData.USER_NAME}!", style = MaterialTheme.typography.headlineSmall)
            }
            item { WaterCard(waterToday = uiState.waterToday, onAddWater = onAddWater) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(RecordType.PULSE, uiState.lastPulse, Modifier.weight(1f))
                    SummaryCard(RecordType.SLEEP, uiState.lastSleep, Modifier.weight(1f))
                    SummaryCard(RecordType.WEIGHT, uiState.lastWeight, Modifier.weight(1f))
                }
            }
            item { Text("Останні записи", style = MaterialTheme.typography.titleMedium) }
            items(uiState.recentRecords, key = { it.id }) { record ->
                RecordCard(record, onClick = { onRecordClick(record.id) })
            }
        }
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@Composable
private fun WaterCard(waterToday: Double, onAddWater: () -> Unit) {
    val goal = FakeData.WATER_GOAL_LITERS
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Вода: ${String.format(Locale.US, "%.2f", waterToday)} з $goal л",
                style = MaterialTheme.typography.titleMedium,
            )
            LinearProgressIndicator(
                progress = { (waterToday / goal).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = onAddWater) { Text("Склянка води (0.25 л)") }
        }
    }
}

@Composable
private fun SummaryCard(type: RecordType, record: HealthRecord?, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(type.title, style = MaterialTheme.typography.labelLarge)
            Text(record?.formattedValue() ?: "—", style = MaterialTheme.typography.titleMedium)
        }
    }
}
