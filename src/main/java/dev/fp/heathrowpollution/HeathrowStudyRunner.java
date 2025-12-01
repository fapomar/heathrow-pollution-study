package dev.fp.heathrowpollution;

import dev.fp.heathrowpollution.config.Config;
import dev.fp.heathrowpollution.model.airquality.AirQualityDataset;
import dev.fp.heathrowpollution.service.DataService;
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

        if (config.isLoadJsonFiles()){
            config.getLocations().forEach(p -> {
                    if (p.getDataSource().equals("LondonAir")){
                        String cannonincalPath = String.format(p.getDatafolder() + p.getFileformat(), start, end);
                        AirQualityDataset dataSet = dataService.load(cannonincalPath);
                    }
                }
            );
        }
    }
}