package dev.alpomar.heathrowpollution.service;

import dev.alpomar.heathrowpollution.model.airquality.AirQualityDataset;
import dev.alpomar.heathrowpollution.model.runway.RunwayInfo;
import dev.alpomar.heathrowpollution.model.scenario.ScenarioRow;
import dev.alpomar.heathrowpollution.model.weather.WeatherDataset;
import dev.alpomar.heathrowpollution.model.weather.WeatherObservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Scenario 4: Westerly operations, southern runway (27L), Battersea PM2.5,
 * filtered to hours where local wind at Battersea is southerly (blowing north).
 */
@Service
public class Scenario4Service {

    private static final double EASTERLY_SPEED_THRESHOLD_KNOTS = 5.0;

    @Autowired private RunwayService runwayService;

    public List<ScenarioRow> run(AirQualityDataset battersea,
                                 WeatherDataset heathrowWeather,
                                 WeatherDataset batterseaWeather,
                                 LocalDate startDate,
                                 LocalDate endDate) {
        Map<LocalDateTime, Double> pm25            = buildPm25Map(battersea);
        Map<LocalDateTime, WeatherObservation> lhr = buildWeatherMap(heathrowWeather);
        Map<LocalDateTime, WeatherObservation> waa = buildWeatherMap(batterseaWeather);

        List<ScenarioRow> results = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Double[] row = new Double[24];
            for (int hour = 0; hour < 24; hour++) {
                LocalDateTime dt = date.atTime(hour, 0);

                WeatherObservation lhrObs = lhr.get(dt);
                if (lhrObs == null || !isWesterly(lhrObs)) continue;

                Optional<RunwayInfo> runway = runwayService.getRunway(dt);
                if (runway.isEmpty() || !"27L".equals(runway.get().getPrimary())) continue;

                WeatherObservation waaObs = waa.get(dt);
                if (waaObs == null || !isSoutherly(waaObs)) continue;

                Double reading = pm25.get(dt);
                if (reading != null && !reading.isNaN()) row[hour] = reading;
            }
            results.add(new ScenarioRow(date, row));
        }
        return results;
    }

    private boolean isWesterly(WeatherObservation obs) {
        Integer direction = obs.getWindDirection180m();
        Double speed = obs.getWindSpeed180m();
        if (direction == null || speed == null) return false;
        boolean isEasterly = speed >= EASTERLY_SPEED_THRESHOLD_KNOTS
                && direction >= 1 && direction <= 180;
        return !isEasterly;
    }

    private boolean isSoutherly(WeatherObservation obs) {
        Integer direction = obs.getWindDirection180m();
        if (direction == null) return false;
        return direction >= 135 && direction <= 225;
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
