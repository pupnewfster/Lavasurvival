package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.LavaItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class MinorHeal extends Heal {
    @Override
    public double getPercent() {
        return 0.25;
    }

    @Override
    public String name() {
        return "Minor Heal";
    }
}
