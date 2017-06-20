package me.eddiep.minecraft.ls.system.bank;

import me.eddiep.PhysicsEngine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BankInventory {
    private static final HashMap<Player, BankInventory> INSTANCERS = new HashMap<>();

    private Inventory inventory;
    private int offset;
    private List<MaterialData> items;

    private BankInventory() {
    }

    public static BankInventory create(Player p, List<MaterialData> items) {
        int slotSize = 9;
        while (items.size() > slotSize)
            slotSize += 9;
        slotSize += 9;
        slotSize = Math.min(slotSize, 54);
        Inventory inventory = Bukkit.createInventory(null, slotSize, "Bank");
        for (int i = 0; i < slotSize; i++) {
            if (i >= items.size())
                break;
            if (i == 53 || i == 45)
                continue;
            ItemStack item = items.get(i).toItemStack(1);
            if (item != null && item.getType() == Material.EMERALD_BLOCK)
                continue;
            inventory.setItem(i, addMeltTime(item));
        }
        if (slotSize == 54) {
            ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "Next Page ->"));
            item.setItemMeta(meta);
            inventory.setItem(53, item);
        }
        BankInventory sInventory = new BankInventory();
        sInventory.inventory = inventory;
        sInventory.items = items;
        INSTANCERS.put(p, sInventory);
        return sInventory;
    }

    private static ItemStack addMeltTime(ItemStack is) {
        if (is != null) {
            MaterialData dat = is.getData();
            if (!dat.getItemType().equals(Material.AIR)) {
                ItemMeta im = is.getItemMeta();
                im.setLore(Collections.singletonList("Lava MeltTime: " + PhysicsEngine.getLavaMeltTimeAsString(dat)));
                is.setItemMeta(im);
            }
        }
        return is;
    }

    public static BankInventory from(Player p) {
        if (INSTANCERS.containsKey(p))
            return INSTANCERS.get(p);
        return null;
    }

    public void nextPage() {
        if (this.offset + 45 >= this.items.size())
            return;
        saveItems();
        this.offset += 54;
        updateView();
    }

    public void previousPage() {
        if (this.offset - 45 < 0)
            return;
        saveItems();
        this.offset -= 54;
        updateView();
    }

    private void saveItems() {
        for (int i = this.offset; i < this.offset + this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i % 54) != null && this.inventory.getItem(i % 54).getType() == Material.EMERALD_BLOCK)
                continue;
            if (i >= this.items.size()) {
                if (this.inventory.getItem(i % 54) != null)
                    this.items.add(this.inventory.getItem(i % 54).getData());
            } else if (this.inventory.getItem(i % 54) != null)
                this.items.set(i, this.inventory.getItem(i % 54).getData());
            else
                this.items.set(i, new MaterialData(Material.AIR));
        }
    }

    private void updateView() {
        this.inventory.clear();

        for (int i = this.offset; i < this.offset + 54; i++) {
            if (i >= this.items.size())
                break;
            ItemStack item = this.items.get(i).toItemStack(1);
            if (item != null && item.getType() == Material.EMERALD_BLOCK)
                continue;
            this.inventory.setItem(i % 54, addMeltTime(item));
        }
        if (this.offset + 45 < this.items.size()) {
            ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "Next Page ->"));
            item.setItemMeta(meta);
            this.inventory.setItem(53, item);
        }
        if (this.offset != 0) {
            ItemStack item = new ItemStack(Material.EMERALD_BLOCK, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GREEN + "<- Previous Page"));
            item.setItemMeta(meta);
            this.inventory.setItem(45, item);
        }
        this.inventory.getViewers().stream().filter(p -> p instanceof Player).forEach(p -> ((Player) p).updateInventory());
    }

    public Inventory openFor(Player p) {
        p.openInventory(this.inventory);
        return this.inventory;
    }

    public boolean isNextPageButton(ItemStack stack) {
        return stack != null && stack.getItemMeta() != null && stack.getItemMeta().getLore() != null && stack.getItemMeta().getLore().stream().anyMatch(lore -> lore.equals(ChatColor.GREEN + "Next Page ->"));
    }

    public boolean isPreviousPageButton(ItemStack stack) {
        return stack != null && stack.getItemMeta() != null && stack.getItemMeta().getLore() != null && stack.getItemMeta().getLore().stream().anyMatch(lore -> lore.equals(ChatColor.GREEN + "<- Previous Page"));
    }

    public void end(Player p) {
        saveItems();
        INSTANCERS.remove(p);
    }

    @SuppressWarnings("unused")
    public List<MaterialData> getItems() {
        return this.items;
    }
}