package com.example.boris.rijksapp;

/**
 * Created by Boris on 11-12-2016.
 */
public class ArtData {

    private String description;
    private String image_url;
    private String objectNumber;

    public ArtData() {}

    public ArtData(String description, String image_url, String objectNumber) {
        this.description = description;
        this.image_url = image_url;
        this.objectNumber = objectNumber;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(String objectNumber) {
        this.objectNumber = objectNumber;
    }

}
