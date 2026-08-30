package com.malincok.geocoding;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Entry point for starting HTTP server providing geocoding API endpoints
 */
public class GeocodingApi {
    private static final String OSM_FILE_PATH = "/map-brno-city-centre.osm";

    public static void main(String[] args) throws IOException {
        GeocodingService service = new GeocodingService(OSM_FILE_PATH);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8081), 0);
        server.createContext("/geocode", httpExchange -> geocode(httpExchange, service));
        server.start();
    }

    private static void geocode(HttpExchange httpExchange, GeocodingService service) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        if (query == null) {
            sendResponse(httpExchange, 400, "Missing query parameters");
            return;
        }

        try {
            Poi poi;
            if (query.startsWith("name=")) {
                poi = service.findByName(query.substring("name=".length()));
            } else if (query.startsWith("lat=")) {
                poi = service.findByCoordinates(query);
            } else {
                sendResponse(httpExchange, 400, "Invalid query parameter");
                return;
            }

            if (poi == null) {
                sendResponse(httpExchange, 404, "POI not found");
                return;
            }

            String response = query.startsWith("name=") ? poi.printCoordinates() : poi.printName();
            sendResponse(httpExchange, 200, response);

        } catch (InvalidCoordinatesException e) {
            sendResponse(httpExchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(httpExchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private static void sendResponse(HttpExchange httpExchange, int statusCode, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(statusCode, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();
    }
}