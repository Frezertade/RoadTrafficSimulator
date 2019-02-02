package com.rt.simulator.producer;

import com.rt.simulator.model.Coordinate;
import com.rt.simulator.model.Route;
import com.rt.simulator.model.Vehicle;
import com.rt.simulator.util.LocationFileReader;
import com.rt.simulator.util.LocationHttpClient;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class VehicleProducer {

    private static final Logger logger = Logger.getLogger(VehicleProducer.class);
    List<String> streets;
    List<Route> routes = new ArrayList<>();
    LocationHttpClient locationHttpClient;

    public VehicleProducer() throws IOException {
        LocationFileReader locationFileReader = new LocationFileReader();
        streets = locationFileReader.readStreets();
        locationHttpClient = new LocationHttpClient();
    }

    public void generateVehicleEvent(Producer<String, Vehicle> producer, String topic) throws InterruptedException, IOException {
        List<String> vehicleTypeList = Arrays.asList(new String[]{"Large Truck", "Small Truck", "Private Car", "Bus", "Taxi"});
        Random random = new Random();
        logger.info("Sending events ...");
        int numOfVehicle = 10;
        int numOfRoute = 10;
        Thread thread = new Thread(() -> {
            while (true) {
                List<Vehicle> eventsList = new ArrayList<>();
                try {
                    for (int i = 0; i < numOfRoute; i++) {
                        Route route = selectRandomRoute(streets);
                        Date timestamp = new Date();
                        double speed = random.nextInt(100 - 20) + 20;
                        double fuelLevel = random.nextInt(40 - 10) + 10;
                        List<Coordinate> coordinates = getCoordinates(route);
                        for (int j = 0; j < numOfVehicle; j++) {
                            Vehicle vehicle = new Vehicle(UUID.randomUUID().toString(), vehicleTypeList.get(random.nextInt(5)), route.getRouteId(), route.getSource(),
                                    route.getDestination(), null, null, timestamp, speed, fuelLevel);
                            for (Coordinate coordinate : coordinates) {
                                vehicle.setLatitude(coordinate.getLatitude());
                                vehicle.setLongitude(coordinate.getLongitude());
                                eventsList.add(vehicle);
                            }
                        }
                    }
                    Collections.shuffle(eventsList);
                    for (Vehicle vehicle : eventsList) {
                        KeyedMessage<String, Vehicle> message = new KeyedMessage<>(topic, vehicle);
                        producer.send(message);
                        Thread.sleep(random.nextInt(3000 - 1000) + 1000);
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        });
        thread.start();
    }

    private List<Coordinate> getCoordinates(Route route) throws IOException {
        if (routes.contains(route)) {
            return routes.get(routes.indexOf(route)).getCoordinates();
        }
        String postfix = ",Atlanta, Georgia 30309, United States";
        Coordinate coordinate1 = locationHttpClient.getStreetCoordinates(route.getSource() + postfix);
        Coordinate coordinate2 = locationHttpClient.getStreetCoordinates(route.getDestination() + postfix);
        List<Coordinate> coordinates = locationHttpClient.getRouteCoordinates(coordinate1, coordinate2);
        route.setCoordinates(coordinates);
        routes.add(route);
        return coordinates;
    }

    public Route selectRandomRoute(List<String> streets) {
        Random random = new Random();
        String street1 = streets.get(random.nextInt(streets.size()));
        String street2 = streets.get(random.nextInt(streets.size()));
        if (street1.equals(street2)) {
            street2 = selectRandomStreet(streets, street1);
        }
        return new Route(UUID.randomUUID().toString(), street1, street2);
    }

    public String selectRandomStreet(List<String> streets, String previousStreet) {
        Random random = new Random();
        String street = streets.get(random.nextInt(streets.size()));
        if (street.equals(previousStreet)) {
            street = selectRandomStreet(streets, previousStreet);
        }
        return street;
    }

}