package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Generosity extends LavaItem {
    UserManager um = new UserManager();
    @Override
    public boolean consume(Player owner) {
        UserInfo info = um.getUser(owner.getUniqueId());

        if (info.wasGenerous()) {
            owner.sendMessage("" + ChatColor.BOLD + ChatColor.RED + "You can only use this item once per round !");
            return false;
        }

        Gamemode gamemode = Gamemode.getCurrentGame();
        if (gamemode.isEndGame()) {
            owner.sendMessage("" + ChatColor.BOLD + ChatColor.RED + "You can't use this item during end-game !");
            return false;
        }

        info.usedGenerosity();

        double bal = Lavasurvival.INSTANCE.getEconomy().getBalance(owner);
        double cost = Math.round(bal * 0.07);
        double bonus = Math.round(bal * 0.05);
        Lavasurvival.INSTANCE.withdrawAndUpdate(owner, cost);
        gamemode.addToBonus(bonus);
        owner.sendMessage("" + ChatColor.ITALIC + ChatColor.RED + cost + "ggs were taken out of your balance!");
        Lavasurvival.globalMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + ChatColor.BOLD + owner.getDisplayName() + ChatColor.RESET + " used the Generosity item to add " + ChatColor.BOLD + bonus + ChatColor.RESET + " to the RoundBonus!");
        return true;
    }

    @Override
    protected ItemStack displayItem() {
        net.minecraft.server.v1_9_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(Material.POTION));
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null)
            tag = nmsStack.getTag();
        tag.setInt("HideFlags", 32);
        tag.setString("Potion", "minecraft:leaping");
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    @Override
    public String name() {
        return "Generosity";
    }

    @Override
    public String description() {
        return "Take 7% of your total money\nand add 5% of it to the current Round Bonus!";
    }

    @Override
    public int getPrice() {
        return 500;
    }
}
