package com.afyaquik.reports;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@ConfigurationProperties(prefix = "app.dhis2")
@Getter @Setter
public class Dhis2Settings {
    private boolean mappingApproved;
    private String mappingVersion;
    private String dataSet;
    private String orgUnit;
    private String attributeOptionCombo;
    private List<Mapping> mappings = new ArrayList<>();

    @Getter @Setter
    public static class Mapping {
        private String metric;
        private String dataElement;
        private String categoryOptionCombo;
    }
}