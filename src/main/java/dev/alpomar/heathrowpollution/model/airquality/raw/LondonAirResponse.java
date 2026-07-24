package dev.alpomar.heathrowpollution.model.airquality.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LondonAirResponse {

    @JsonProperty("AirQualityData")
    private AirQualityDataWrapper airQualityData;

    public List<RawColumnDefinition> getColumns() {
        return airQualityData.columns.column;
    }

    public List<RawDataEntry> getDataEntries() {
        return airQualityData.rawAQData.data;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class AirQualityDataWrapper {
        @JsonProperty("Columns")
        private ColumnsWrapper columns;
        @JsonProperty("RawAQData")
        private RawAQDataWrapper rawAQData;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ColumnsWrapper {
        @JsonProperty("Column")
        private List<RawColumnDefinition> column;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class RawAQDataWrapper {
        @JsonProperty("Data")
        private List<RawDataEntry> data;
    }
}
