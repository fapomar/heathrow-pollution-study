package dev.fp.heathrowpollution.service;

import dev.fp.heathrowpollution.model.airquality.AirQualityDataset;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
public class DataService {

    public DataService() {
    }

    public boolean downloadJson(String url, String outputFile){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String originalContent = response.body();

            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

            String prettyContent = writer.writeValueAsString(mapper.readTree(originalContent));

            Path filePath = Path.of(outputFile);
            Files.writeString(filePath, prettyContent);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public AirQualityDataset load(String inputFile) {
        // ToDo: Load JSON file and unmarshall into AirQualityDataset
        System.out.println(inputFile);

        return null;
    }

}
