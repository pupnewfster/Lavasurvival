package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.LavaItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public abstract class Heal extends LavaItem {
    @Override
    public boolean consume(Player owner) {
        if (owner.getHealth() == owner.getMaxHealth()) {
            owner.sendMessage(ChatColor.DARK_RED + "You are already at full health");
            return false;
        }

        double health = owner.getHealth();
        double healAdd = owner.getMaxHealth() * getPercent();
        owner.setHealth(health + healAdd);

        return true;
    }

    @Override
    protected ItemStack displayItem() {
        Potion potion = new Potion(PotionType.INSTANT_HEAL);
        return potion.toItemStack(1);
    }

    @Override
    public String description() {
        return "Heal " + (getPercent() * 100) + "% of your total health";
    }

    public abstract double getPercent();
}
