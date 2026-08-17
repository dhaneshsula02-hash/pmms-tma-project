package com.acme.pmms.model;

public abstract class Pen implements QualityInspectable {
    private String productId;
    private String brandName;
    private double unitPrice;

    public Pen(String productId, String brandName, double unitPrice) {
        this.productId = productId;
        this.brandName = brandName;
        this.unitPrice = unitPrice;
    }

    public String getProductId() { return productId; }
    public String getBrandName() { return brandName; }
    public double getUnitPrice() { return unitPrice; }

    public abstract void produce();
}