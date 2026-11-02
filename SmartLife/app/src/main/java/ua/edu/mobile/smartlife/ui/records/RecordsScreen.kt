package ua.edu.mobile.smartlife.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType
import ua.edu.mobile.smartlife.ui.AppViewModelProvider
import ua.edu.mobile.smartlife.ui.components.RecordCard
import ua.edu.mobile.smartlife.ui.components.icon

/** Отримує ViewModel і передає далі лише дані та лямбди. */
@Composable
fun RecordsScreen(
    onRecordClick: (Long) -> Unit,
    viewModel: RecordsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val records by viewModel.records.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    RecordsContent(
        records = records,
        selectedType = selectedType,
        onSelectType = viewModel::selectType,
        onAdd = viewModel::addRecord,
        onRecordClick = onRecordClick,
    )
}

/** Нічого не знає про ViewModel, тому її легко показати в @Preview і протестувати. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsContent(
    records: List<HealthRecord>,
    selectedType: RecordType?,
    onSelectType: (RecordType?) -> Unit,
    onAdd: (RecordType, Double, String) -> Unit,
    onRecordClick: (Long) -> Unit,
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Журнал показників") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Додати запис")
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TypeFilterRow(selectedType = selectedType, onSelectType = onSelectType)
            if (records.isEmpty()) {
                Text(
                    text = if (selectedType == null) "Записів поки немає"
                    else "Записів типу «${selectedType.title}» немає",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(records, key = { it.id }) { record ->
                    RecordCard(record, onClick = { onRecordClick(record.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddRecordDialog(
            initialType = selectedType ?: RecordType.WATER,
            onDismiss = { showAddDialog = false },
            onConfirm = { type, value, note ->
                onAdd(type, value, note)
                showAddDialog = false
            },
        )
    }
}

/** Практичне завдання 2.6 (стан): чипи-фільтри над списком. */
@Composable
private fun TypeFilterRow(selectedType: RecordType?, onSelectType: (RecordType?) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedType == null,
                onClick = { onSelectType(null) },
                label = { Text("Усі") },
            )
        }
        items(RecordType.entries) { type ->
            FilterChip(
                selected = selectedType == type,
                // повторне натискання на обраний чип знімає фільтр
                onClick = { onSelectType(if (selectedType == type) null else type) },
                label = { Text(type.title) },
                leadingIcon = {
                    Icon(type.icon, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize))
                },
            )
        }
    }
}
