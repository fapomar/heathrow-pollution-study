package dev.fp.heathrowpollution;

import dev.fp.heathrowpollution.config.Config;
import dev.fp.heathrowpollution.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class StudyRunner implements CommandLineRunner {

    private Config config;

    @Autowired
    private DataService dataService;

    public StudyRunner(Config config) {
        this.config = config;
    }

    @Override
    public void run(String... args) {
        String start = config.getStartdate();
        String end = config.getEnddate();

        if (config.isDownloadData()){
                config.getLocations().forEach(p -> {
                    String urlTemplate = p.getUrl();
                    String cannonincalPath = String.format(p.getDatafolder()+p.getFileformat(), start);
                    String url = String.format(urlTemplate, start, end);
                    dataService.download(url,cannonincalPath);
                    System.out.println(url);
                }
            );
        }

    }
}