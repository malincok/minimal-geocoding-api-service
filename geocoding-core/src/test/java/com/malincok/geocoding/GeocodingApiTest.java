package com.malincok.geocoding;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeocodingApiTest {

    private static final String BASE_URL = "http://127.0.0.1:8081/geocode";
    private static final HttpClient client = HttpClient.newHttpClient();

    @BeforeClass
    public static void startServer() throws IOException {
        GeocodingApi.main(new String[]{});
    }

    @Test
    public void missingQueryTest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(400, response.statusCode());
        Assert.assertEquals("Missing query parameters", response.body());
    }

    @Test
    public void invalidQueryTest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?batman"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(400, response.statusCode());
        Assert.assertEquals("Invalid query parameter", response.body());
    }

    @Test
    public void notFoundTest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?name=batman"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(404, response.statusCode());
        Assert.assertEquals("POI not found", response.body());

        HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?lat=0.0&lon=0.0"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(404, response.statusCode());
        Assert.assertEquals("POI not found", response.body());
    }

    @Test
    public void geocodeByNameTest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?name=Brno"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(200, response.statusCode());
        Assert.assertTrue(response.body().matches("lat=(-?[0-9]+\\.[0-9]+)\\nlon=(-?[0-9]+\\.[0-9]+)"));
    }

    @Test
    public void geocodeByCoordinatesTest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?lat=49.1922443&lon=16.6113382"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(200, response.statusCode());
        Assert.assertEquals("Brno", response.body());
    }

    @Test
    public void invalidCoordFormatTest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?lat=49.1922443;lon=16.6113382"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(400, response.statusCode());
        Assert.assertTrue(response.body().startsWith("Invalid coordinate format:"));

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?lat=49.1922443lon=16.6113382"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(400, response.statusCode());
        Assert.assertTrue(response.body().startsWith("Invalid coordinate format:"));

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?lat=49.192"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(400, response.statusCode());
        Assert.assertTrue(response.body().startsWith("Invalid coordinate format:"));

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?lat=batman"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(400, response.statusCode());
        Assert.assertTrue(response.body().startsWith("Invalid coordinate format:"));
    }

    @Test
    public void coordsOutOfRangeTest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?lat=120.1922443&lon=-16.6113382"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(400, response.statusCode());
        Assert.assertTrue(response.body().startsWith("Input coordinates out of range:"));

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?lat=-49.1922443&lon=200.6113382"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(400, response.statusCode());
        Assert.assertTrue(response.body().startsWith("Input coordinates out of range:"));
    }
}