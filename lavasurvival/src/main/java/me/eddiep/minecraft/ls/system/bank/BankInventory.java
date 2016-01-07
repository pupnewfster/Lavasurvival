package me.eddiep.minecraft.ls.system.bank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BankInventory {
    private static HashMap<Player, BankInventory> INSTANCERS = new HashMap<Player, BankInventory>();

    private Inventory inventory;
    private int offset = 0;
    private List<ItemStack> items;
    private BankInventory() { }

    public static BankInventory create(Player p, List<ItemStack> items) {
        int slotSize = 9;
        while (items.size() > slotSize) {
            slotSize += 9;
        }
        slotSize += 9;
        slotSize = Math.min(slotSize, 54);

        Inventory inventory = Bukkit.createInventory(null, slotSize, "Bank");

        for (int i = 0; i < slotSize; i++) {
            if (i >= items.size())
                break;
            if (i == 53 || i == 45)
                continue;

            ItemStack item = items.get(i);
            inventory.setItem(i, item);
        }

        if (slotSize == 54) {
            ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "Next Page ->"));
            item.setItemMeta(meta);

            inventory.setItem(53, item);
        }

        BankInventory sinventory = new BankInventory();
        sinventory.inventory = inventory;
        sinventory.items = items;
        INSTANCERS.put(p, sinventory);

        return sinventory;
    }

    public static BankInventory from(Player p) {
        if (INSTANCERS.containsKey(p))
            return INSTANCERS.get(p);
        return null;
    }

    public void nextPage() {
        if (offset + 45 >= items.size())
            return;

        saveItems();
        offset += 54;

        updateView();
    }

    public void previousPage() {
        if (offset - 45 < 0)
            return;

        saveItems();
        offset -= 54;

        updateView();
    }

    private void saveItems() {
        for (int i = offset; i < offset + inventory.getSize(); i++) {
            if (i >= items.size()) {
                if (inventory.getItem(i % 54) != null) {
                    this.items.add(inventory.getItem(i % 54));
                }
            } else {
                this.items.set(i, inventory.getItem(i % 54));
            }
        }
    }

    public void updateView() {
        inventory.clear();

        for (int i = offset; i < offset + 54; i++) {
            if (i >= items.size())
                break;
            ItemStack item = items.get(i);
            inventory.setItem(i % 54, item);
        }

        if (offset + 54 < items.size()) {
            ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "Next Page ->"));
            item.setItemMeta(meta);

            inventory.setItem(53, item);
        }

        if (offset != 0) {
            ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "<- Previous Page"));
            item.setItemMeta(meta);

            inventory.setItem(45, item);
        }

        for (HumanEntity p : inventory.getViewers()) {
            if (p instanceof Player) {
                ((Player)p).updateInventory();
            }
        }
    }

    public Inventory openFor(Player p) {
        p.openInventory(inventory);
        return inventory;
    }

    public boolean isNextPageButton(ItemStack stack) {
        if (stack == null)
            return false;
        if (stack.getItemMeta() == null)
            return false;

        if (stack.getItemMeta().getLore() != null) {
            for (String lore : stack.getItemMeta().getLore()) {
                if (lore.equals(ChatColor.GREEN + "Next Page ->"))
                    return true;
            }
        }

        return false;
    }

    public boolean isPreviousPageButton(ItemStack stack) {
        if (stack == null)
            return false;
        if (stack.getItemMeta() == null)
            return false;

        if (stack.getItemMeta().getLore() != null) {
            for (String lore : stack.getItemMeta().getLore()) {
                if (lore.equals(ChatColor.GREEN + "<- Previous Page"))
                    return true;
            }
        }

        return false;
    }

    public void end(Player p) {
        saveItems();
        INSTANCERS.remove(p);
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
