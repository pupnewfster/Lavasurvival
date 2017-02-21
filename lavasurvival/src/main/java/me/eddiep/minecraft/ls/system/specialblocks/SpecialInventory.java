package me.eddiep.minecraft.ls.system.specialblocks;

import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.items.Intrinsic;
import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.ranks.RankType;
import me.eddiep.minecraft.ls.system.PhysicsListener;
import me.eddiep.minecraft.ls.system.util.ArrayHelper;
import me.eddiep.minecraft.ls.system.util.RandomHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static me.eddiep.minecraft.ls.system.util.RandomDistribution.NEGATIVE_EXPONENTIAL;
import static me.eddiep.minecraft.ls.system.util.RandomDistribution.POSITIVE_EXPONENTIAL;
import static me.eddiep.minecraft.ls.system.util.RandomHelper.random;

public class SpecialInventory {
    private static final HashMap<FallingBlock, SpecialInventory> INSTANCERS = new HashMap<>();

    private Inventory inventory;

    private SpecialInventory() {
    }

    public static SpecialInventory create(FallingBlock block, Intrinsic tier) {
        int slotSize = 9;
        ArrayList<ItemStack> items = chooseItems(tier);
        Inventory inventory = Bukkit.createInventory(null, slotSize, tier.name());
        for (int i = 0; i < slotSize; i++) {
            if (i >= items.size())
                break;
            ItemStack item = items.get(i).clone();
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(0, "Special");
                meta.setLore(lore);
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            }
        }
        SpecialInventory sInventory = new SpecialInventory();
        sInventory.inventory = inventory;
        INSTANCERS.put(block, sInventory);
        return sInventory;
    }

    private static List<ItemStack> chooseBlocks(Intrinsic tier) {
        List<MaterialData> temp;
        switch (tier) {
            case COMMON:
                temp = new ArrayList<>();
                break;
            case UNCOMMON:
                temp = ArrayHelper.combind(
                        ShopFactory.getBlocksFor(RankType.SURVIVOR),
                        ShopFactory.getBlocksFor(RankType.TRUSTED)
                );
                break;
            case EPIC:
                temp = ArrayHelper.combind(
                        ShopFactory.getBlocksFor(RankType.SURVIVOR),
                        ShopFactory.getBlocksFor(RankType.TRUSTED)
                );
                break;
            default:
                temp = new ArrayList<>();
                break;
        }
        return ArrayHelper.transform(temp, m -> addMeltTime(m.toItemStack(1)));
    }

    private static ItemStack addMeltTime(ItemStack is) { //TODO maybe move this into some utils class
        if (is != null) {
            MaterialData dat = is.getData();
            if (!dat.getItemType().equals(Material.AIR)) {
                ItemMeta im = is.getItemMeta();
                im.setLore(Arrays.asList("Lava MeltTime: " + PhysicsListener.getLavaMeltTimeAsString(dat), "Water MeltTime: " + PhysicsListener.getWaterMeltTimeAsString(dat)));
                is.setItemMeta(im);
            }
        }
        return is;
    }

    private static ArrayList<ItemStack> chooseItems(Intrinsic tier) {
        ArrayList<ItemStack> items = new ArrayList<>();
        List<ItemStack> possibleItems = ArrayHelper.combind(LavaItem.filter(tier), chooseBlocks(tier));
        List<ItemStack> uncommon;
        uncommon = tier != Intrinsic.UNCOMMON ? ArrayHelper.combind(LavaItem.filter(Intrinsic.UNCOMMON), chooseBlocks(Intrinsic.UNCOMMON)) : possibleItems;

        switch (tier) {
            case COMMON: { //MAX of 9 common items
                int itemCount = random(3, 9, POSITIVE_EXPONENTIAL);
                for (int i = 0; i < itemCount; i++)
                    items.add(possibleItems.get(random(possibleItems.size())));

                if (itemCount < 7 && RandomHelper.negativeExponentialRandom() > 0.5) {
                    //Add a uncommon item
                    ItemStack uncommonItem = ArrayHelper.random(uncommon);
                    items.add(uncommonItem);
                }
                break;
            }
            case UNCOMMON: { //MAX of 5 uncommon items and 4 common items
                int itemCount = random(2, 5, NEGATIVE_EXPONENTIAL);
                for (int i = 0; i < itemCount; i++)
                    items.add(possibleItems.get(random(possibleItems.size())));

                int commonCount = random(1, 4);
                possibleItems = LavaItem.filter(Intrinsic.COMMON);
                for (int i = 0; i < commonCount; i++)
                    items.add(possibleItems.get(random(possibleItems.size())));
                break;
            }
            case EPIC: { //MAX of 3 epic items and 3 uncommon items
                int itemCount = random(1, 3, NEGATIVE_EXPONENTIAL);
                for (int i = 0; i < itemCount; i++)
                    items.add(possibleItems.get(random(possibleItems.size())));

                int uncommonCount = random(1, 3, NEGATIVE_EXPONENTIAL);
                for (int i = 0; i < uncommonCount; i++)
                    items.add(uncommon.get(random(uncommon.size())));
                break;
            }
        }
        return items;
    }

    public static SpecialInventory from(FallingBlock b) {
        if (INSTANCERS.containsKey(b))
            return INSTANCERS.get(b);
        return null;
    }

    public static FallingBlock from(Player p) {
        return INSTANCERS.keySet().stream().filter(b -> INSTANCERS.get(b).inventory.getViewers().contains(p)).findFirst().orElse(null);
    }

    public static void tryClose(Player p) {
        FallingBlock close = null;
        for (FallingBlock b : INSTANCERS.keySet()) {
            SpecialInventory i = INSTANCERS.get(b);
            if (i.inventory.getViewers().contains(p)) {
                if (i.isEmpty() && i.inventory.getViewers().size() == 1)
                    close = b;
                break;
            }
        }
        if (close != null) {
            INSTANCERS.remove(close);
            if (Gamemode.getScoreboard() != null)
                Gamemode.getScoreboard().getTeam("Special").removeEntry(close.getUniqueId().toString());
            close.remove();
        }
    }

    private boolean isEmpty() {
        return this.inventory == null || Arrays.stream(this.inventory.getContents()).noneMatch(i -> i != null && !i.getType().equals(Material.AIR));
    }

    public Inventory openFor(Player p) {
        p.openInventory(this.inventory);
        return this.inventory;
    }
}