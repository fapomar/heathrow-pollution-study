package dev.fp.heathrowpollution;

import dev.fp.heathrowpollution.config.Config;
import dev.fp.heathrowpollution.model.LocationRole;
import dev.fp.heathrowpollution.model.airquality.AirQualityDataset;
import dev.fp.heathrowpollution.model.scenario.ScenarioRow;
import dev.fp.heathrowpollution.model.weather.WeatherDataset;
import dev.fp.heathrowpollution.service.DataService;
import dev.fp.heathrowpollution.service.DownloadService;
import dev.fp.heathrowpollution.service.Scenario1Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class HeathrowStudyRunner implements CommandLineRunner {

    @Autowired private Config config;
    @Autowired private DownloadService downloadService;
    @Autowired private DataService dataService;
    @Autowired private Scenario1Service scenario1Service;

    public static void main(String[] args) {
        SpringApplication.run(HeathrowStudyRunner.class, args);
    }

    @Override
    public void run(String[] args) {
        String start = config.getStartdate();
        String end = config.getEnddate();

        if (config.isDownloadData()) {
            config.getLocations().forEach(p -> {
                String path = String.format(p.getDatafolder() + p.getFileformat(), start, end);
                String url  = String.format(p.getUrl(), start, end);
                downloadService.downloadJson(url, path);
                System.out.println(url);
            });
        }

        AirQualityDataset batterseaAQM    = null;
        WeatherDataset    heathrowWeather = null;

        if (config.isLoadJsonFiles()) {
            for (var p : config.getLocations()) {
                String path = String.format(p.getDatafolder() + p.getFileformat(), start, end);
                if (p.getDataSource().equals("LondonAir")) {
                    AirQualityDataset ds = dataService.load(path, p.getName());
                    int total = ds.getDays().stream().mapToInt(d -> d.getMeasurements().size()).sum();
                    System.out.printf("Loaded %-35s %d days, %d observations%n", ds.getName(), ds.getDays().size(), total);
                    if (p.getRole() == LocationRole.AQM_BATTERSEA) batterseaAQM = ds;
                } else if (p.getDataSource().equals("OpenMeteo")) {
                    WeatherDataset ds = dataService.loadWeather(path, p.getName());
                    long nonNull = ds.getObservations().stream().filter(o -> o.getWindDirection180m() != null).count();
                    System.out.printf("Loaded %-35s %d observations (%d with data)%n", ds.getName(), ds.getObservations().size(), nonNull);
                    if (p.getRole() == LocationRole.WEATHER_HEATHROW) heathrowWeather = ds;
                }
            }
        }

        if (config.isGenerateScenario1() && batterseaAQM != null && heathrowWeather != null) {
            List<ScenarioRow> results = scenario1Service.run(
                    batterseaAQM, heathrowWeather,
                    LocalDate.parse(start), LocalDate.parse(end));
            writeCsv(results, "output/scenario1.csv");
        }
    }

    private void writeCsv(List<ScenarioRow> rows, String outputFile) {
        try {
            Path path = Path.of(outputFile);
            Files.createDirectories(path.getParent());
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                StringBuilder header = new StringBuilder("Date");
                for (int h = 0; h < 24; h++) header.append(",").append(h);
                pw.println(header);
                for (ScenarioRow row : rows) {
                    StringBuilder line = new StringBuilder(row.getDate().toString());
                    for (Double val : row.getPm25()) {
                        line.append(",");
                        if (val != null) line.append(val);
                    }
                    pw.println(line);
                }
            }
            System.out.printf("CSV written to %s%n", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
