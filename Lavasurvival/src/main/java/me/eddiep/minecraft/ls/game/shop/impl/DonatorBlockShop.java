package me.eddiep.minecraft.ls.game.shop.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.system.PhysicsListener;
import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.annotation.PreProcessor;
import net.njay.player.MenuPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@MenuInventory(slots = 54, name = "Donator Block Shop")
public class DonatorBlockShop extends Menu {
    public DonatorBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = Material.EMERALD, name = "")
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCategory(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = Material.WOOD, durability = 1, name = "")
    )
    public void buySprucePlanks(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD, price(Material.WOOD), (byte) 1);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.WOOD, durability = 2, name = "")
    )
    public void buyBirchPlanks(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD, price(Material.WOOD), (byte) 2);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.WOOD, durability = 3, name = "")
    )
    public void buyJunglePlanks(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD, price(Material.WOOD), (byte) 3);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.WOOD, durability = 4, name = "")
    )
    public void buyAcaciaPlanks(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD, price(Material.WOOD), (byte) 4);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.WOOD, durability = 5, name = "")
    )
    public void buyDarkOakPlanks(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD, price(Material.WOOD), (byte) 5);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.SAND, durability = 1, name = "")
    )
    public void buyRedSand(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SAND, price(Material.SAND), (byte) 1);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = Material.SPRUCE_FENCE, name = "")
    )
    public void buySpruceFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SPRUCE_FENCE, price(Material.SPRUCE_FENCE));
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = Material.BIRCH_FENCE, name = "")
    )
    public void buyBirchFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BIRCH_FENCE, price(Material.BIRCH_FENCE));
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = Material.JUNGLE_FENCE, name = "")
    )
    public void buyJungleFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.JUNGLE_FENCE, price(Material.JUNGLE_FENCE));
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = Material.DARK_OAK_FENCE, name = "")
    )
    public void buyDarkOakFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.DARK_OAK_FENCE, price(Material.DARK_OAK_FENCE));
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = Material.ACACIA_FENCE, name = "")
    )
    public void buyAcaciaFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ACACIA_FENCE, price(Material.ACACIA_FENCE));
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = Material.FENCE_GATE, name = "")
    )
    public void buyFenceGate(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.FENCE_GATE, price(Material.FENCE_GATE));
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = Material.SPRUCE_FENCE_GATE, name = "")
    )
    public void buySpruceFenceGate(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SPRUCE_FENCE_GATE, price(Material.SPRUCE_FENCE_GATE));
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = Material.BIRCH_FENCE_GATE, name = "")
    )
    public void buyBirchFenceGate(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BIRCH_FENCE_GATE, price(Material.BIRCH_FENCE_GATE));
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = Material.JUNGLE_FENCE_GATE, name = "")
    )
    public void buyJungleFenceGate(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.JUNGLE_FENCE_GATE, price(Material.JUNGLE_FENCE_GATE));
    }

    @MenuItem(
            slot = 16,
            item = @ItemStackAnnotation(material = Material.DARK_OAK_FENCE_GATE, name = "")
    )
    public void buyDarkOakFenceGate(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.DARK_OAK_FENCE_GATE, price(Material.DARK_OAK_FENCE_GATE));
    }

    @MenuItem(
            slot = 17,
            item = @ItemStackAnnotation(material = Material.ACACIA_FENCE_GATE, name = "")
    )
    public void buyAcaciaFenceGate(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ACACIA_FENCE_GATE, price(Material.ACACIA_FENCE_GATE));
    }

    @MenuItem(
            slot = 18,
            item = @ItemStackAnnotation(material = Material.SPRUCE_DOOR_ITEM, name = "")
    )
    public void buySpruceDoor(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SPRUCE_DOOR_ITEM, price(Material.SPRUCE_DOOR_ITEM));
    }

    @MenuItem(
            slot = 19,
            item = @ItemStackAnnotation(material = Material.BIRCH_DOOR_ITEM, name = "")
    )
    public void buyBirchDoor(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BIRCH_DOOR_ITEM, price(Material.BIRCH_DOOR_ITEM));
    }

    @MenuItem(
            slot = 20,
            item = @ItemStackAnnotation(material = Material.JUNGLE_DOOR_ITEM, name = "")
    )
    public void buyJungleDoor(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.JUNGLE_DOOR_ITEM, price(Material.JUNGLE_DOOR_ITEM));
    }

    @MenuItem(
            slot = 21,
            item = @ItemStackAnnotation(material = Material.DARK_OAK_DOOR_ITEM, name = "")
    )
    public void buyDarkOakDoor(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.DARK_OAK_DOOR_ITEM, price(Material.DARK_OAK_DOOR_ITEM));
    }

    @MenuItem(
            slot = 22,
            item = @ItemStackAnnotation(material = Material.ACACIA_DOOR_ITEM, name = "")
    )
    public void buyAcaciaDoor(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ACACIA_DOOR_ITEM, price(Material.ACACIA_DOOR_ITEM));
    }

    @MenuItem(
            slot = 23,
            item = @ItemStackAnnotation(material = Material.SEA_LANTERN, name = "")
    )
    public void buySeaLantern(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SEA_LANTERN, price(Material.SEA_LANTERN));
    }

    @MenuItem(
            slot = 24,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 0, name = "")
    )
    public void buyWhiteCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 0);
    }

    @MenuItem(
            slot = 25,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 1, name = "")
    )
    public void buyOrangeCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 1);
    }

    @MenuItem(
            slot = 26,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 2, name = "")
    )
    public void buyMagentaCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 2);
    }

    @MenuItem(
            slot = 27,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 3, name = "")
    )
    public void buyLightBlueCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 3);
    }

    @MenuItem(
            slot = 28,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 4, name = "")
    )
    public void buyYellowCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 4);
    }

    @MenuItem(
            slot = 29,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 5, name = "")
    )
    public void buyLimeCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 5);
    }

    @MenuItem(
            slot = 30,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 6, name = "")
    )
    public void buyPinkCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 6);
    }

    @MenuItem(
            slot = 31,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 7, name = "")
    )
    public void buyGrayCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 7);
    }

    @MenuItem(
            slot = 32,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 8, name = "")
    )
    public void buyLightGrayCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 8);
    }

    @MenuItem(
            slot = 33,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 9, name = "")
    )
    public void buyCyanCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 9);
    }

    @MenuItem(
            slot = 34,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 10, name = "")
    )
    public void buyPurpleCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 10);
    }

    @MenuItem(
            slot = 35,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 11, name = "")
    )
    public void buyBlueCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 11);
    }

    @MenuItem(
            slot = 36,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 12, name = "")
    )
    public void buyBrownCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 12);
    }

    @MenuItem(
            slot = 37,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 13, name = "")
    )
    public void buyGreenCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 13);
    }

    @MenuItem(
            slot = 38,
            item = @ItemStackAnnotation(material = Material.CARPET, durability = 14, name = "")
    )
    public void buyRedCarpet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CARPET, price(Material.CARPET), (byte) 14);
    }

    @MenuItem(
            slot = 39,
            item = @ItemStackAnnotation(material = Material.FLOWER_POT_ITEM, name = "")
    )
    public void buyFlowerpot(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.FLOWER_POT_ITEM, price(Material.FLOWER_POT_ITEM));
    }

    @MenuItem(
            slot = 40,
            item = @ItemStackAnnotation(material = Material.BOOKSHELF, name = "")
    )
    public void buyBookshelf(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BOOKSHELF, price(Material.BOOKSHELF));
    }

    @MenuItem(
            slot = 41,
            item = @ItemStackAnnotation(material = Material.YELLOW_FLOWER, name = "")
    )
    public void buyDandelion(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.YELLOW_FLOWER, price(Material.YELLOW_FLOWER));
    }

    @MenuItem(
            slot = 42,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 0, name = "")
    )
    public void buyPoppy(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 0);
    }

    @MenuItem(
            slot = 43,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 1, name = "")
    )
    public void buyBlueOrchid(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 1);
    }

    @MenuItem(
            slot = 44,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 2, name = "")
    )
    public void buyAllium(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 2);
    }

    @MenuItem(
            slot = 45,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 3, name = "")
    )
    public void buyAzureBluet(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 3);
    }

    @MenuItem(
            slot = 46,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 4, name = "")
    )
    public void buyRedTulip(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 4);
    }

    @MenuItem(
            slot = 47,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 5, name = "")
    )
    public void buyOrangeTulip(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 5);
    }

    @MenuItem(
            slot = 48,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 6, name = "")
    )
    public void buyWhiteTulip(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 6);
    }

    @MenuItem(
            slot = 49,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 7, name = "")
    )
    public void buyPinkTulip(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 7);
    }

    @MenuItem(
            slot = 50,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, durability = 8, name = "")
    )
    public void buyOxeyeDaisy(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE), (byte) 8);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        return !(player == null || player.getBukkit() == null) && player.getBukkit().hasPermission("lavasurvival.donator");
    }

    @PreProcessor
    public void process(Inventory inv) {
        ItemStack stack = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Back to block shop");
        meta.setLore(Arrays.asList(ChatColor.GOLD + "" + ChatColor.ITALIC + "Buy more blocks!"));
        stack.setItemMeta(meta);
        stack = ShopFactory.addGlow(stack);
        inv.setItem(0, stack);
        for (int i = 1; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is == null)
                continue;
            ItemMeta m = is.getItemMeta();
            m.setLore(Arrays.asList(price(is.getType()) + " ggs", "Melt time: " + PhysicsListener.getMeltTimeAsString(is.getData())));
            is.setItemMeta(m);
            inv.setItem(i, is);
        }
    }

    protected int price(Material type) {
        switch (type) {
            case WOOD:
                return 1500;
            case SAND:
                return 1500;
            case SPRUCE_FENCE:
                return 1500;
            case BIRCH_FENCE:
                return 1500;
            case JUNGLE_FENCE:
                return 1500;
            case DARK_OAK_FENCE:
                return 1500;
            case ACACIA_FENCE:
                return 1500;
            case FENCE_GATE:
                return 1500;
            case SPRUCE_FENCE_GATE:
                return 1500;
            case BIRCH_FENCE_GATE:
                return 1500;
            case JUNGLE_FENCE_GATE:
                return 1500;
            case DARK_OAK_FENCE_GATE:
                return 1500;
            case ACACIA_FENCE_GATE:
                return 1500;
            case SPRUCE_DOOR_ITEM:
                return 1500;
            case BIRCH_DOOR_ITEM:
                return 1500;
            case JUNGLE_DOOR_ITEM:
                return 1500;
            case DARK_OAK_DOOR_ITEM:
                return 1500;
            case ACACIA_DOOR_ITEM:
                return 1500;
            case SEA_LANTERN:
                return 1500;
            case CARPET:
                return 1500;
            case FLOWER_POT_ITEM:
                return 1500;
            case BOOKSHELF:
                return 1500;
            case YELLOW_FLOWER:
                return 1500;
            case RED_ROSE:
                return 1500;
            default:
                return 0;
        }
    }
}