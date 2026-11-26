package ua.edu.mobile.smartlife

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ua.edu.mobile.smartlife.data.FakeData
import ua.edu.mobile.smartlife.data.model.RecordType
import ua.edu.mobile.smartlife.data.repository.InMemoryRecordRepository
import ua.edu.mobile.smartlife.ui.records.RecordsViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class RecordsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun selectType_filtersRecords_andNullShowsAll() = runTest(dispatcher) {
        val viewModel = RecordsViewModel(InMemoryRecordRepository(FakeData.records))
        backgroundScope.launch { viewModel.records.collect {} } // підписка для WhileSubscribed

        viewModel.selectType(RecordType.WATER)
        assertEquals(RecordType.WATER, viewModel.selectedType.value)
        assertEquals(FakeData.records.count { it.type == RecordType.WATER }, viewModel.records.value.size)
        assertTrue(viewModel.records.value.all { it.type == RecordType.WATER })

        viewModel.selectType(null)
        assertEquals(FakeData.records.size, viewModel.records.value.size)
    }
}
