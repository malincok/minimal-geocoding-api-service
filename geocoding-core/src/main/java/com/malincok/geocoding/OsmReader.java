package com.malincok.geocoding;

import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Utility class for reading and parsing OSM data file
 */
public class OsmReader {

    /**
     * Reads OSM file and extracts a list of POIs
     * @param resource the URL of the OSM file to process
     * @return list of POIs
     */
    public static List<Poi> readData(URL resource) {
        if (resource == null) {
            throw new IllegalStateException("OSM file not found");
        }
        try {
            PoiSink poiSink = new PoiSink();
            File file = new File(resource.toURI());
            XmlReader xmlReader = new XmlReader(file, true, CompressionMethod.None);
            xmlReader.setSink(poiSink);
            xmlReader.run();
            return poiSink.getPois();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to read OSM file", e);
        }
    }
}
