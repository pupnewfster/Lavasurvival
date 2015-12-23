package me.eddiep.minecraft.ls.game.items;

import me.eddiep.minecraft.ls.game.items.impl.RestoreHealthItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseItem {

    public static final BaseItem[] ITEMS = new BaseItem[] {
            new RestoreHealthItem()
    };
    public static final BaseItem MINOR_HEAL = ITEMS[0];

    public abstract void consume(Player owner);

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
