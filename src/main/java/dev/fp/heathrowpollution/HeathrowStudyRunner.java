package dev.fp.heathrowpollution;

import dev.fp.heathrowpollution.config.Config;
import dev.fp.heathrowpollution.model.airquality.AirQualityDataset;
import dev.fp.heathrowpollution.model.weather.WeatherDataset;
import dev.fp.heathrowpollution.service.DataService;
import dev.fp.heathrowpollution.service.RunwayService;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class HeathrowStudyRunner implements CommandLineRunner {

    @Autowired
    private Config config;

    @Autowired
    private DataService dataService;

    @Autowired
    private RunwayService runwayService;

    public static void main(String[] args) {
        SpringApplication.run(HeathrowStudyRunner.class, args);
    }

    @Override
    public void run(String[] args) throws Exception {
        String start = config.getStartdate();
        String end = config.getEnddate();

        if (config.isDownloadData()){
                config.getLocations().forEach(p -> {
                    String urlTemplate = p.getUrl();
                    String cannonincalPath = String.format(p.getDatafolder() + p.getFileformat(), start, end);
                    String url = String.format(urlTemplate, start, end);
                    dataService.downloadJson(url,cannonincalPath);
                    System.out.println(url);
                }
            );
        }

        System.out.println("\n--- Runway schedule sample ---");
        var samples = new LocalDateTime[]{
            LocalDateTime.of(2026, 5, 20,  3, 0),   // night
            LocalDateTime.of(2026, 5, 20,  6, 30),  // both runways
            LocalDateTime.of(2026, 5, 20, 10, 0),   // morning
            LocalDateTime.of(2026, 5, 20, 17, 0),   // afternoon
            LocalDateTime.of(2026, 5, 25,  6, 0),   // week boundary - morning
            LocalDateTime.of(2026, 5, 25,  2, 0),   // Monday before 06:00 -> previous week night
        };
        for (LocalDateTime dt : samples) {
            String result = runwayService.getRunway(dt)
                    .map(r -> r.toString())
                    .orElse("no schedule");
            System.out.printf("  %s  ->  %s%n", dt, result);
        }
        System.out.println();

        if (config.isLoadJsonFiles()){
            config.getLocations().forEach(p -> {
                    if (p.getDataSource().equals("LondonAir")) {
                        String cannonincalPath = String.format(p.getDatafolder() + p.getFileformat(), start, end);
                        AirQualityDataset dataSet = dataService.load(cannonincalPath, p.getName());
                        int totalObs = dataSet.getDays().stream().mapToInt(d -> d.getMeasurements().size()).sum();
                        System.out.printf("Loaded %-35s %d days, %d observations%n", dataSet.getName(), dataSet.getDays().size(), totalObs);
                    } else if (p.getDataSource().equals("OpenMeteo")) {
                        String cannonincalPath = String.format(p.getDatafolder() + p.getFileformat(), start, end);
                        WeatherDataset dataSet = dataService.loadWeather(cannonincalPath, p.getName());
                        long nonNull = dataSet.getObservations().stream().filter(o -> o.getWindDirection180m() != null).count();
                        System.out.printf("Loaded %-35s %d observations (%d with data)%n", dataSet.getName(), dataSet.getObservations().size(), nonNull);
                    }
                }
            );
        }
    }
}