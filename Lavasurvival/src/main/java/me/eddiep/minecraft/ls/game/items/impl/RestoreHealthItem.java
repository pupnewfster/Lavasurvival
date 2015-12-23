package me.eddiep.minecraft.ls.game.items.impl;

import me.eddiep.minecraft.ls.game.items.BaseItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class RestoreHealthItem extends BaseItem {
    @Override
    public void consume(Player owner) {
        double health = owner.getHealth();
        double healAdd = owner.getHealth() * 0.25;
        owner.setHealth(health + healAdd);
    }

    @Override
    protected ItemStack displayItem() {
        Potion potion = new Potion(PotionType.INSTANT_HEAL);
        return potion.toItemStack(1);
    }

    @Override
    public String name() {
        return "Minor Heal";
    }

    @Override
    public String description() {
        return "Heal 25% of your total health";
    }
}
