package me.eddiep.minecraft.ls.game.shop.impl;

import me.eddiep.handles.PhysicsEngine;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

/**
 * An interface with a default implementations for purposes of "extending" more than one class and removing duplicated code
 */
interface BlockShop {
    default void setupInventory(Inventory inv) {
        ItemStack stack = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Back to block shop");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "" + ChatColor.ITALIC + "Buy more blocks!"));
        stack.setItemMeta(meta);
        stack = ShopFactory.addGlow(stack);
        inv.setItem(0, stack);
        for (int i = 1; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is == null)
                continue;
            try {
                ItemMeta m = is.getItemMeta();
                m.setLore(Arrays.asList(price(is.getType()) + " ggs", "Lava MeltTime: " + PhysicsEngine.getLavaMeltTimeAsString(is.getData()), "Water MeltTime: " + PhysicsEngine.getWaterMeltTimeAsString(is.getData())));
                is.setItemMeta(m);
                inv.setItem(i, is);
            } catch (Exception ignored) {
            }
        }
    }

    int price(Material type);
}