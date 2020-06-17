package mc.com.geoplaces.models.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DeliveryEntity extends RealmObject {

    @PrimaryKey
    private int id;
    private String description;
    private String imageUrl;
    private double lat;
    private double lng;
    private String address;

    public DeliveryEntity(int id, String description, String imageUrl, double lat, double lng, String address) {
        this.id = id;
        this.description = description;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public DeliveryEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}