package dev.fp.heathrowpollution.service;

import dev.fp.heathrowpollution.model.airquality.AirQualityDataset;
import dev.fp.heathrowpollution.model.airquality.AirQualityObservation;
import dev.fp.heathrowpollution.model.runway.RunwayInfo;
import dev.fp.heathrowpollution.model.scenario.ScenarioRow;
import dev.fp.heathrowpollution.model.weather.WeatherDataset;
import dev.fp.heathrowpollution.model.weather.WeatherObservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ScenarioService {

    // Easterly operations: wind speed >= 5 knots FROM the east (direction 1-180 degrees)
    // Below 5 knots: westerly preference applies regardless of direction
    private static final double EASTERLY_SPEED_THRESHOLD_KNOTS = 5.0;

    @Autowired
    private RunwayService runwayService;

    /**
     * Scenario 1: Westerly operations, northern runway (27R), Battersea PM2.5.
     * For each hour in the date range, records Battersea PM2.5 only when:
     *   1. Wind at Heathrow 180m is westerly (speed < 5 kts, OR direction outside 1-180 degrees)
     *   2. The scheduled landing runway is 27R (northern runway)
     * Wind dissipation towards the AQM is not controlled for in this scenario.
     */
    public List<ScenarioRow> runScenario1(AirQualityDataset battersea,
                                          WeatherDataset heathrowWeather,
                                          LocalDate startDate,
                                          LocalDate endDate) {
        Map<LocalDateTime, Double> pm25 = buildPm25Map(battersea);
        Map<LocalDateTime, WeatherObservation> weather = buildWeatherMap(heathrowWeather);

        List<ScenarioRow> results = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Double[] row = new Double[24];
            for (int hour = 0; hour < 24; hour++) {
                LocalDateTime dt = date.atTime(hour, 0);

                WeatherObservation obs = weather.get(dt);
                if (obs == null || !isWesterly(obs)) continue;

                Optional<RunwayInfo> runway = runwayService.getRunway(dt);
                if (runway.isEmpty() || !"27R".equals(runway.get().getPrimary())) continue;

                Double reading = pm25.get(dt);
                if (reading != null && !reading.isNaN()) row[hour] = reading;
            }
            results.add(new ScenarioRow(date, row));
        }
        return results;
    }

    public void printTable(List<ScenarioRow> rows) {
        System.out.printf("%n--- Scenario 1: Westerly ops / runway 27R (northern) / Battersea PM2.5 ---%n");
        System.out.printf("%-12s", "Date");
        for (int h = 0; h < 24; h++) System.out.printf(" |%5d", h);
        System.out.println();
        System.out.println("-".repeat(12 + 24 * 7));
        for (ScenarioRow row : rows) {
            System.out.printf("%-12s", row.getDate());
            for (Double val : row.getPm25()) {
                if (val != null) System.out.printf(" |%5.1f", val);
                else             System.out.printf(" |     ");
            }
            System.out.println();
        }
    }

    // Easterly: speed >= 5 knots AND direction 1-180 degrees (headwind on runway 09)
    private boolean isWesterly(WeatherObservation obs) {
        Integer direction = obs.getWindDirection180m();
        Double speed = obs.getWindSpeed180m();
        if (direction == null || speed == null) return false;
        boolean isEasterly = speed >= EASTERLY_SPEED_THRESHOLD_KNOTS
                && direction >= 1 && direction <= 180;
        return !isEasterly;
    }

    private Map<LocalDateTime, Double> buildPm25Map(AirQualityDataset dataset) {
        Map<LocalDateTime, Double> map = new HashMap<>();
        dataset.getDays().forEach(day ->
            day.getMeasurements().forEach(obs ->
                map.put(obs.getTimestamp(), obs.getPm25())));
        return map;
    }

    private Map<LocalDateTime, WeatherObservation> buildWeatherMap(WeatherDataset dataset) {
        Map<LocalDateTime, WeatherObservation> map = new HashMap<>();
        dataset.getObservations().forEach(obs -> map.put(obs.getTimestamp(), obs));
        return map;
    }
}
