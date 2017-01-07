package me.eddiep.minecraft.ls.game.items;

import me.eddiep.minecraft.ls.game.items.impl.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class LavaItem {
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final LavaItem[] ITEMS = new LavaItem[]{
            new Generosity(),
            new MinorHeal(),
            new MajorHeal(),
            new MinorInvincibility(),
            new MajorInvincibility(),
            new SecondChance(),
            new WaterSponge(),
            new LavaSponge(),
            new EpicHeal(),
            new EpicInvincibility(),
            new EpicSponge()
    };
    public static final LavaItem GENEROSITY = ITEMS[0];
    public static final LavaItem MINOR_HEAL = ITEMS[1];
    public static final LavaItem MAJOR_HEAL = ITEMS[2];
    public static final LavaItem MINOR_INVINCIBILITY = ITEMS[3];
    public static final LavaItem MAJOR_INVINCIBILITY = ITEMS[4];
    public static final LavaItem SECOND_CHANCE = ITEMS[5];
    public static final LavaItem WATER_SPONGE = ITEMS[6];
    public static final LavaItem LAVA_SPONGE = ITEMS[7];
    public static final LavaItem EPIC_HEAL = ITEMS[8];
    public static final LavaItem EPIC_INVINCIBLITY = ITEMS[9];
    public static final LavaItem EPIC_SPONGE = ITEMS[10];
    public static final String EPIC_TEXT = "" + ChatColor.BOLD + ChatColor.GREEN + "EPIC" + ChatColor.RESET;

    public abstract boolean consume(Player owner);

    protected abstract ItemStack displayItem();

    public abstract String name();

    protected abstract String description();

    public Intrinsic intrinsic() {
        return Intrinsic.COMMON;
    }

    public ItemStack createItem() {
        ItemStack item = displayItem();
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name());

        List<String> lore = meta.getLore();
        lore.addAll(Arrays.asList(description().split("\n")));
        if (intrinsic() == Intrinsic.EPIC) {
            lore.add(EPIC_TEXT);
        }
        meta.setLore(lore);

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