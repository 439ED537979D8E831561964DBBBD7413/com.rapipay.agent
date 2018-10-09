package com.rapipay.android.agent.Model;

public class ImagePozo {
    int imageId;
    String imageName;
    int imageUrl;

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
