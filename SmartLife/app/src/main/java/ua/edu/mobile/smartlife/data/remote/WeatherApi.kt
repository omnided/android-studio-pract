package ua.edu.mobile.smartlife.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Опис REST API сервісу Open-Meteo (https://open-meteo.com) — безкоштовний, без API-ключа.
 * Retrofit сам створить реалізацію цього інтерфейсу.
 *
 * Підсумковий запит виглядає так:
 * GET https://api.open-meteo.com/v1/forecast?latitude=50.45&longitude=30.52&current=...
 *     &daily=temperature_2m_max,temperature_2m_min&forecast_days=3&timezone=auto
 */
interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") fields: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m,weather_code",
        // Практичне завдання 3.6: денний прогноз (максимум і мінімум температури) на 3 дні
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min",
        @Query("forecast_days") forecastDays: Int = 3,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponseDto
}
