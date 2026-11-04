package ua.edu.mobile.smartlife

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ua.edu.mobile.smartlife.data.FakeData
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType
import ua.edu.mobile.smartlife.data.repository.InMemoryRecordRepository

class InMemoryRecordRepositoryTest {

    @Test
    fun observeRecordsByType_returnsOnlyRecordsOfThatType() = runTest {
        val repository = InMemoryRecordRepository(FakeData.records)

        val water = repository.observeRecordsByType(RecordType.WATER).first()

        assertEquals(FakeData.records.count { it.type == RecordType.WATER }, water.size)
        assertTrue(water.all { it.type == RecordType.WATER })
    }

    @Test
    fun observeRecordsByType_updatesAfterAddingRecord() = runTest {
        val repository = InMemoryRecordRepository(FakeData.records)
        val before = repository.observeRecordsByType(RecordType.PULSE).first().size

        repository.addRecord(HealthRecord(type = RecordType.PULSE, value = 80.0))

        assertEquals(before + 1, repository.observeRecordsByType(RecordType.PULSE).first().size)
    }

    @Test
    fun addRecord_assignsNextId_andDeleteRemovesIt() = runTest {
        val repository = InMemoryRecordRepository(FakeData.records)

        val id = repository.addRecord(HealthRecord(type = RecordType.WATER, value = 0.25))
        assertEquals(FakeData.records.maxOf { it.id } + 1, id)

        repository.deleteRecord(id)
        assertNull(repository.observeRecord(id).first())
    }
}
