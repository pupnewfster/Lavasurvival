package me.eddiep.minecraft.ls.game.items;

import me.eddiep.minecraft.ls.game.items.impl.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class LavaItem {

    public static final LavaItem[] ITEMS = new LavaItem[] {
            new MinorHeal(),
            new Generosity(),
            new MajorHeal(),
            new MinorInvincibility(),
            new MajorInvincibility()
    };
    public static final LavaItem MINOR_HEAL = ITEMS[0];
    public static final LavaItem GENEROSITY = ITEMS[1];
    public static final LavaItem MAJOR_HEAL = ITEMS[2];
    public static final LavaItem MINOR_INVINCIBILITY = ITEMS[3];
    public static final LavaItem MAJOR_INVINCIBILITY = ITEMS[4];

    public abstract boolean consume(Player owner);

    protected abstract ItemStack displayItem();

    public abstract String name();

    public abstract String description();

    public ItemStack createItem() {
        ItemStack item = displayItem();
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name());

        List<String> loreLines = Arrays.asList(description().split("\n"));
        meta.setLore(loreLines);

        item.setItemMeta(meta);

        return item;
    }

    public boolean isItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        return meta.getDisplayName().equals(name());
    }

    public boolean giveItem(Player player) {
        ItemStack item = createItem();

        Inventory inventory = player.getInventory();
        int index = inventory.firstEmpty();
        if (index == -1)
            return false;

        inventory.setItem(index, item);
        return true;
    }
}
