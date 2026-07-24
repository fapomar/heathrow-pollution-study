package dev.alpomar.heathrowpollution.config;

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
    private boolean generateScenario2;
    private String scenario2Output;
    private boolean generateScenario3;
    private String scenario3Output;
    private boolean generateScenario4;
    private String scenario4Output;
}
