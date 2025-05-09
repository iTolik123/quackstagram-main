package com.example;
public class ImageData {
    private String imageName; // Name of the image file
    private byte[] imageBytes; // Raw binary data of the image

    // Constructor
    public ImageData(String imageName, byte[] imageBytes) {
        this.imageName = imageName;
        this.imageBytes = imageBytes;
    }

    // Getter for image name
    public String getImageName() {
        return imageName;
    }

    // Getter for image bytes
    public byte[] getImageBytes() {
        return imageBytes;
    }
}