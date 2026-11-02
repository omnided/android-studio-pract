package ua.edu.mobile.smartlife.data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Тип показника здоров'я: назва для інтерфейсу та одиниця виміру. */
enum class RecordType(val title: String, val unit: String) {
    WATER("Вода", "л"),
    PULSE("Пульс", "уд/хв"),
    SLEEP("Сон", "год"),
    WEIGHT("Вага", "кг"),
}

/** Один запис показника. */
data class HealthRecord(
    val id: Long,
    val type: RecordType,
    val value: Double,
    val note: String = "",
    val dateTime: LocalDateTime = LocalDateTime.now(),
)

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

/** Значення з одиницею виміру: «72 уд/хв», «0.25 л», «7.5 год». */
fun HealthRecord.formattedValue(): String {
    val number = when (type) {
        RecordType.PULSE -> value.toInt().toString()
        RecordType.WATER -> String.format(Locale.US, "%.2f", value)
        RecordType.SLEEP, RecordType.WEIGHT -> String.format(Locale.US, "%.1f", value)
    }
    return "$number ${type.unit}"
}

fun HealthRecord.formattedDate(): String = dateTime.format(dateFormatter)

/** Текст запису для кнопки «Поділитися». */
fun HealthRecord.shareText(): String = buildString {
    append("${type.title}: ${formattedValue()} (${formattedDate()})")
    if (note.isNotBlank()) append(". Нотатка: $note")
}
