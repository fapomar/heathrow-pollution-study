package dev.fp.heathrowpollution.model.airquality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirQualityObservation {
    private LocalDateTime timestamp;
    private double pm25;
    private double pm10;
}
