package com.rt.simulator.util;

import com.rt.simulator.model.Coordinate;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LocationHttpClient {

    private static final Logger logger = Logger.getLogger(LocationHttpClient.class);

    private final String MAP_BOX_BASE_URL;
    private final String MAP_BOX_API_KEY;
    private final String GRAPH_HOOPER_BASE_URL;
    private final String GRAPH_HOOPER_API_KEY;
    private Properties properties = null;

    public LocationHttpClient() throws IOException {
        properties = PropertyFileReader.readPropertyFile();
        MAP_BOX_BASE_URL = properties.getProperty("com.rt.simulator.mapbox.street.base.url");
        MAP_BOX_API_KEY = properties.getProperty("com.rt.simulator.mapbox.apikey");
        GRAPH_HOOPER_BASE_URL = properties.getProperty("com.rt.simulator.graphhooper.route.base.url");
        GRAPH_HOOPER_API_KEY = properties.getProperty("com.rt.simulator.graphhooper.apikey");
    }

    public Coordinate getStreetCoordinates(String streetName) throws IOException {
        String url = MAP_BOX_BASE_URL + encodeStreetName(streetName) + properties.getProperty("com.rt.simulator.mapbox.street.post.fix") + MAP_BOX_API_KEY;
//        logger.info("start fetching street coordinates ... ");
        HttpResponse response = makeHttpRequest(url);
        Coordinate coordinate = getStreetCoordinates(response);
//        logger.info("done fetching street coordinates");
        return coordinate;
    }

    public List<Coordinate> getRouteCoordinates(Coordinate source, Coordinate destination) throws IOException {
        String url = GRAPH_HOOPER_BASE_URL +
                "point=" + source.getLatitude() + "," + source.getLongitude() +
                "&point=" + destination.getLatitude() + "," + destination.getLongitude() +
                properties.getProperty("com.rt.simulator.graphhooper.route.post.fix") +
                GRAPH_HOOPER_API_KEY;
//        logger.info("start fetching route ... ");
//        HttpResponse response = makeHttpRequest(url);
        HttpResponse response = makeHttpRequest("https://graphhopper.com/api/1/route?point=" + source.getLatitude() + "," + source.getLongitude() + "&point=" + destination.getLatitude() + "," + destination.getLongitude() + "&vehicle=car&points_encoded=false&key=9d4f5ffe-bdf2-42b1-aa9b-7d2e0637509b");
        List<Coordinate> coordinates = getRouteCoordinates(response);
//        logger.info("done fetching route ... ");
        return coordinates;
    }

    private HttpResponse makeHttpRequest(String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        return httpClient.execute(request);
    }

    private Coordinate getStreetCoordinates(HttpResponse response) throws IOException {
        String json = IOUtils.toString(response.getEntity().getContent());
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("features");
        JSONObject street = jsonArray.getJSONObject(0);
        JSONArray centerCoordinates = street.getJSONArray("center");
        return new Coordinate(centerCoordinates.get(1).toString(),centerCoordinates.get(0).toString());
    }

    private List<Coordinate> getRouteCoordinates(HttpResponse response) throws IOException {
        String json = IOUtils.toString(response.getEntity().getContent());
        JSONObject jsonObject = new JSONObject(json);
        JSONArray paths = jsonObject.getJSONArray("paths");
        JSONObject firstIndex = paths.getJSONObject(0);
        JSONObject points = firstIndex.getJSONObject("points");
        JSONArray coordinatesJsonArray = points.getJSONArray("coordinates");
        List<Coordinate> coordinates = new ArrayList<>();
        for(int i=0; i < coordinatesJsonArray.length(); i++) {
            JSONArray jsonArray = coordinatesJsonArray.getJSONArray(i);
            coordinates.add(new Coordinate(jsonArray.getDouble(1) + "",jsonArray.getDouble(0) + ""));
        }
        return coordinates;
    }

    private String encodeStreetName(String streetName) {
        return streetName.replaceAll(" ","%20");
    }

}
