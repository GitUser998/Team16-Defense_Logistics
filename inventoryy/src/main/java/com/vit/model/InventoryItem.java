package com.vit.model;

public class InventoryItem {

    private String itemId;
    private String itemName;
    private String category;
    private int quantity;
    private int reorderLevel;
    private String unit;
    private String location;

    public InventoryItem() {
    }

    public InventoryItem(String itemId, String itemName, String category, int quantity,
                         int reorderLevel, String unit, String location) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.unit = unit;
        this.location = location;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}