package ua.edu.mobile.smartlife.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType
import ua.edu.mobile.smartlife.data.repository.RecordRepository
import java.time.LocalDate

/** Стан головного екрана: підсумки дня і останні записи. */
data class HomeUiState(
    val waterToday: Double = 0.0,
    val lastPulse: HealthRecord? = null,
    val lastSleep: HealthRecord? = null,
    val lastWeight: HealthRecord? = null,
    val recentRecords: List<HealthRecord> = emptyList(),
)

class HomeViewModel(private val recordRepository: RecordRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = recordRepository.observeRecords()
        .map { records ->
            val today = LocalDate.now()
            HomeUiState(
                waterToday = records
                    .filter { it.type == RecordType.WATER && it.dateTime.toLocalDate() == today }
                    .sumOf { it.value },
                lastPulse = records.lastOfType(RecordType.PULSE),
                lastSleep = records.lastOfType(RecordType.SLEEP),
                lastWeight = records.lastOfType(RecordType.WEIGHT),
                recentRecords = records.sortedByDescending { it.dateTime }.take(5),
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun addWaterGlass() {
        viewModelScope.launch {
            recordRepository.addRecord(
                HealthRecord(id = 0, type = RecordType.WATER, value = 0.25, note = "Склянка води")
            )
        }
    }

    private fun List<HealthRecord>.lastOfType(type: RecordType): HealthRecord? =
        filter { it.type == type }.maxByOrNull { it.dateTime }
}
