package me.eddiep.minecraft.ls.game.shop.impl;

import com.crossge.necessities.RankManager.RankManager;
import me.eddiep.minecraft.ls.Lavasurvival;
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

@MenuInventory(slots = 9, name = "Elder Block Shop")
public class ElderBlockShop extends BlockShop {
    public ElderBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = Material.EMERALD, name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCategory(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = Material.GLOWSTONE, name = "Glowstone")
    )
    public void buyGlowstone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.GLOWSTONE, price(Material.GLOWSTONE));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.NETHER_FENCE, name = "Nether brick fence")
    )
    public void buyNetherFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHER_FENCE, price(Material.NETHER_FENCE));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.NETHERRACK, name = "Netherrack")
    )
    public void buyNetherrack(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHERRACK, price(Material.NETHERRACK));
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.NETHER_BRICK_STAIRS, name = "Nether brick stairs")
    )
    public void buyNetherStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHER_BRICK_STAIRS, price(Material.NETHER_BRICK_STAIRS));
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.NETHER_BRICK, name = "Nether brick")
    )
    public void buyNetherBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHER_BRICK, price(Material.NETHER_BRICK));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.STEP, durability = 6, name = "Nether brick slab")
    )
    public void buyNetherBrickSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 6);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = Material.ENDER_STONE, name = "Endstone")
    )
    public void buyEndstone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ENDER_STONE, price(Material.ENDER_STONE));
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Lavasurvival.INSTANCE.getRankManager();
        if (rm.hasRank(Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Elder")))
            return true;
        else {
            player.getBukkit().sendMessage(ChatColor.RED + "You must be Elder or higher to purchase from this shop.");
            return false;
        }
    }

    @Override
    protected int price(Material type) {
        switch (type) {
            case GLOWSTONE:
                return 580;
            case NETHER_FENCE:
                return 720;
            case NETHERRACK:
                return 720;
            case NETHER_BRICK_STAIRS:
                return 730;
            case NETHER_BRICK:
                return 900;
            case STEP:
                return 900;
            case ENDER_STONE:
                return 1100;
            default:
                return 0;
        }
    }
}