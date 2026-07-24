package dev.alpomar.heathrowpollution.model.airquality.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class RawColumnDefinition {

    @JsonProperty("@ColumnId")
    private String columnId;

    @JsonProperty("@ColumnName")
    private String columnName;
}
