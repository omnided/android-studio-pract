package ua.edu.mobile.smartlife.data.repository

import kotlinx.coroutines.flow.Flow
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType

/** Контракт шару даних: що можна робити із записами, але не як. */
interface RecordRepository {
    fun observeRecords(): Flow<List<HealthRecord>>

    /** Практичне завдання 2.6 (архітектура): записи лише одного типу. */
    fun observeRecordsByType(type: RecordType): Flow<List<HealthRecord>>

    fun observeRecord(id: Long): Flow<HealthRecord?>
    suspend fun addRecord(record: HealthRecord): Long
    suspend fun updateRecord(record: HealthRecord)
    suspend fun deleteRecord(id: Long)
}
