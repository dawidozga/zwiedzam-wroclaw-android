package com.wrocapp.zwiedzamwroclaw;

/**
 * Created by Dawid on 07.11.2017.
 */

public class ListItem {

    private int id;
    private String title;
    private String shortDesc;
    private String smallImageURL;
    private String bigImageURL;
    private String longDesc;
    private double latitude;
    private double longitude;

    public ListItem(int id, String title, String shortDesc, String smallImageURL, String bigImageURL, String longDesc, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.shortDesc = shortDesc;
        this.smallImageURL = smallImageURL;
        this.bigImageURL = bigImageURL;
        this.longDesc = longDesc;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getSmallImageURL() {
        return smallImageURL;
    }

    public String getBigImageURL() {
        return bigImageURL;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
