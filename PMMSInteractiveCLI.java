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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class PMMSInteractiveCLI {
    private static String pendingInput = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("ACME PMMS - Factory Control Panel");

        while (true) {
            printDashboard();
            printMenu();

            if (!scanner.hasNextLine()) {
                break;
            }

            System.out.print("Select option: ");
            String choice = readInputLine(scanner).trim();
            System.out.println();

            switch (choice) {
                case "1":
                    startProductionBatch(scanner);
                    break;
                case "2":
                    conductQualityInspection(scanner);
                    break;
                case "3":
                    processSalesOrder(scanner);
                    break;
                case "4":
                    generateSystemReport(scanner);
                    break;
                case "5":
                    System.out.println("Exiting control panel...");
                    return;
                default:
                    System.out.println("Invalid option. Please choose from 1 to 5.");
                    pause(scanner);
                    break;
            }
        }
    }

    private static void printDashboard() {
        Map<String, Integer> rawMaterials = readInventoryField("rawMaterials");
        Map<String, Integer> finishedGoods = readInventoryField("finishedGoods");

        System.out.println();
        System.out.println("LIVE DASHBOARD");
        System.out.println();
        System.out.println("Raw materials:");
        if (rawMaterials.isEmpty()) {
            System.out.println("  - none recorded");
        } else {
            for (Map.Entry<String, Integer> entry : rawMaterials.entrySet()) {
                System.out.println("  - " + entry.getKey() + ": " + entry.getValue() + " units");
            }
        }

        System.out.println("Finished goods:");
        if (finishedGoods.isEmpty()) {
            System.out.println("  - none recorded");
        } else {
            int total = 0;
            for (Map.Entry<String, Integer> entry : finishedGoods.entrySet()) {
                int stockQuantity = entry.getValue() == null ? 0 : entry.getValue();
                total += stockQuantity;
                System.out.println("  - Product ID: " + entry.getKey() + " | Stock: " + stockQuantity + " units");
            }
            System.out.println("  Total stock: " + total + " units");
        }

        System.out.println("Status: ONLINE");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println("CONTROL MENU");
        System.out.println("1. Start Production Batch");
        System.out.println("2. Conduct QA Inspection");
        System.out.println("3. Process Sales Order");
        System.out.println("4. Generate System Report");
        System.out.println("5. Exit Control Panel");
    }

    private static void startProductionBatch(Scanner scanner) {
        System.out.println();
        System.out.println("Start production batch");
        System.out.print("Pen type (gel/ballpoint): ");
        String penType = readInputLine(scanner).trim().toLowerCase();

        System.out.print("Product ID: ");
        String productId = readInputLine(scanner).trim();

        System.out.print("Batch quantity: ");
        String qtyInput = readInputLine(scanner).trim();

        int quantity = parsePositiveInt(qtyInput);
        if (quantity <= 0) {
            System.out.println("[ERROR] Batch quantity must be greater than zero.");
            pause(scanner);
            return;
        }

        try {
            if (productExists(productId)) {
                throw new DuplicateProductIdException("Error: Product ID " + productId + " already exists!");
            }

            // Trigger production thread
            Thread productionThread = new Thread(new ProductionTask(penType, productId, quantity));
            productionThread.start();
            productionThread.join();
            System.out.println("Production batch completed for " + productId + ".");
        } catch (DuplicateProductIdException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[ERROR] Production thread interrupted: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        pause(scanner);
    }

    private static void conductQualityInspection(Scanner scanner) {
        System.out.println();
        System.out.println("Conduct quality inspection");
        System.out.print("Product ID: ");
        String productId = readInputLine(scanner).trim();
        System.out.print("Ink viscosity: ");
        String viscosityInput = readInputLine(scanner).trim();

        double viscosity = parseDouble(viscosityInput);
        Pen pen = new GelPen(productId, "ACME", 25.0, viscosity);

        // Trigger QA thread
        Thread inspectionThread = new Thread(new QualityInspectionTask(pen));
        inspectionThread.start();
        try {
            inspectionThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[ERROR] QA thread interrupted: " + e.getMessage());
        }

        String result = pen.performInspection() ? "PASSED" : "FAILED / QUARANTINED";
        System.out.println("Inspection outcome for " + productId + ": " + result);
        pause(scanner);
    }

    private static void processSalesOrder(Scanner scanner) {
        System.out.println();
        System.out.println("Process sales order");
        System.out.print("Product ID: ");
        String productId = readInputLine(scanner).trim();
        System.out.print("Order quantity: ");
        String quantityInput = readInputLine(scanner).trim();

        int quantity = parsePositiveInt(quantityInput);
        try {
            if (quantity <= 0) {
                throw new InvalidOrderException("Error: Sales order quantity must be greater than 0.");
            }

            // Validate stock before sale
            InventoryManager.getInstance().processSale(productId, quantity);
            System.out.println("Sales order processed successfully for " + productId + ".");
        } catch (InvalidOrderException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (InsufficientMaterialsException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        pause(scanner);
    }

    private static void generateSystemReport(Scanner scanner) {
        System.out.println();
        System.out.println("Generate system report");
        System.out.print("Report filename: ");
        String filename = readInputLine(scanner).trim();
        if (filename.isEmpty()) {
            filename = "pmms_production.txt";
        }

        String reportData = buildDynamicProductionReport();
        Thread reportThread = new Thread(new ReportTask(new ProductionReportStrategy(), reportData, filename));
        reportThread.start();
        try {
            reportThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[ERROR] Report thread interrupted: " + e.getMessage());
        }

        System.out.println("System report generated and saved to " + filename + ".");
        pause(scanner);
    }

    private static String buildDynamicProductionReport() {
        Map<String, Integer> rawMaterials = readInventoryField("rawMaterials");
        Map<String, Integer> finishedGoods = readInventoryField("finishedGoods");

        StringBuilder report = new StringBuilder();
        report.append("=== ACME PMMS DYNAMIC PRODUCTION REPORT ===\n");
        report.append("Timestamp: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n");
        report.append("Summary Status: SYSTEM ONLINE\n");
        report.append("Raw Materials:\n");
        report.append("  - Plastic Barrels: ")
                .append(rawMaterials.getOrDefault("PlasticBarrels", 0))
                .append(" units\n");
        report.append("  - Ink Units: ")
                .append(rawMaterials.getOrDefault("InkUnits", 0))
                .append(" units\n");

        report.append("Finished Goods Inventory:\n");
        if (finishedGoods.isEmpty()) {
            report.append("  - No finished goods currently registered.\n");
        } else {
            Map<String, Integer> sortedFinishedGoods = new TreeMap<>(finishedGoods);
            for (Map.Entry<String, Integer> entry : sortedFinishedGoods.entrySet()) {
                report.append("  - Product ID: ")
                        .append(entry.getKey())
                        .append(" | Stock: ")
                        .append(entry.getValue())
                        .append(" units\n");
            }
        }

        report.append("Overall Inventory Status: ")
                .append(finishedGoods.isEmpty() ? "No active finished goods" : "Products tracked: " + finishedGoods.size())
                .append("\n");
        return report.toString();
    }

    private static boolean productExists(String productId) {
        try {
            Field field = InventoryManager.class.getDeclaredField("finishedGoods");
            field.setAccessible(true);
            Object value = field.get(InventoryManager.getInstance());
            if (value instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) value;
                return map.containsKey(productId);
            }
        } catch (Exception e) {
            // Ignore reflection failure and continue with the flow
        }
        return false;
    }

    private static String readInputLine(Scanner scanner) {
        if (pendingInput != null) {
            String value = pendingInput;
            pendingInput = null;
            return value;
        }
        return scanner.nextLine();
    }

    private static void pause(Scanner scanner) {
        System.out.println();
        System.out.println("Press Enter to continue...");
        if (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            if (!nextLine.trim().isEmpty()) {
                pendingInput = nextLine;
            }
        }
        System.out.println();
    }

    private static Map<String, Integer> readInventoryField(String fieldName) {
        Map<String, Integer> result = new HashMap<>();
        try {
            Field field = InventoryManager.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(InventoryManager.getInstance());
            if (value instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) value;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    if (key != null && val instanceof Number) {
                        result.put(String.valueOf(key), ((Number) val).intValue());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[INFO] Inventory snapshot unavailable: " + e.getMessage());
        }
        return result;
    }

    private static int parsePositiveInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
