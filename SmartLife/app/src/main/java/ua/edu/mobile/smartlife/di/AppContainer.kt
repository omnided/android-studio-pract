package ua.edu.mobile.smartlife.di

import android.content.Context
import ua.edu.mobile.smartlife.data.FakeData
import ua.edu.mobile.smartlife.data.repository.InMemoryRecordRepository
import ua.edu.mobile.smartlife.data.repository.RecordRepository

/** Контейнер залежностей: одна реалізація кожного репозиторію на весь застосунок. */
class AppContainer(private val context: Context) {
    val recordRepository: RecordRepository by lazy {
        InMemoryRecordRepository(FakeData.records)
    }
}
