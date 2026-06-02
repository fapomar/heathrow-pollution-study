package dev.fp.heathrowpollution.model.scenario;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ScenarioRow {
    private final LocalDate date;
    private final Double[] pm25;  // 24 elements (hours 0-23), null = condition not met or no reading
}
