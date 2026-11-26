package ua.edu.mobile.smartlife.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * DTO (Data Transfer Object) — класи, що точно повторюють структуру JSON-відповіді сервера.
 * Приклад відповіді Open-Meteo:
 * {
 *   "latitude": 50.4375, "longitude": 30.5, "timezone": "Europe/Kyiv",
 *   "current": { "time": "2026-09-23T16:30", "temperature_2m": 13.7,
 *                "relative_humidity_2m": 63, "apparent_temperature": 11.5,
 *                "wind_speed_10m": 9.0, "weather_code": 3 },
 *   "daily": { "time": ["2026-09-23", "2026-09-24", "2026-09-25"],
 *              "temperature_2m_max": [15.2, 17.0, 14.1],
 *              "temperature_2m_min": [8.4, 9.9, 7.6] }
 * }
 */

@Serializable
data class WeatherResponseDto(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeatherDto,
    // Значення за замовчуванням: якщо блоку daily у відповіді немає, розбір не впаде
    val daily: DailyWeatherDto? = null
)

@Serializable
data class CurrentWeatherDto(
    val time: String,
    // @SerialName — ім'я поля в JSON, якщо воно відрізняється від імені властивості
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("relative_humidity_2m") val humidity: Int,
    @SerialName("apparent_temperature") val feelsLike: Double,
    @SerialName("wind_speed_10m") val windSpeed: Double,
    @SerialName("weather_code") val weatherCode: Int
)

/** Денний прогноз: три паралельні масиви однакової довжини — дати, максимуми й мінімуми. */
@Serializable
data class DailyWeatherDto(
    val time: List<String>,
    @SerialName("temperature_2m_max") val maxTemperatures: List<Double>,
    @SerialName("temperature_2m_min") val minTemperatures: List<Double>
)
