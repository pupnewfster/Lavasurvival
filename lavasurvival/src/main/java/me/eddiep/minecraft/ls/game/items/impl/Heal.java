package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.LavaItem;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

abstract class Heal extends LavaItem {
    @Override
    public boolean consume(Player owner) {
        if (owner.getHealth() == owner.getMaxHealth()) {
            owner.sendMessage(ChatColor.DARK_RED + "You are already at full health");
            return false;
        }

        double health = owner.getHealth();
        double healAdd = owner.getMaxHealth() * getPercent();
        double newHealth = Math.min(health + healAdd, owner.getMaxHealth());
        owner.setHealth(newHealth);

        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected ItemStack displayItem() {
        net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(Material.POTION));
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null)
            tag = nmsStack.getTag();
        tag.setInt("HideFlags", 63);
        tag.setString("Potion", "minecraft:healing");
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    @Override
    public String description() {
        return "Heal " + (getPercent() * 100) + "% of your total health";
    }

    protected abstract double getPercent();
}