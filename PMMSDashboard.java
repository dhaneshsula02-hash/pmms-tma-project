package com.acme.pmms;

import com.acme.pmms.exceptions.DuplicateProductIdException;
import com.acme.pmms.exceptions.InsufficientMaterialsException;
import com.acme.pmms.exceptions.InvalidOrderException;
import com.acme.pmms.model.BallpointPen;
import com.acme.pmms.model.GelPen;
import com.acme.pmms.model.Pen;
import com.acme.pmms.patterns.InventoryManager;
import com.acme.pmms.patterns.ProductionReportStrategy;
import com.acme.pmms.services.ProductionTask;
import com.acme.pmms.services.QualityInspectionTask;
import com.acme.pmms.services.ReportTask;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class PMMSDashboard {
    private static final Map<String, String> inspectionRecords = new LinkedHashMap<>();
    private static final Map<String, String> activityRecords = new LinkedHashMap<>();

    public static void main(String[] args) {
        
        System.out.println("ACME PMMS Control Panel");
        System.out.println("Live Production Management Dashboard\n");
        
        showDashboard();
        runMenu();
    }

    private static void runMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n Control Menu ");
            System.out.println("1. Process Production Batch");
            System.out.println("2. Conduct Quality Inspection");
            System.out.println("3. Process Sales Order");
            System.out.println("4. Add Finished Goods");
            System.out.println("5. Generate System Log");
            System.out.println("6. Refresh System Dashboard");
            System.out.println("7. Exit System");
            System.out.print("Select an option: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    handleProductionBatch(scanner);
                    break;
                case "2":
                    handleQualityInspection(scanner);
                    break;
                case "3":
                    handleSalesOrder(scanner);
                    break;
                case "4":
                    handleAddFinishedGoods(scanner);
                    break;
                case "5":
                    handleSystemLog(scanner);
                    break;
                case "6":
                    showDashboard();
                    break;
                case "7":
                    System.out.println("System shutdown request received. Closing control panel.");
                    return;
                default:
                    System.out.println("Invalid selection. Please choose a value from 1 to 7.");
                    break;
            }
        }
    }

    private static void handleProductionBatch(Scanner scanner) {
        System.out.print("Enter pen type (gel/ballpoint): ");
        String penType = scanner.nextLine().trim();
        System.out.print("Enter product ID: ");
        String productId = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        String quantityText = scanner.nextLine().trim();

        int quantity = parsePositiveInteger(quantityText);
        if (quantity <= 0) {
            System.out.println("Batch quantity must be greater than zero.");
            showDashboard();
            return;
        }

        Thread batchThread = new Thread(new ProductionTask(penType, productId, quantity));
        batchThread.start();
        try {
            batchThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Production processing was interrupted: " + e.getMessage());
        }

        activityRecords.put("Production Batch", "processed " + penType + " for " + productId + " quantity " + quantity);
        showDashboard();
    }

    private static void handleQualityInspection(Scanner scanner) {
        System.out.print("Enter product ID: ");
        String productId = scanner.nextLine().trim();
        System.out.print("Enter ink viscosity: ");
        String viscosityText = scanner.nextLine().trim();

        double viscosity = parseDouble(viscosityText);
        Pen pen = new GelPen(productId, "ACME", 25.0, viscosity);

        Thread inspectionThread = new Thread(new QualityInspectionTask(pen));
        inspectionThread.start();
        try {
            inspectionThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Inspection procedure was interrupted: " + e.getMessage());
        }

        String decision = pen.performInspection() ? "PASS" : "QUARANTINE";
        inspectionRecords.put(productId, decision);
        activityRecords.put("Quality Inspection", "recorded " + productId + " as " + decision);
        System.out.println("Quality outcome for " + productId + ": " + decision);
        showDashboard();
    }

    private static void handleSalesOrder(Scanner scanner) {
        System.out.print("Enter product ID: ");
        String productId = scanner.nextLine().trim();
        System.out.print("Enter order quantity: ");
        String quantityText = scanner.nextLine().trim();

        int quantity = parsePositiveInteger(quantityText);
        if (quantity <= 0) {
            System.out.println("Order quantity must be greater than zero.");
            showDashboard();
            return;
        }

        try {
            InventoryManager.getInstance().processSale(productId, quantity);
            activityRecords.put("Sales Order", "processed " + productId + " quantity " + quantity);
        } catch (InvalidOrderException | InsufficientMaterialsException e) {
            System.out.println("Sales order issue: " + e.getMessage());
            activityRecords.put("Sales Order", "issue on " + productId + ": " + e.getMessage());
        }

        showDashboard();
    }

    private static void handleAddFinishedGoods(Scanner scanner) {
        System.out.print("Enter pen type (gel/ballpoint): ");
        String penType = scanner.nextLine().trim();
        System.out.print("Enter product ID: ");
        String productId = scanner.nextLine().trim();
        System.out.print("Enter brand name: ");
        String brandName = scanner.nextLine().trim();
        System.out.print("Enter unit price: ");
        String priceText = scanner.nextLine().trim();

        double unitPrice = parseDouble(priceText);
        Pen pen = createPen(penType, productId, brandName, unitPrice);

        if (pen == null) {
            System.out.println("Unsupported pen type. Use gel or ballpoint.");
            showDashboard();
            return;
        }

        try {
            InventoryManager.getInstance().addFinishedGoods(pen);
            activityRecords.put("Finished Goods", "added " + productId + " to inventory");
        } catch (DuplicateProductIdException e) {
            System.out.println("Duplicate product issue: " + e.getMessage());
            activityRecords.put("Finished Goods", "duplicate on " + productId + ": " + e.getMessage());
        }

        showDashboard();
    }

    private static void handleSystemLog(Scanner scanner) {
        System.out.print("Enter log details: ");
        String logData = scanner.nextLine().trim();
        System.out.print("Enter target filename: ");
        String targetFile = scanner.nextLine().trim();

        Thread reportThread = new Thread(new ReportTask(new ProductionReportStrategy(), logData, targetFile));
        reportThread.start();
        try {
            reportThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Log generation was interrupted: " + e.getMessage());
        }

        activityRecords.put("System Log", "generated: " + logData + " -> " + targetFile);
        showDashboard();
    }

    private static void showDashboard() {
        System.out.println("\n=== System Overview ===");
        Map<String, Integer> rawMaterials = readInventoryMap("rawMaterials");
        Map<String, Integer> finishedGoods = readInventoryMap("finishedGoods");

        System.out.println("Raw Material Stock:");
        if (rawMaterials.isEmpty()) {
            System.out.println("  No raw material entries available.");
        } else {
            rawMaterials.forEach((key, value) -> System.out.println("  - " + key + ": " + value + " units"));
        }

        System.out.println("Finished Goods Inventory:");
        if (finishedGoods.isEmpty()) {
            System.out.println("  No finished goods entries available.");
        } else {
            finishedGoods.forEach((key, value) -> System.out.println("  - " + key + ": " + value + " units"));
        }

        System.out.println("Operational Status:");
        System.out.println("  - System: Online");
        System.out.println("  - Workforce: Available");
        System.out.println("  - Quality Monitor: Active");

        String latestActivity = "No recent activity";
        if (!activityRecords.isEmpty()) {
            latestActivity = activityRecords.values().stream().reduce((first, second) -> second).orElse("No recent activity");
        }
        System.out.println("  - Latest Activity: " + latestActivity);

        if (!inspectionRecords.isEmpty()) {
            System.out.println("  - Quality Record:");
            inspectionRecords.forEach((key, value) -> System.out.println("      " + key + " -> " + value));
        }
    }

    private static Map<String, Integer> readInventoryMap(String fieldName) {
        Map<String, Integer> result = new HashMap<>();
        try {
            Field field = InventoryManager.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(InventoryManager.getInstance());
            if (value instanceof Map) {
                Map<?, ?> items = (Map<?, ?>) value;
                for (Map.Entry<?, ?> entry : items.entrySet()) {
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    if (key != null && val instanceof Number) {
                        result.put(String.valueOf(key), ((Number) val).intValue());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Inventory snapshot unavailable: " + e.getMessage());
        }
        return result;
    }

    private static Pen createPen(String penType, String productId, String brandName, double unitPrice) {
        String type = penType == null ? "" : penType.trim().toLowerCase();
        switch (type) {
            case "gel":
                return new GelPen(productId, brandName, unitPrice, 1.8);
            case "ballpoint":
                return new BallpointPen(productId, brandName, unitPrice, true);
            default:
                return null;
        }
    }

    private static int parsePositiveInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
