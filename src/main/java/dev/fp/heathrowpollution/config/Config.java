package dev.fp.heathrowpollution.config;

import dev.fp.heathrowpollution.model.Location;
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

    public List<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(List<Location> locations){
        this.locations = locations;
    }

}
