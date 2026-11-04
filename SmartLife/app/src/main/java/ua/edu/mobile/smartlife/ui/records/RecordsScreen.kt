package ua.edu.mobile.smartlife.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType
import ua.edu.mobile.smartlife.ui.AppViewModelProvider
import ua.edu.mobile.smartlife.ui.components.RecordCard
import ua.edu.mobile.smartlife.ui.components.icon

@Composable
fun RecordsScreen(
    onRecordClick: (Long) -> Unit,
    viewModel: RecordsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val records by viewModel.records.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    RecordsContent(
        records = records,
        selectedType = selectedType,
        onSelectType = viewModel::selectType,
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onAddRecord = viewModel::addRecord,
        onRecordClick = onRecordClick
    )
}

/**
 * Екран списку без власних даних (stateless): список і обраний фільтр приходять параметрами,
 * а про дії користувача екран повідомляє через onSelectType і onAddRecord (state hoisting).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsContent(
    records: List<HealthRecord>,
    selectedType: RecordType?,
    onSelectType: (RecordType?) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    onAddRecord: (RecordType, Double, String) -> Unit,
    onRecordClick: (Long) -> Unit
) {
    // Стан діалогу — суто UI-стан, тому лишається локальним
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Журнал показників (${records.size})") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Додати запис")
            }
        }
    ) { innerPadding ->
        // Порожня база і немає ні фільтра, ні пошуку — показуємо заглушку зі stage-07
        if (records.isEmpty() && selectedType == null && query.isBlank()) {
            EmptyRecords(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Практичне завдання 3.6: пошук за нотаткою (запит LIKE у DAO)
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Пошук за нотаткою") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Очистити пошук")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )
            TypeFilterRow(selectedType = selectedType, onSelectType = onSelectType)
            if (records.isEmpty()) {
                Text(
                    text = "Нічого не знайдено",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records, key = { it.id }) { record ->
                    RecordCard(record = record, onClick = { onRecordClick(record.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddRecordDialog(
            onDismiss = { showAddDialog = false },
            onSave = { type, value, note ->
                onAddRecord(type, value, note)
                showAddDialog = false
            },
            // Якщо обрано фільтр — новий запис одразу цього типу
            initialType = selectedType ?: RecordType.WATER
        )
    }
}

/** Чипи-фільтри над списком: «Усі» + по одному на кожен тип запису. */
@Composable
private fun TypeFilterRow(
    selectedType: RecordType?,
    onSelectType: (RecordType?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedType == null,
                onClick = { onSelectType(null) },
                label = { Text("Усі") }
            )
        }
        items(RecordType.entries) { type ->
            FilterChip(
                selected = type == selectedType,
                // Повторне натискання на обраний чип знімає фільтр
                onClick = { onSelectType(if (type == selectedType) null else type) },
                label = { Text(type.title) },
                leadingIcon = {
                    Icon(type.icon, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize))
                }
            )
        }
    }
}

/** Заглушка, коли в базі ще немає жодного запису. */
@Composable
private fun EmptyRecords(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.AutoMirrored.Filled.EventNote,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Записів поки немає",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            "Натисніть «+», щоб додати перший показник",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
