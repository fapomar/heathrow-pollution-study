package dev.fp.heathrowpollution.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirQualityRecord {
    private LocalDateTime timestamp;
    private double pm25;
    private double pm10;
}
