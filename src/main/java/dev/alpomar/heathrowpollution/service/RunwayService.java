package dev.alpomar.heathrowpollution.service;

import dev.alpomar.heathrowpollution.model.runway.RunwayInfo;
import dev.alpomar.heathrowpollution.model.runway.RunwayInfo.Period;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Properties;

@Service
public class RunwayService {

    private static final LocalTime DAY_START      = LocalTime.of(6, 0);
    private static final LocalTime BOTH_END       = LocalTime.of(7, 0);   // 06:00-07:00 both runways
    private static final LocalTime AFTERNOON_START = LocalTime.of(15, 0);
    private static final LocalTime NIGHT_START    = LocalTime.of(23, 0);  // approximate last departure

    private final Properties schedule = new Properties();

    @PostConstruct
    public void init() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/runway-alternation.properties")) {
            schedule.load(is);
        }
    }

    /**
     * Returns the scheduled runway(s) for the given date and time.
     * Empty if the date falls outside the 2026 schedule.
     *
     * Note: the day-time result applies only during westerly operations (~70% of the year).
     * During easterly operations the southern runway (27L) is always used; the caller
     * should cross-reference with wind direction data to determine which applies.
     */
    public Optional<RunwayInfo> getRunway(LocalDateTime dateTime) {
        LocalDate weekKey = resolveWeekKey(dateTime);
        String key = "runway." + weekKey;

        LocalTime time = dateTime.toLocalTime();

        if (isNight(time)) {
            String primary   = schedule.getProperty(key + ".night.primary");
            String secondary = schedule.getProperty(key + ".night.secondary");
            if (primary == null) return Optional.empty();
            return Optional.of(new RunwayInfo(Period.NIGHT, primary, secondary));
        }

        if (time.isBefore(BOTH_END)) {
            // 06:00-07:00: both runways active simultaneously
            String morning   = schedule.getProperty(key + ".day.morning");
            String afternoon = schedule.getProperty(key + ".day.afternoon");
            if (morning == null) return Optional.empty();
            return Optional.of(new RunwayInfo(Period.BOTH, morning, afternoon));
        }

        if (time.isBefore(AFTERNOON_START)) {
            String primary = schedule.getProperty(key + ".day.morning");
            if (primary == null) return Optional.empty();
            return Optional.of(new RunwayInfo(Period.MORNING, primary, null));
        }

        String primary = schedule.getProperty(key + ".day.afternoon");
        if (primary == null) return Optional.empty();
        return Optional.of(new RunwayInfo(Period.AFTERNOON, primary, null));
    }

    private boolean isNight(LocalTime time) {
        return time.isBefore(DAY_START) || !time.isBefore(NIGHT_START);
    }

    private LocalDate resolveWeekKey(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        // Monday before 06:00 continues the previous week's night pattern
        if (dateTime.getDayOfWeek() == DayOfWeek.MONDAY && dateTime.toLocalTime().isBefore(DAY_START)) {
            return date.minusWeeks(1);
        }
        return date.with(DayOfWeek.MONDAY);
    }
}
