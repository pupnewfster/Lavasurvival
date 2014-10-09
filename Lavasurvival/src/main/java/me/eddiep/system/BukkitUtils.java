package me.eddiep.system;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BukkitUtils {
    public static boolean isInventoryEmpty(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item != null)
                return false;
        }
        return true;
    }
}
