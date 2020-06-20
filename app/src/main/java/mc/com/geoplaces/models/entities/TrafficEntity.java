package mc.com.geoplaces.models.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TrafficEntity extends RealmObject {

    @PrimaryKey
    private int id;
    private String imageUrl;
    private double lat;
    private double lng;
    private String address;
    private String type;
    private String date;
    //private double distance;
    //public TrafficEntity(int id, String imageUrl, double lat, double lng, String address, String type, String date, double distance) {
    public TrafficEntity(int id, String imageUrl, double lat, double lng, String address, String type, String date) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.type = type;
        this.date = date;
        //this.distance = distance;
    }

    public TrafficEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //public void setDistance(double distance){ this.distance = distance;}

    //public double getDistance(){ return distance; }
}