package ua.edu.mobile.smartlife.data.repository

import ua.edu.mobile.smartlife.data.model.DailyForecast
import ua.edu.mobile.smartlife.data.model.Weather
import ua.edu.mobile.smartlife.data.model.weatherCodeToText
import ua.edu.mobile.smartlife.data.remote.DailyWeatherDto
import ua.edu.mobile.smartlife.data.remote.WeatherApi
import java.time.LocalDate

interface WeatherRepository {
    /** Кидає виняток (IOException, HttpException), якщо запит не вдався. */
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Weather
}

class NetworkWeatherRepository(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Weather {
        val response = api.getCurrentWeather(latitude, longitude)
        // Перетворюємо DTO на модель застосунку
        return Weather(
            temperature = response.current.temperature,
            feelsLike = response.current.feelsLike,
            humidity = response.current.humidity,
            windSpeedKmh = response.current.windSpeed,
            description = weatherCodeToText(response.current.weatherCode),
            weatherCode = response.current.weatherCode,
            latitude = response.latitude,
            longitude = response.longitude,
            forecast = response.daily?.toForecast().orEmpty()
        )
    }
}

/** Перетворює паралельні масиви daily на список днів; minOf захищає від масивів різної довжини. */
fun DailyWeatherDto.toForecast(): List<DailyForecast> {
    val days = minOf(time.size, maxTemperatures.size, minTemperatures.size)
    return (0 until days).map { i ->
        DailyForecast(
            date = LocalDate.parse(time[i]), // формат ISO: "2026-09-23"
            maxTemperature = maxTemperatures[i],
            minTemperature = minTemperatures[i]
        )
    }
}
