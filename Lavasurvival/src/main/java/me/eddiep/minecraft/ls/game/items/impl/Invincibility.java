package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.game.status.PlayerStatusManager;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
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
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(new Potion(PotionType.INVISIBILITY).toItemStack(1));
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null)
            tag = nmsStack.getTag();
        tag.setInt("HideFlags", 32);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    @Override
    public String description() {
        return "Become immune to lava/water\nfor " + duration() + " seconds!";
    }

    public abstract int duration();


}
