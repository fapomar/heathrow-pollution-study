package dev.fp.heathrowpollution.model.airquality;

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
public class AirQualityDaily {
    private LocalDate date;
    private List<AirQualityObservation> measurements;
}
