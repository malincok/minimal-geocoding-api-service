package com.malincok.geocoding;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.Set;

/**
 * POI parsed from OSM data
 */
@Getter
@AllArgsConstructor
public class Poi {
    private final long id;
    private final double lat;
    private final double lon;
    private final Date timestamp;
    private final Set<String> names;
    private int tagCount;

    /**
     * @return latitude and longitude formatted as a multi-line string
     */
    public String printCoordinates() {
        return "lat=" + lat + "\nlon=" + lon;
    }

    /**
     * @return first available name for this POI, or an empty string if none exist
     */
    public String printName() {
        if (names == null || names.isEmpty()) {
            return "";
        }
        return names.iterator().next();
    }
}
