package com.acme.pmms.model;

public class GelPen extends Pen {
    private double inkViscosity;

    public GelPen(String productId, String brandName, double unitPrice, double inkViscosity) {
        super(productId, brandName, unitPrice);
        this.inkViscosity = inkViscosity;
    }

    @Override
    public void produce() {
        System.out.println("[Production] Manufacturing Gel Pen: " + getProductId());
    }

    @Override
    public boolean performInspection() {
        return this.inkViscosity >= 1.5;
    }
}