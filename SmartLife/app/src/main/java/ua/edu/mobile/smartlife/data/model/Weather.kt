package ua.edu.mobile.smartlife.data.model

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/** Модель погоди, яку використовує UI (не залежить від формату JSON). */
data class Weather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeedKmh: Double,
    val description: String,
    val weatherCode: Int,
    val latitude: Double,
    val longitude: Double,
    val forecast: List<DailyForecast> = emptyList()
)

/** Прогноз на один день (практичне завдання 3.6). */
data class DailyForecast(
    val date: LocalDate,
    val maxTemperature: Double,
    val minTemperature: Double
)

/** Підпис дня для рядка прогнозу: «Сьогодні», «Завтра» або назва дня тижня («П'ятниця»). */
fun forecastDayLabel(date: LocalDate, today: LocalDate = LocalDate.now()): String = when (date) {
    today -> "Сьогодні"
    today.plusDays(1) -> "Завтра"
    else -> date.dayOfWeek
        .getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("uk"))
        .replaceFirstChar { it.uppercase() }
}

/** Розшифровка коду погоди WMO (https://open-meteo.com/en/docs, розділ "WMO Weather interpretation codes"). */
fun weatherCodeToText(code: Int): String = when (code) {
    0 -> "Ясно"
    1, 2 -> "Мінлива хмарність"
    3 -> "Хмарно"
    45, 48 -> "Туман"
    51, 53, 55, 56, 57 -> "Мряка"
    61, 63, 65, 66, 67 -> "Дощ"
    71, 73, 75, 77 -> "Сніг"
    80, 81, 82 -> "Злива"
    85, 86 -> "Снігопад"
    95, 96, 99 -> "Гроза"
    else -> "Невідомо"
}
