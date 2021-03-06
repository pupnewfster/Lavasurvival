package me.eddiep.minecraft.ls.game.shop.impl;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.RankManager;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.ranks.UserInfo;
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

@SuppressWarnings({"SameParameterValue", "unused"})
@MenuInventory(slots = 18, name = "Elder Block Shop")
public class ElderBlockShop extends Menu implements BlockShop {
    public ElderBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(slot = 0, item = @ItemStackAnnotation(material = Material.EMERALD, name = ""))
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCategory(player.getMenuManager(), null));
    }

    @MenuItem(slot = 1, item = @ItemStackAnnotation(material = Material.GLOWSTONE, name = ""))
    public void buyGlowstone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.GLOWSTONE, price(Material.GLOWSTONE));
    }

    @MenuItem(slot = 2, item = @ItemStackAnnotation(material = Material.NETHER_FENCE, name = ""))
    public void buyNetherFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHER_FENCE, price(Material.NETHER_FENCE));
    }

    @MenuItem(slot = 3, item = @ItemStackAnnotation(material = Material.NETHERRACK, name = ""))
    public void buyNetherrack(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHERRACK, price(Material.NETHERRACK));
    }

    @MenuItem(slot = 4, item = @ItemStackAnnotation(material = Material.NETHER_BRICK, name = ""))
    public void buyNetherBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHER_BRICK, price(Material.NETHER_BRICK));
    }

    @MenuItem(slot = 5, item = @ItemStackAnnotation(material = Material.RED_NETHER_BRICK, name = ""))
    public void buyRedNetherBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_NETHER_BRICK, price(Material.RED_NETHER_BRICK));
    }

    @MenuItem(slot = 6, item = @ItemStackAnnotation(material = Material.NETHER_BRICK_STAIRS, name = ""))
    public void buyNetherStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHER_BRICK_STAIRS, price(Material.NETHER_BRICK_STAIRS));
    }

    @MenuItem(slot = 7, item = @ItemStackAnnotation(material = Material.STEP, durability = 6, name = ""))
    public void buyNetherBrickSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 6);
    }

    @MenuItem(slot = 8, item = @ItemStackAnnotation(material = Material.MAGMA, name = ""))
    public void buyMagmaBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.MAGMA, price(Material.MAGMA));
    }

    @MenuItem(slot = 9, item = @ItemStackAnnotation(material = Material.PURPUR_BLOCK, name = ""))
    public void buyPurPurBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PURPUR_BLOCK, price(Material.PURPUR_BLOCK));
    }

    @MenuItem(slot = 10, item = @ItemStackAnnotation(material = Material.PURPUR_PILLAR, name = ""))
    public void buyPurPurPillar(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PURPUR_PILLAR, price(Material.PURPUR_PILLAR));
    }

    @MenuItem(slot = 11, item = @ItemStackAnnotation(material = Material.PURPUR_STAIRS, name = ""))
    public void buyPurPurStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PURPUR_STAIRS, price(Material.PURPUR_STAIRS));
    }

    @MenuItem(slot = 12, item = @ItemStackAnnotation(material = Material.PURPUR_SLAB, name = ""))
    public void buyPurPurSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PURPUR_SLAB, price(Material.PURPUR_SLAB));
    }

    @MenuItem(slot = 13, item = @ItemStackAnnotation(material = Material.ENDER_STONE, name = ""))
    public void buyEndstone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ENDER_STONE, price(Material.ENDER_STONE));
    }

    @MenuItem(slot = 14, item = @ItemStackAnnotation(material = Material.END_BRICKS, name = ""))
    public void buyEndBricks(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.END_BRICKS, price(Material.END_BRICKS));
    }

    @MenuItem(slot = 15, item = @ItemStackAnnotation(material = Material.END_ROD, name = ""))
    public void buyEndRod(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.END_ROD, price(Material.END_ROD));
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Necessities.getRM();
        if (rm.hasRank(Necessities.getUM().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Elder")))
            return true;
        else {
            player.getBukkit().sendMessage(ChatColor.RED + "You must be Elder or higher to purchase from this shop.");
            return false;
        }
    }

    @PreProcessor
    public void process(Inventory inv) {
        setupInventory(inv);
    }

    public int price(Material type) {
        switch (type) {
            case GLOWSTONE:
                return 3250;
            case NETHER_FENCE:
                return 3500;
            case NETHERRACK:
                return 2500;
            case NETHER_BRICK:
                return 3500;
            case RED_NETHER_BRICK:
                return 3500;
            case NETHER_BRICK_STAIRS:
                return 3500;
            case STEP:
                return 3500;
            case MAGMA:
                return 2500;
            case PURPUR_BLOCK:
                return 3500;
            case PURPUR_PILLAR:
                return 3500;
            case PURPUR_STAIRS:
                return 3500;
            case PURPUR_SLAB:
                return 3500;
            case ENDER_STONE:
                return 5000;
            case END_BRICKS:
                return 5000;
            case END_ROD:
                return 5000;
            default:
                return 0;
        }
    }
}