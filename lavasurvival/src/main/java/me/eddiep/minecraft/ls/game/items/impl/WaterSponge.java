package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.LavaItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WaterSponge extends LavaItem {
    @Override
    public boolean consume(Player owner) {
        return false; //This item can't be consumed
    }

    @Override
    protected ItemStack displayItem() {
        ItemStack item = new ItemStack(Material.SPONGE);
        item.setDurability((short) 0);
        return item;
    }

    @Override
    public String name() {
        return "Water Sponge";
    }

    @Override
    protected String description() {
        return "Absorb water in a 5 block radius for 15 seconds";
    }

    @Override
    public int getPrice() {
        return 600;
    }
}
