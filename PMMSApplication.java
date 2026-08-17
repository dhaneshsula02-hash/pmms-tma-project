package com.acme.pmms;

import com.acme.pmms.model.*;
import com.acme.pmms.patterns.*;
import com.acme.pmms.services.*;
import com.acme.pmms.exceptions.*;

public class PMMSApplication {
    public static void main(String[] args) {
        System.out.println("  ACME (Pvt) Ltd. - PMMS Execution Pipeline \n");

        Thread batch1 = new Thread(new ProductionTask("gel", "GEL1", 20));
        Thread batch2 = new Thread(new ProductionTask("ballpoint", "BAL1", 15));

        batch1.start();
        batch2.start();

        try {
            batch1.join();
            batch2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Pen validPen = new GelPen("GEL2", "ACME Gel Pro", 30.0, 1.9);
        Pen defectivePen = new GelPen("GEL3", "ACME Gel Low", 30.0, 0.8);

        Thread qaThread1 = new Thread(new QualityInspectionTask(validPen));
        Thread qaThread2 = new Thread(new QualityInspectionTask(defectivePen));

        qaThread1.start();
        qaThread2.start();

        try {
            qaThread1.join();
            qaThread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        ReportStrategy prodStrategy = new ProductionReportStrategy();
        String summaryData = "Batch GEL1: 20 units | Batch BAL1: 15 units successfully processed.";
        
        Thread reportThread = new Thread(new ReportTask(prodStrategy, summaryData, "pmms_production.txt"));
        reportThread.start();

        System.out.println("\n Testing Custom Exception Handling ");
        
        try {
            InventoryManager.getInstance().processSale("GEL1", -5);
        } catch (InvalidOrderException | InsufficientMaterialsException e) {
            System.err.println("Caught Expected Exception: " + e.getMessage());
        }

        try {
            InventoryManager.getInstance().addFinishedGoods(validPen);
            InventoryManager.getInstance().addFinishedGoods(validPen);
        } catch (DuplicateProductIdException e) {
            System.err.println("Caught Expected Exception: " + e.getMessage());
        }

        try {
            InventoryManager.getInstance().processSale("BAL99", 500);
        } catch (InvalidOrderException | InsufficientMaterialsException e) {
            System.err.println("Caught Expected Exception: " + e.getMessage());
        }
    }
}