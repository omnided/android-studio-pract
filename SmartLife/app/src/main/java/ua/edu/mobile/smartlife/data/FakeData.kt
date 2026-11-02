package ua.edu.mobile.smartlife.data

import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType
import java.time.LocalDateTime

/** Тестові дані, щоб екрани не були порожніми. */
object FakeData {
    const val USER_NAME = "Олена"
    const val USER_EMAIL = "olena@example.com"
    const val WATER_GOAL_LITERS = 2.0

    private val now: LocalDateTime = LocalDateTime.now()

    val records: List<HealthRecord> = listOf(
        HealthRecord(8, RecordType.WATER, 0.25, "Склянка води", now.minusMinutes(30)),
        HealthRecord(7, RecordType.PULSE, 72.0, "Після ранкової прогулянки", now.minusHours(1)),
        HealthRecord(6, RecordType.WATER, 0.25, "Склянка води", now.minusHours(2)),
        HealthRecord(5, RecordType.WEIGHT, 61.4, "Зранку натще", now.minusHours(3)),
        HealthRecord(4, RecordType.SLEEP, 7.5, "Прокинулась сама", now.minusHours(4)),
        HealthRecord(3, RecordType.PULSE, 64.0, "У спокої", now.minusDays(1)),
        HealthRecord(2, RecordType.SLEEP, 6.0, "Пізно ліг(ла)", now.minusDays(1).minusHours(4)),
        HealthRecord(1, RecordType.WEIGHT, 61.8, "", now.minusDays(2)),
    )
}
