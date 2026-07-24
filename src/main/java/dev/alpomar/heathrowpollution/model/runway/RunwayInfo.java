package dev.alpomar.heathrowpollution.model.runway;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RunwayInfo {

    public enum Period { MORNING, AFTERNOON, BOTH, NIGHT }

    // Which physical runway a code maps to
    public enum PhysicalRunway { NORTHERN, SOUTHERN }

    private final Period period;
    private final String primary;    // e.g. "27L" — active landing runway
    private final String secondary;  // NIGHT: alternate approach direction; BOTH: second active runway; else null

    public PhysicalRunway primaryRunway() {
        return toPhysical(primary);
    }

    public static PhysicalRunway toPhysical(String code) {
        return switch (code) {
            case "27R", "09L" -> PhysicalRunway.NORTHERN;
            case "27L", "09R" -> PhysicalRunway.SOUTHERN;
            default -> throw new IllegalArgumentException("Unknown runway code: " + code);
        };
    }

    @Override
    public String toString() {
        return switch (period) {
            case MORNING   -> "Morning   | landing runway: " + primary + " (" + primaryRunway() + ")";
            case AFTERNOON -> "Afternoon | landing runway: " + primary + " (" + primaryRunway() + ")";
            case BOTH      -> "Both      | " + primary + " (" + toPhysical(primary) + ") + " + secondary + " (" + toPhysical(secondary) + ")";
            case NIGHT     -> "Night     | primary: " + primary + " (" + primaryRunway() + ")  secondary: " + secondary;
        };
    }
}
