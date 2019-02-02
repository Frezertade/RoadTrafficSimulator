package com.rt.simulator.model;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private String routeId;
    private String source;
    private String destination;
    private List<Coordinate> coordinates = new ArrayList<>();

    public Route() {
    }

    public Route(String routeId, String source, String destination) {
        this.routeId = routeId;
        this.source = source;
        this.destination = destination;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Route route = (Route) object;

        return routeId.equals(route.routeId);
    }

    @Override
    public int hashCode() {
        return routeId.hashCode();
    }
}
