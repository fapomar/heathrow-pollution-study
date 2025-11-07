package dev.fp.heathrowpollution.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DataService {

    public DataService() {
    }

    public boolean download(String url, String file){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String content = response.body();

            Path filePath = Path.of(file);
            Files.writeString(filePath, content);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void load(String folder){
    }
}
