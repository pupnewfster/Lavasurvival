package me.eddiep.minecraft.ls.system.specialblocks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpecialInventory {
    private static final HashMap<FallingBlock, SpecialInventory> INSTANCERS = new HashMap<>();

    private Inventory inventory;

    private SpecialInventory() {
    }

    public static SpecialInventory create(FallingBlock block, InventoryTiers tier) {
        int slotSize = 9;
        ArrayList<ItemStack> items = chooseItems(tier); //TODO change slot size if for some reason we decide to let there be more than 9 items per crate
        Inventory inventory = Bukkit.createInventory(null, slotSize, tier.getName());
        for (int i = 0; i < slotSize; i++) {
            if (i >= items.size())
                break;
            ItemStack item = items.get(i);
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(0, "Special");
                meta.setLore(lore);
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            }
        }
        SpecialInventory sInventory = new SpecialInventory();
        sInventory.inventory = inventory;
        INSTANCERS.put(block, sInventory);
        return sInventory;
    }

    private static ArrayList<ItemStack> chooseItems(InventoryTiers tier) {
        ArrayList<ItemStack> items = new ArrayList<>();
        //TODO add items to this list based on the tier
        //TODO: make the items have a certain probability of showing up for a certain tier and such
        return items;
    }

    public static SpecialInventory from(FallingBlock b) {
        if (INSTANCERS.containsKey(b))
            return INSTANCERS.get(b);
        return null;
    }

    public static FallingBlock from(Player p) {
        for (FallingBlock b : INSTANCERS.keySet())
            if (INSTANCERS.get(b).inventory.getViewers().contains(p))
                return b;
        return null;
    }

    public static void tryClose(Player p) {
        FallingBlock close = null;
        for (FallingBlock b : INSTANCERS.keySet()) {
            SpecialInventory i = INSTANCERS.get(b);
            if (i.inventory.getViewers().contains(p)) {
                if (i.isEmpty() && i.inventory.getViewers().size() == 1)
                    close = b;
                break;
            }
        }
        if (close != null) {
            INSTANCERS.remove(close);
            close.remove();
        }
    }

    private boolean isEmpty() {
        if (this.inventory == null)
            return true;
        for (ItemStack i : this.inventory.getContents())
            if (i != null && !i.getType().equals(Material.AIR))
                return false;
        return true;
    }

    public Inventory openFor(Player p) {
        p.openInventory(this.inventory);
        return this.inventory;
    }
}