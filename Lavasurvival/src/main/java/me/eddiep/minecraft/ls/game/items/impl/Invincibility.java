package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public abstract class Invincibility extends LavaItem {
    @Override
    public boolean consume(Player owner) {
        PlayerStatusManager.makeInvincible(owner, duration());
        owner.sendMessage(ChatColor.GREEN + "You are invincible for " + duration() + " seconds!");

        return true;
    }

    @Override
    protected ItemStack displayItem() {
        Potion potion = new Potion(PotionType.INVISIBILITY);
        return potion.toItemStack(1);
    }

    @Override
    public String description() {
        return "Become immune to lava/water\nfor " + duration() + " seconds!";
    }

    public abstract int duration();
}
