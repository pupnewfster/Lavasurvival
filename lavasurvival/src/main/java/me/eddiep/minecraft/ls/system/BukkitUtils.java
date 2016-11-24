package me.eddiep.minecraft.ls.system;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class BukkitUtils {
    @SuppressWarnings("unused")
    public static boolean isInventoryEmpty(Inventory inv) {
        for (ItemStack item : inv.getContents())
            if (item != null)
                return false;
        return true;
    }

    public static boolean isInventoryFull(Inventory inv) {//Does this check armor slots if so it might need to change a little
        return inv.firstEmpty() == -1;
    }

    public static boolean hasItem(Inventory inventory, MaterialData dat) {
        for (ItemStack stack : inventory)
            if (stack != null && stack.getData().equals(dat))
                return true;
        return false;
    }

    public static boolean hasItem(Inventory inventory, ItemStack item) {
        return hasItem(inventory, item.getData());
    }
}