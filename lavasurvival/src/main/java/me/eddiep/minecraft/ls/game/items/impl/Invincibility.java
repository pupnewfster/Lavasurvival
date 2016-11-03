package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Invincibility extends LavaItem {
    @Override
    public boolean consume(Player owner) {
        PlayerStatusManager.makeInvincible(owner, duration());
        owner.sendMessage(ChatColor.GREEN + "You are invincible for " + duration() + " seconds!");
        return true;
    }

    @Override
    protected ItemStack displayItem() {
        net.minecraft.server.v1_10_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(Material.POTION));
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null)
            tag = nmsStack.getTag();
        tag.setInt("HideFlags", 32);
        tag.setString("Potion", "minecraft:invisibility");
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    @Override
    public String description() {
        return "Become immune to lava/water\nfor " + duration() + " seconds!";
    }

    public abstract int duration();
}