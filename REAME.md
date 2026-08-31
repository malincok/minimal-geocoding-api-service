# Geocoding API

Simple geocoding API built with **TypeScript/Node.js** and **Java**.

## Architecture

The application consists of two services:

~~~text
Client
  |
  v
TypeScript API :8080
  |
  v
Java Backend :8081
  |
  v
OSM data
~~~

The TypeScript server handles incoming HTTP requests and forwards `/geocode` requests to the Java backend.

The Java backend handles the geocoding logic, reads the OpenStreetMap dataset, and searches for matching POIs.


## Tech Stack

- **TypeScript / Node.js** – API gateway
- **Java** – geocoding logic
- **OpenStreetMap** – source of POI data

The chosen technologies are sufficient for the current requirements of the application.

## Setup

### Requirements

- Node.js (v18+)
- npm
- Java JDK 17+
- Apache Maven

### Java Backend

The Java backend runs on port `8081`.

Make sure the OSM dataset file is placed in the resources folder:

~~~text
geocoding-core/src/main/resources/map-brno-city-centre.osm
~~~

Navigate to the `geocoding-core` directory and start the Java backend:

~~~bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.malincok.geocoding.GeocodingApi"
~~~

### TypeScript API Gateway

Navigate to the `geocoding-api` directory, install dependencies, and start the server:

~~~bash
npm install
npx ts-node src/server.ts
~~~

The API Gateway will be available at:

~~~text
http://127.0.0.1:8080
~~~

## API

### Find POI by name

~~~http
GET /geocode?name=<name>
~~~

Example:

~~~bash
curl "http://127.0.0.1:8080/geocode?name=Brno"
~~~

### Find POI by coordinates

~~~http
GET /geocode?lat=<latitude>&lon=<longitude>
~~~

Example:

~~~bash
curl "http://127.0.0.1:8080/geocode?lat=49.1922443&lon=16.6113382"
~~~

### Response Codes

| Code | Description |
|------|-------------|
| `200` | POI found |
| `400` | Invalid request |
| `404` | POI not found |
| `500` | Internal server error |
| `503` | Java backend unavailable |

## Architectural Decisions

The TypeScript and Java services are separated to keep the API layer independent from the geocoding logic.

The OSM data is loaded into memory when the Java backend starts, which provides fast lookups for the current dataset.

When multiple POIs match, the application uses deterministic sorting to consistently select the same result.

## Current Limitations

- **Fixed Local Dataset:** The application currently runs on a static OpenStreetMap dataset bounded by specific coordinates (`16.43512969, 49.07250360, 16.78533938, 49.32194151`).
- **Single File Input:** Data is loaded at startup from a single hardcoded resource file, lacking support for dynamic paths, multiple file merging, or external CLI parameters.
- **Strict Query Matching & Fixed Parameter Order:** Direct geocoding relies on exact character matching and is case-sensitive (e.g., searching for "brno" will not match "Brno"). Additionally, reverse geocoding requires a fixed query parameter order: `lat` must come first, followed by `lon`. 
- **Exact Coordinate Matching:** Reverse geocoding does not support search radiuses or distance tolerance. Coordinates must match the exact floating-point values stored in the OSM data.
- **In-Memory Storage:** All parsed POIs are kept directly in memory, making dataset capacity limited by available RAM and Java collection bounds.
---

## Future Improvements

### Data & Search Enhancements
- **Fuzzy & Case-Insensitive Search:** Implement case-insensitive matching and fuzzy search algorithms (e.g., Levenshtein distance) or integrate **Elasticsearch** for flexible full-text queries.
- **Flexible Coordinate Matching:** Allow configurable decimal precision (e.g., matching coordinates up to 6 decimal places) instead of requiring an exact match. 
- **Tag Relevance Scoring:** Instead of using total tag count to choose between duplicate POIs, the application could evaluate tag importance — prioritizing key tags such as `amenity` or `addr:*` over general metadata.
- **On-Demand External Geocoding (Nominatim Integration):** Fallback to external APIs like [Nominatim](https://nominatim.openstreetmap.org/) for queries missing in the local dataset, with optional response caching.

### Architecture & Dataset Management
- **Structured JSON Responses:** Transition from plain text output to structured JSON payloads with standard HTTP status codes, error messaging, and metadata.
- **Configurable Data Sources & Multi-File Support:** Allow passing dataset paths via environment variables or CLI arguments, enabling the application to load and merge multiple OSM files.
- **Spatial Database Integration:** Replace in-memory parsing with a spatial database (e.g., PostgreSQL with PostGIS) to handle large-scale datasets efficiently.

### Frontend & UI Enhancements
- **Interactive Map UI:** Develop a graphical interface displaying search results as interactive markers on a map.
- **Category & Type Filtering:** Add UI controls to allow users to filter results by POI categories (e.g., cities, restaurants, historical sites).