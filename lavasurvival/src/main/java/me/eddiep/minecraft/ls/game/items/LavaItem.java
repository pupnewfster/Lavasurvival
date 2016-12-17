package me.eddiep.minecraft.ls.game.items;

import me.eddiep.minecraft.ls.game.items.impl.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class LavaItem {
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final LavaItem[] ITEMS = new LavaItem[]{
            new Generosity(),
            new MinorHeal(),
            new MajorHeal(),
            new MinorInvincibility(),
            new MajorInvincibility(),
            new SecondChance()
    };
    public static final LavaItem GENEROSITY = ITEMS[0];
    public static final LavaItem MINOR_HEAL = ITEMS[1];
    public static final LavaItem MAJOR_HEAL = ITEMS[2];
    public static final LavaItem MINOR_INVINCIBILITY = ITEMS[3];
    public static final LavaItem MAJOR_INVINCIBILITY = ITEMS[4];
    public static final LavaItem SECOND_CHANCE = ITEMS[5];

    public abstract boolean consume(Player owner);

    protected abstract ItemStack displayItem();

    public abstract String name();

    protected abstract String description();

    public ItemStack createItem() {
        ItemStack item = displayItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name());
        meta.setLore(Arrays.asList((description()).split("\n")));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createItemWithPrice() {
        ItemStack item = displayItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name());
        meta.setLore(Arrays.asList((description() + "\n" + getPrice() + " ggs").split("\n")));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isItem(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(name());
    }

    public abstract int getPrice();
}