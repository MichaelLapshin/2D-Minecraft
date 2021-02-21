// Version 3

import java.util.Map;
import java.util.TreeMap;

public class Inventory {
    private TreeMap<Byte, Integer> inventory; // type, quantity
    private byte NUMBER_OF_ITEMS = 80;

    public Inventory() {
        this.inventory = new TreeMap<>();
        for (byte i = 0; i < NUMBER_OF_ITEMS; i++) {
            inventory.put(i, 0);
        }
    }

    public Inventory(TreeMap<Byte, Integer> inventory) {
        if (inventory != null) this.inventory = inventory;
        else this.inventory = new TreeMap<>();
        for (byte i = 0; i < NUMBER_OF_ITEMS; i++) {
            inventory.put(i, 0);
        }
    }

    /////=== Single Item Methods ===\\\\\
    public void addItem(byte item, int quantity) {
        this.inventory.put(item, this.inventory.get(item) + quantity);
    }

    public void removeItem(byte item, int quantity) {
        this.inventory.put(item, this.inventory.get(item) - quantity);
    }

    public boolean canAfford(byte item, int quantity) {
        return this.inventory.get(item) >= quantity;
    }

    public int itemCount(byte item) {
        return this.inventory.get(item);
    }

    /////=== Multi Item Methods ===\\\\\
    public void addItems(TreeMap<Byte, Integer> items) {
        for (Map.Entry<Byte, Integer> entry : items.entrySet()) {
            this.inventory.put(entry.getKey(), this.inventory.get(entry.getKey()) + entry.getKey());
        }
    }

    public void removeItems(TreeMap<Byte, Integer> items) {
        for (Map.Entry<Byte, Integer> entry : items.entrySet()) {
            this.inventory.put(entry.getKey(), this.inventory.get(entry.getKey()) - entry.getKey());
        }
    }

    public boolean canAfford(TreeMap<Byte, Integer> items) {
        for (Map.Entry<Byte, Integer> entry : items.entrySet()) {
            if (this.inventory.get(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public TreeMap<Byte, Integer> getInventory() {
        return inventory;
    }

}
