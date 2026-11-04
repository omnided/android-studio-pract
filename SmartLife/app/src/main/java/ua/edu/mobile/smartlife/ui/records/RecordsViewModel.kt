package ua.edu.mobile.smartlife.ui.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType
import ua.edu.mobile.smartlife.data.repository.RecordRepository

/** ViewModel екрана «Журнал»: отримує дані з репозиторію, а не зберігає їх сама. */
class RecordsViewModel(
    private val recordRepository: RecordRepository
) : ViewModel() {

    // Обраний фільтр: null — показуємо всі записи. Змінювати його може лише ViewModel
    private val _selectedType = MutableStateFlow<RecordType?>(null)
    val selectedType: StateFlow<RecordType?> = _selectedType.asStateFlow()

    // flatMapLatest при зміні фільтра відписується від старого потоку і підписується на новий
    @OptIn(ExperimentalCoroutinesApi::class)
    val records: StateFlow<List<HealthRecord>> = _selectedType
        .flatMapLatest { type ->
            if (type == null) recordRepository.observeRecords()
            else recordRepository.observeRecordsByType(type)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectType(type: RecordType?) {
        _selectedType.value = type
    }

    fun addRecord(type: RecordType, value: Double, note: String) {
        viewModelScope.launch {
            recordRepository.addRecord(HealthRecord(type = type, value = value, note = note))
        }
    }
}
