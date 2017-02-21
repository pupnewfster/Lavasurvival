package me.eddiep.minecraft.ls.system;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Objects;

public class BukkitUtils {
    @SuppressWarnings("unused")
    public static boolean isInventoryEmpty(Inventory inv) {
        return Arrays.stream(inv.getContents()).noneMatch(Objects::nonNull);
    }

    public static boolean isInventoryFull(Inventory inv) {//Does this check armor slots if so it might need to change a little
        return inv.firstEmpty() == -1;
    }

    public static boolean hasItem(Inventory inventory, MaterialData dat) {
        if (inventory instanceof PlayerInventory) {
            PlayerInventory pInv = (PlayerInventory) inventory;
            ItemStack[] armor = pInv.getArmorContents();
            for (ItemStack stack : armor)
                if (stack != null && stack.getData().equals(dat))
                    return true;
            ItemStack offhand = pInv.getItemInOffHand();
            if (offhand != null && offhand.getData().equals(dat))
                return true;
        }
        for (ItemStack stack : inventory)
            if (stack != null && stack.getData().equals(dat))
                return true;
        return false;
    }

    public static boolean hasItem(Inventory inventory, ItemStack item) {
        return hasItem(inventory, item.getData());
    }
}