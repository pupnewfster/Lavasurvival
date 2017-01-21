package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.Intrinsic;
import me.eddiep.minecraft.ls.game.items.LavaItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EpicSponge extends LavaItem {
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
        return "Epic Sponge";
    }

    @Override
    protected String description() {
        return "Absorb water AND lava in a\n10 block radius for 25 seconds";
    }


    @Override
    public Intrinsic intrinsic() {
        return Intrinsic.EPIC;
    }

    @Override
    public int getPrice() {
        return 1600;
    }
}