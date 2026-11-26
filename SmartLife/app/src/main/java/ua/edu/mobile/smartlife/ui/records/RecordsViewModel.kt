package ua.edu.mobile.smartlife.ui.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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

    // Практичне завдання 3.6: текст пошуку за нотаткою (живе у ViewModel, тож переживає поворот)
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // combine реагує на зміну будь-якого з двох станів, а flatMapLatest перемикається на
    // відповідний запит до бази: усі записи, лише тип (observeByType) або пошук (searchByNote)
    @OptIn(ExperimentalCoroutinesApi::class)
    val records: StateFlow<List<HealthRecord>> = combine(_selectedType, _query) { type, query ->
        type to query.trim()
    }
        .flatMapLatest { (type, query) ->
            when {
                query.isEmpty() && type == null -> recordRepository.observeRecords()
                query.isEmpty() -> recordRepository.observeRecordsByType(type!!)
                // Пошук + тип одночасно: результат пошуку додатково звужуємо за типом
                else -> recordRepository.searchRecords(query).map { list ->
                    if (type == null) list else list.filter { it.type == type }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectType(type: RecordType?) {
        _selectedType.value = type
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun addRecord(type: RecordType, value: Double, note: String) {
        viewModelScope.launch {
            recordRepository.addRecord(HealthRecord(type = type, value = value, note = note))
        }
    }
}
