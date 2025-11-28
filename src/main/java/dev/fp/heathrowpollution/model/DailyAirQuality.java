package dev.fp.heathrowpollution.model;

import dev.fp.heathrowpollution.model.AirQualityRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyAirQuality {
    private LocalDate date;
    private List<AirQualityRecord> measurements;
}
