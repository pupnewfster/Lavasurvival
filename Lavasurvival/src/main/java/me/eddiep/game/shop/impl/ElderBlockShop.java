package me.eddiep.game.shop.impl;

import me.eddiep.Lavasurvival;
import me.eddiep.ranks.UserInfo;
import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.player.MenuPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@MenuInventory(slots = 9, name = "Elder Block Shop")
public class ElderBlockShop extends Menu {
    public ElderBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = (Material.EMERALD), name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCatagory(player.getMenuManager(), null, player.getBukkit()));
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = (Material.GLOWSTONE), name = "Glowstone", lore = {"580 ggs"})
    )
    public void buyGlowstone(MenuPlayer player) {
        getUser(player).buyBlock(Material.GLOWSTONE, 580);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.NETHER_FENCE), name = "Nether brick fence", lore = {"720 ggs"})
    )
    public void buyNetherFence(MenuPlayer player) {
        getUser(player).buyBlock(Material.NETHER_FENCE, 720);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.NETHERRACK), name = "Netherrack", lore = {"720 ggs"})
    )
    public void buyNetherrack(MenuPlayer player) {
        getUser(player).buyBlock(Material.NETHERRACK, 720);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.NETHER_BRICK_STAIRS), name = "Nether brick stairs", lore = {"730 ggs"})
    )
    public void buyNetherStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.NETHER_BRICK_STAIRS, 730);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = (Material.NETHER_BRICK), name = "Nether brick", lore = {"900 ggs"})
    )
    public void buyNetherBrick(MenuPlayer player) {
        getUser(player).buyBlock(Material.NETHER_BRICK, 900);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = (Material.STEP), durability = 6, name = "Nether brick slab", lore = {"900 ggs"})
    )
    public void buyNetherBrickSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.STEP, 900, (byte) 6);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = (Material.ENDER_STONE), name = "Endstone", lore = {"1100 ggs"})
    )
    public void buyEndstone(MenuPlayer player) {
        getUser(player).buyBlock(Material.ENDER_STONE, 1100);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }
}