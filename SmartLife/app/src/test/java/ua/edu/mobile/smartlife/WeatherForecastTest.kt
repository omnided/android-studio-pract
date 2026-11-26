package ua.edu.mobile.smartlife

import org.junit.Assert.assertEquals
import org.junit.Test
import ua.edu.mobile.smartlife.data.model.DailyForecast
import ua.edu.mobile.smartlife.data.model.forecastDayLabel
import ua.edu.mobile.smartlife.data.remote.DailyWeatherDto
import ua.edu.mobile.smartlife.data.repository.toForecast
import java.time.LocalDate

class WeatherForecastTest {

    @Test
    fun toForecast_mapsParallelArraysToDays() {
        val dto = DailyWeatherDto(
            time = listOf("2026-11-04", "2026-11-05", "2026-11-06"),
            maxTemperatures = listOf(9.4, 11.0, 7.2),
            minTemperatures = listOf(2.1, 4.5, 0.3)
        )

        val forecast = dto.toForecast()

        assertEquals(3, forecast.size)
        assertEquals(DailyForecast(LocalDate.of(2026, 11, 5), 11.0, 4.5), forecast[1])
    }

    @Test
    fun toForecast_ignoresExtraValuesWhenArraysHaveDifferentSize() {
        val dto = DailyWeatherDto(
            time = listOf("2026-11-04", "2026-11-05"),
            maxTemperatures = listOf(9.4, 11.0, 7.2),
            minTemperatures = listOf(2.1, 4.5)
        )

        assertEquals(2, dto.toForecast().size)
    }

    @Test
    fun forecastDayLabel_todayTomorrowAndWeekday() {
        val today = LocalDate.of(2026, 11, 4) // середа

        assertEquals("Сьогодні", forecastDayLabel(today, today))
        assertEquals("Завтра", forecastDayLabel(today.plusDays(1), today))
        assertEquals("Субота", forecastDayLabel(today.plusDays(3), today))
    }
}
