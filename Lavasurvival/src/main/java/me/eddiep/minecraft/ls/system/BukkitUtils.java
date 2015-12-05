package me.eddiep.minecraft.ls.system;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class BukkitUtils {
    public static boolean isInventoryEmpty(Inventory inv) {
        for (ItemStack item : inv.getContents())
            if (item != null)
                return false;
        return true;
    }

    public static boolean isInventoryFull(Inventory inv) {
        return inv.firstEmpty() == -1;
    }

    public static boolean hasItem(Inventory inventory, MaterialData dat) {
        for (ItemStack stack : inventory) {
            if (stack.getData().equals(dat))
                return true;
        }

        return false;
    }

    public static boolean hasItem(Inventory inventory, ItemStack item) {
        return hasItem(inventory, item.getData());
    }
}