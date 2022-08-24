package com.gomap.demo.model;

public class PoiModel {

    private String lat;
    private String lng;

    private String name;
    private String subclass;
    private String poiclass;
    private String poi_code;
    private String address;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubclass() {
        return subclass;
    }

    public void setSubclass(String subclass) {
        this.subclass = subclass;
    }

    public String getPoiclass() {
        return poiclass;
    }

    public void setPoiclass(String poiclass) {
        this.poiclass = poiclass;
    }

    public String getPoi_code() {
        return poi_code;
    }

    public void setPoi_code(String poi_code) {
        this.poi_code = poi_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
