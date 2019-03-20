package com.rapipay.android.agent.Model;

public class ImagePozo {
    int imageId;
    String imageTagName;
    String imageName;
    byte[] imagePath;
    int imageUrl;

    public ImagePozo(){}

    public ImagePozo(String imageTagName, String imageName, int imageUrl) {
        this.imageTagName = imageTagName;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public String getImageTagName() {
        return imageTagName;
    }

    public void setImageTagName(String imageTagName) {
        this.imageTagName = imageTagName;
    }

    public byte[] getImagePath() {
        return imagePath;
    }

    public void setImagePath(byte[] imagePath) {
        this.imagePath = imagePath;
    }

    public ImagePozo(int imageId, String imageName, int imageUrl) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }
}
