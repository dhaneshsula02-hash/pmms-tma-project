package com.acme.pmms.patterns;

import com.acme.pmms.model.Pen;
import com.acme.pmms.exceptions.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryManager {
    private static InventoryManager instance;
    private Map<String, Integer> rawMaterials = new ConcurrentHashMap<>();
    private Map<String, Integer> finishedGoods = new ConcurrentHashMap<>();
    private Set<String> registeredProductIds = Collections.synchronizedSet(new HashSet<>());

    private InventoryManager() {
        rawMaterials.put("InkUnits", 500);
        rawMaterials.put("PlasticBarrels", 500);
    }

    public static synchronized InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public synchronized void verifyAndDeductMaterials(int quantity) throws InsufficientMaterialsException {
        int availableInk = rawMaterials.getOrDefault("InkUnits", 0);
        int availablePlastic = rawMaterials.getOrDefault("PlasticBarrels", 0);

        if (availableInk < quantity || availablePlastic < quantity) {
            throw new InsufficientMaterialsException("Error: Insufficient raw materials for quantity: " + quantity);
        }

        rawMaterials.put("InkUnits", availableInk - quantity);
        rawMaterials.put("PlasticBarrels", availablePlastic - quantity);
        System.out.println("[Inventory] Deducted " + quantity + " units of raw materials successfully.");
    }

    public synchronized void addFinishedGoods(Pen pen) throws DuplicateProductIdException {
        if (registeredProductIds.contains(pen.getProductId())) {
            throw new DuplicateProductIdException("Error: Product ID " + pen.getProductId() + " already exists!");
        }
        registeredProductIds.add(pen.getProductId());
        finishedGoods.put(pen.getProductId(), finishedGoods.getOrDefault(pen.getProductId(), 0) + 1);
        System.out.println("[Inventory] Added product " + pen.getProductId() + " to Finished Goods Inventory.");
    }

    public synchronized void addFinishedGoods(Pen pen, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Error: Finished goods quantity must be greater than 0.");
        }

        if (!registeredProductIds.contains(pen.getProductId())) {
            registeredProductIds.add(pen.getProductId());
        }

        int currentStock = finishedGoods.getOrDefault(pen.getProductId(), 0);
        finishedGoods.put(pen.getProductId(), currentStock + quantity);
        System.out.println("[Inventory] Added " + quantity + " units of product " + pen.getProductId() + " to Finished Goods Inventory.");
    }

    public synchronized void processSale(String productId, int qty) throws InvalidOrderException, InsufficientMaterialsException {
        if (qty <= 0) {
            throw new InvalidOrderException("Error: Sales order quantity must be greater than 0.");
        }
        int stock = finishedGoods.getOrDefault(productId, 0);
        if (stock < qty) {
            throw new InsufficientMaterialsException("Error: Stock depleted for Product ID: " + productId);
        }
        finishedGoods.put(productId, stock - qty);
        System.out.println("[Sales] Successfully processed sale of " + qty + " units of " + productId);
    }
}