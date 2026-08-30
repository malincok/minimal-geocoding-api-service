package com.malincok.geocoding;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Query service for searching POIs in OSM data
 */
public class GeocodingService {
    private static final String COORDS_REGEX = "lat=(-?[0-9]+\\.[0-9]+)&lon=(-?[0-9]+\\.[0-9]+)";
    private final List<Poi> pois;

    public GeocodingService(String osmFilePath) {
        URL resource = GeocodingService.class.getResource(osmFilePath);
        this.pois = OsmReader.readData(resource);
    }

    /**
     * Finds POI by name
     * Compares timestamp, tag count, latitude, longitude, and ID in this exact order
     * to ensure deterministic results for identical names
     * @param name POI name to search for
     * @return matching POI or null if not found
     */
    public Poi findByName(String name) {
        return this.pois.stream()
                .filter(poi -> poi.getNames() != null && poi.getNames().contains(name))
                .max(Comparator.comparing(Poi::getTimestamp)
                        .thenComparing(Poi::getTagCount)
                        .thenComparing(Poi::getLat)
                        .thenComparing(Poi::getLon)
                        .thenComparing(Poi::getId))
                .orElse(null);
    }

    /**
     * Finds a POI by coordinates
     * Compares timestamp, tag count, and ID in this exact order
     * to ensure deterministic results for identical coordinates
     * @param input formatted string containing latitude and longitude
     * @return matching POI or null if not found
     */
    public Poi findByCoordinates(String input) {
        Matcher matcher = Pattern.compile(COORDS_REGEX).matcher(input);
        if (!matcher.matches()) {
            throw new InvalidCoordinatesException("Invalid coordinate format: " + input
                    + ". Expected format: lat=<latitude>&lon=<longitude>");
        }

        double lat = Double.parseDouble(matcher.group(1));
        double lon = Double.parseDouble(matcher.group(2));

        if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
            throw new InvalidCoordinatesException("Input coordinates out of range: " + input);
        }

        return this.pois.stream()
                .filter(poi -> Double.compare(poi.getLat(), lat) == 0 && Double.compare(poi.getLon(), lon) == 0)
                .max(Comparator.comparing(Poi::getTimestamp)
                        .thenComparing(Poi::getTagCount)
                        .thenComparing(Poi::getId))
                .orElse(null);
    }
}
