package dev.fp.heathrowpollution.config;

import dev.fp.heathrowpollution.config.Location;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix="app")
@Getter
@Setter
public class Config {
    private List<Location> locations;
    private String startdate;
    private String enddate;
    private boolean downloadData;
    private boolean loadJsonFiles;
    private boolean generateScenario1;
    private String scenario1Output;
}
