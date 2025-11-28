package dev.fp.heathrowpollution.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {
    private String name;
    private String dataSource;
    private String sitecode;
    private String longitude;
    private String latitude;
    private String url;
    private String datafolder;
    private String fileformat;
}
