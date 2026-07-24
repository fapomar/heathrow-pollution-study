package dev.alpomar.heathrowpollution.model.airquality.raw;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter @NoArgsConstructor
public class RawDataEntry {

    @JsonProperty("@MeasurementDateGMT")
    private String measurementDateGMT;

    private final Map<String, String> dataValues = new HashMap<>();

    // Captures @Data1, @Data2, ... — strips the leading @ so keys match @ColumnId values
    @JsonAnySetter
    public void setDataValue(String key, String value) {
        dataValues.put(key.replaceFirst("^@", ""), value);
    }
}
