package com.acme.pmms.model;

public class BallpointPen extends Pen {
    private boolean smoothInkFlow;

    public BallpointPen(String productId, String brandName, double unitPrice, boolean smoothInkFlow) {
        super(productId, brandName, unitPrice);
        this.smoothInkFlow = smoothInkFlow;
    }

    @Override
    public void produce() {
        System.out.println("[Production] Manufacturing Ballpoint Pen: " + getProductId());
    }

    @Override
    public boolean performInspection() {
        return this.smoothInkFlow;
    }
}