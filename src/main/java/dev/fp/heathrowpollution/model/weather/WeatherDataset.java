package dev.fp.heathrowpollution.model.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class WeatherDataset {
    private String name;
    private List<WeatherObservation> observations;
}
