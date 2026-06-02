package dev.fp.heathrowpollution.service;

import dev.fp.heathrowpollution.model.airquality.AirQualityDaily;
import dev.fp.heathrowpollution.model.airquality.AirQualityDataset;
import dev.fp.heathrowpollution.model.airquality.AirQualityObservation;
import dev.fp.heathrowpollution.model.airquality.raw.LondonAirResponse;
import dev.fp.heathrowpollution.model.airquality.raw.RawColumnDefinition;
import dev.fp.heathrowpollution.model.airquality.raw.RawDataEntry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
public class DataService {

    private static final DateTimeFormatter AQM_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ObjectMapper mapper = new ObjectMapper();

    public boolean downloadJson(String url, String outputFile) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            String prettyContent = writer.writeValueAsString(mapper.readTree(response.body()));
            Files.writeString(Path.of(outputFile), prettyContent);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public AirQualityDataset load(String inputFile, String name) {
        try {
            LondonAirResponse response = mapper.readValue(Path.of(inputFile).toFile(), LondonAirResponse.class);
            return toDataset(response, name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private AirQualityDataset toDataset(LondonAirResponse response, String name) {
        // Map each species to its column id, e.g. "PM2.5" -> "Data5"
        Map<String, String> speciesColumn = new HashMap<>();
        for (RawColumnDefinition col : response.getColumns()) {
            String colName = col.getColumnName().toLowerCase();
            if (colName.contains("pm2.5"))                speciesColumn.put("PM2.5", col.getColumnId());
            else if (colName.contains("pm10"))            speciesColumn.put("PM10",  col.getColumnId());
            else if (colName.contains("oxides of nitrogen")) speciesColumn.put("NOX", col.getColumnId());
        }

        String pm25Col = speciesColumn.get("PM2.5");
        String pm10Col = speciesColumn.get("PM10");
        String noxCol  = speciesColumn.get("NOX");

        Map<LocalDate, List<AirQualityObservation>> byDay = new LinkedHashMap<>();
        for (RawDataEntry entry : response.getDataEntries()) {
            LocalDateTime ts = LocalDateTime.parse(entry.getMeasurementDateGMT(), AQM_DATE_FORMAT);
            double pm25 = parseDouble(pm25Col != null ? entry.getDataValues().get(pm25Col) : null);
            double pm10 = parseDouble(pm10Col != null ? entry.getDataValues().get(pm10Col) : null);
            double nox  = parseDouble(noxCol  != null ? entry.getDataValues().get(noxCol)  : null);

            byDay.computeIfAbsent(ts.toLocalDate(), d -> new ArrayList<>())
                 .add(new AirQualityObservation(ts, pm25, pm10, nox));
        }

        List<AirQualityDaily> days = byDay.entrySet().stream()
                .map(e -> new AirQualityDaily(e.getKey(), e.getValue()))
                .toList();

        return new AirQualityDataset(days, name);
    }

    private double parseDouble(String value) {
        if (value == null || value.isBlank()) return Double.NaN;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
