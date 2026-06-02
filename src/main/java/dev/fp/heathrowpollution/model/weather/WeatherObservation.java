package dev.fp.heathrowpollution.model.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class WeatherObservation {
    private LocalDateTime timestamp;
    private Integer windDirection180m;
    private Double windSpeed180m;  // knots
}
