package com.acme.pmms.services;

import com.acme.pmms.model.Pen;

public class QualityInspectionTask implements Runnable {
    private Pen pen;

    public QualityInspectionTask(Pen pen) {
        this.pen = pen;
    }

    @Override
    public void run() {
        System.out.println("[Thread - QA] Inspecting product: " + pen.getProductId());
        if (pen.performInspection()) {
            System.out.println("[Thread - QA] Product " + pen.getProductId() + " PASSED quality inspection.");
        } else {
            System.out.println("[Thread - QA] Product " + pen.getProductId() + " FAILED quality inspection. Quarantined.");
        }
    }
}