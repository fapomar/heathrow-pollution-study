package dev.fp.heathrowpollution;

import dev.fp.heathrowpollution.config.Config;
import dev.fp.heathrowpollution.model.airquality.AirQualityDataset;
import dev.fp.heathrowpollution.model.scenario.ScenarioRow;
import dev.fp.heathrowpollution.model.weather.WeatherDataset;
import dev.fp.heathrowpollution.service.DataService;
import dev.fp.heathrowpollution.service.RunwayService;
import dev.fp.heathrowpollution.service.ScenarioService;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class HeathrowStudyRunner implements CommandLineRunner {

    @Autowired private Config config;
    @Autowired private DataService dataService;
    @Autowired private RunwayService runwayService;
    @Autowired private ScenarioService scenarioService;

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
                dataService.downloadJson(url, path);
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
                    if (p.getName().contains("Battersea")) batterseaAQM = ds;
                } else if (p.getDataSource().equals("OpenMeteo")) {
                    WeatherDataset ds = dataService.loadWeather(path, p.getName());
                    long nonNull = ds.getObservations().stream().filter(o -> o.getWindDirection180m() != null).count();
                    System.out.printf("Loaded %-35s %d observations (%d with data)%n", ds.getName(), ds.getObservations().size(), nonNull);
                    if (p.getName().contains("Heathrow")) heathrowWeather = ds;
                }
            }
        }

        if (config.isGenerateScenario1() && batterseaAQM != null && heathrowWeather != null) {
            List<ScenarioRow> results = scenarioService.runScenario1(
                    batterseaAQM, heathrowWeather,
                    LocalDate.parse(start), LocalDate.parse(end));
            scenarioService.printTable(results);
            scenarioService.writeCsv(results, "output/scenario1.csv");
        }
    }
}
