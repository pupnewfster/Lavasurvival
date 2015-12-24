package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.items.LavaItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Generosity extends LavaItem {
    @Override
    public boolean consume(Player owner) {
        Gamemode gamemode = Gamemode.getCurrentGame();
        if (gamemode.isEndGame()) {
            owner.sendMessage("" + ChatColor.BOLD + ChatColor.RED + "You can't use this item during end-game !");
            return false;
        }

        double bal = Lavasurvival.INSTANCE.getEconomy().getBalance(owner);
        double takeOut = bal * 0.05;
        Lavasurvival.INSTANCE.getEconomy().withdrawPlayer(owner, takeOut);
        gamemode.addToBonus(takeOut);
        Lavasurvival.globalMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + ChatColor.BOLD + owner.getDisplayName() + ChatColor.RESET + " used the Generosity item to add " + ChatColor.BOLD + takeOut + ChatColor.RESET + " to the RoundBonus!");
        return true;
    }

    @Override
    protected ItemStack displayItem() {
        Potion potion = new Potion(PotionType.JUMP);
        return potion.toItemStack(1);
    }

    @Override
    public String name() {
        return "Generosity";
    }

    @Override
    public String description() {
        return "Take 5% of your total money and add it to the current Round Bonus!";
    }
}
