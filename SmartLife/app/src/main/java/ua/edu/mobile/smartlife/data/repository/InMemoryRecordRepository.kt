package ua.edu.mobile.smartlife.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType

/** Реалізація репозиторію, що зберігає записи в пам'яті (до підключення Room). */
class InMemoryRecordRepository(initialRecords: List<HealthRecord> = emptyList()) : RecordRepository {

    private val records = MutableStateFlow(initialRecords)

    override fun observeRecords(): Flow<List<HealthRecord>> = records

    override fun observeRecordsByType(type: RecordType): Flow<List<HealthRecord>> =
        records.map { list -> list.filter { it.type == type } }

    override fun observeRecord(id: Long): Flow<HealthRecord?> =
        records.map { list -> list.find { it.id == id } }

    override suspend fun addRecord(record: HealthRecord): Long {
        var newId = 0L
        records.update { current ->
            newId = (current.maxOfOrNull { it.id } ?: 0L) + 1
            listOf(record.copy(id = newId)) + current // новий список, а не зміна старого
        }
        return newId
    }

    override suspend fun updateRecord(record: HealthRecord) {
        records.update { list -> list.map { if (it.id == record.id) record else it } }
    }

    override suspend fun deleteRecord(id: Long) {
        records.update { list -> list.filterNot { it.id == id } }
    }
}
