package com.acme.pmms.services;

import com.acme.pmms.model.Pen;
import com.acme.pmms.patterns.*;
import com.acme.pmms.exceptions.*;

public class ProductionTask implements Runnable {
    private String penType;
    private String productId;
    private int batchQuantity;

    public ProductionTask(String penType, String productId, int batchQuantity) {
        this.penType = penType;
        this.productId = productId;
        this.batchQuantity = batchQuantity;
    }

    @Override
    public void run() {
        System.out.println("[Thread - Production] Task started for ID: " + productId);
        try {
            InventoryManager.getInstance().verifyAndDeductMaterials(batchQuantity);
            Pen newPen = PenFactory.createPen(penType, productId, "ACME", 25.0);
            newPen.produce();
            InventoryManager.getInstance().addFinishedGoods(newPen, batchQuantity);
        } catch (InsufficientMaterialsException e) {
            System.err.println("[Thread - Production Exception] " + e.getMessage());
        }
    }
}