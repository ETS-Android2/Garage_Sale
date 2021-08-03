package com.example.garagesale.models;

public class Product {
    private String productKey;
    private String productOwnerUid;
    private String productName;
    private double productPrice;
    private String productDescription;
    private double productLat;
    private double productLng;

    public Product() {}

    public Product(String productKey, String productOwnerUid, String productName, double productPrice, String productDescription, Double productLat, Double productLng) {
        this.productKey = productKey;
        this.productOwnerUid = productOwnerUid;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.productLat = productLat;
        this.productLng = productLng;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getProductLat() {
        return productLat;
    }

    public void setProductLat(double productLat) {
        this.productLat = productLat;
    }

    public double getProductLng() {
        return productLng;
    }

    public void setProductLng(double productLng) {
        this.productLng = productLng;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductOwnerUid() {
        return productOwnerUid;
    }

    public void setProductOwnerUid(String productOwnerUid) {
        this.productOwnerUid = productOwnerUid;
    }
}
