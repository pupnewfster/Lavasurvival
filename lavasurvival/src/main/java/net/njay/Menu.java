package net.njay;

import org.bukkit.inventory.Inventory;

/**
 * Class to represent a Menu
 */
public class Menu {
    /**
     * The Bukkit Inventory for this Menu
     */
    private Inventory inv;

    /**
     * The MenuManager for this Menu
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final MenuManager manager;

    public Menu(MenuManager manager, Inventory inv) {
        this.manager = manager;
        this.inv = inv;
    }

    /**
     * Gets this Menu's Bukkit Inventory
     * @return Bukkit Inventory
     */
    public Inventory getInventory() {
        return this.inv;
    }

    /**
     * Sets this Menu's Bukkit Inventory
     * @param inv Bukkit Inventory
     */
    public void setInventory(Inventory inv) {
        this.inv = inv;
    }
}