package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SecondChance extends LavaItem {
    @Override
    public boolean consume(Player owner) {
        PlayerStatusManager.makeInvincible(owner, 5);
        owner.sendMessage(ChatColor.GREEN + "You are now invincible for 5 seconds!");
        return true;
    }

    @Override
    protected ItemStack displayItem() {
        return ShopFactory.addGlow(new ItemStack(Material.TOTEM));
    }

    @Override
    public String name() {
        return "Second Chance";
    }

    @Override
    protected String description() {
        return "Hold this item in your offhand\nand when you die you will become\ninvincible for 5 seconds instead.";
    }

    @Override
    public int getPrice() {
        return 0;
    }
}