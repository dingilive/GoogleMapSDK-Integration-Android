package com.example.dingisample.model;

public class SearchAddress {


    private String Id;
    private String RoadName;
    private String Address;
    private String Distance;
    private Double Lat;
    private Double Lon;


    public SearchAddress(String id, String roadName, String address, String distance, Double lat, Double lon) {
        Id = id;
        RoadName = roadName;
        Address = address;
        Distance = distance;
        Lat = lat;
        Lon = lon;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getRoadName() {
        return RoadName;
    }

    public void setRoadName(String roadName) {
        RoadName = roadName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLon() {
        return Lon;
    }

    public void setLon(Double lon) {
        Lon = lon;
    }
}
