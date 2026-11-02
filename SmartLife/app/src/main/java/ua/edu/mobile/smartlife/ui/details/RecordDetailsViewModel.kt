package ua.edu.mobile.smartlife.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.repository.RecordRepository
import ua.edu.mobile.smartlife.ui.navigation.RecordDetailsRoute

class RecordDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val recordRepository: RecordRepository,
) : ViewModel() {

    /** Аргумент маршруту ViewModel отримує сама, без участі екрана. */
    private val recordId: Long = savedStateHandle.toRoute<RecordDetailsRoute>().recordId

    val record: StateFlow<HealthRecord?> = recordRepository.observeRecord(recordId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun deleteRecord() {
        viewModelScope.launch { recordRepository.deleteRecord(recordId) }
    }
}
