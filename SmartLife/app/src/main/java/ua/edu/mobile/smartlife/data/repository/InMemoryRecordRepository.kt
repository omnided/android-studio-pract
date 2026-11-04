package ua.edu.mobile.smartlife.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType

/**
 * Реалізація репозиторію в оперативній пам'яті.
 * Дані зникають після закриття застосунку — у розділі 6 замінимо її на Room.
 */
class InMemoryRecordRepository(
    initialRecords: List<HealthRecord> = emptyList()
) : RecordRepository {

    private val records = MutableStateFlow(initialRecords.sortedByDescending { it.timestamp })

    override fun observeRecords(): Flow<List<HealthRecord>> = records

    // Фільтруємо на рівні репозиторію: у Room це стане запитом WHERE type = :type
    override fun observeRecordsByType(type: RecordType): Flow<List<HealthRecord>> =
        records.map { list -> list.filter { it.type == type } }

    override fun searchRecords(query: String): Flow<List<HealthRecord>> =
        records.map { list -> list.filter { it.note.contains(query, ignoreCase = true) } }

    override fun observeRecord(id: Long): Flow<HealthRecord?> =
        records.map { list -> list.find { it.id == id } }

    override suspend fun addRecord(record: HealthRecord): Long {
        var newId = 0L
        records.update { current ->
            newId = (current.maxOfOrNull { it.id } ?: 0) + 1
            (current + record.copy(id = newId)).sortedByDescending { it.timestamp }
        }
        return newId
    }

    override suspend fun updateRecord(record: HealthRecord) {
        records.update { current -> current.map { if (it.id == record.id) record else it } }
    }

    override suspend fun deleteRecord(id: Long) {
        records.update { current -> current.filterNot { it.id == id } }
    }
}
