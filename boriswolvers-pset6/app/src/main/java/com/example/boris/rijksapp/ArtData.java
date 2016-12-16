package com.example.boris.rijksapp;
/**
 * Created by Boris on 11-12-2016.
 * ArtData - Class creates the objects for each artwork.
 */
public class ArtData {

    // ArtData object contains these 'columns'
    private String description;
    private String image_url;
    private String objectNumber;

    // Empty constructor
    public ArtData() {}

    // Initialize object
    public ArtData(String description, String image_url, String objectNumber) {
        this.description = description;
        this.image_url = image_url;
        this.objectNumber = objectNumber;
    }

    // Getters and setters
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
