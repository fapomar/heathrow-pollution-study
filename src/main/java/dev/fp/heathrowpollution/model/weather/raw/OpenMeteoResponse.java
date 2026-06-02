package dev.fp.heathrowpollution.model.weather.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoResponse {

    private OpenMeteoHourly hourly;

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenMeteoHourly {
        private List<String> time;
        private List<Integer> wind_direction_180m;
        private List<Double> wind_speed_180m;
    }
}
