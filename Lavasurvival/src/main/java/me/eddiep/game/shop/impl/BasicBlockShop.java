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

@MenuInventory(slots = 9, name = "Basic Block Shop")
public class BasicBlockShop extends Menu {
    public BasicBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
        slot = 0,
        item = @ItemStackAnnotation(material = (Material.EMERALD), name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCatagory(player.getMenuManager(), null));
    }

    @MenuItem(
        slot = 1,
        item = @ItemStackAnnotation(material = (Material.GRAVEL), name = "Gravel", lore = {"75 ggs"})
    )
    public void buyGravel(MenuPlayer player) {
        getUser(player).buyBlock(Material.GRAVEL, 75);
    }

    @MenuItem(
        slot = 2,
        item = @ItemStackAnnotation(material = (Material.STONE), name = "Stone", lore = {"100 ggs"})
    )
    public void buyStone(MenuPlayer player) {
        getUser(player).buyBlock(Material.STONE, 100);
    }

    @MenuItem(
        slot = 3,
        item = @ItemStackAnnotation(material = (Material.SANDSTONE), name = "Sandstone", lore = {"180 ggs"})
    )
    public void buySandstone(MenuPlayer player) {
        getUser(player).buyBlock(Material.SANDSTONE, 180);
    }

    @MenuItem(
        slot = 4,
        item = @ItemStackAnnotation(material = (Material.BRICK), name = "Brick", lore = {"200 ggs"})
    )
    public void buyBrick(MenuPlayer player) {
        getUser(player).buyBlock(Material.BRICK, 200);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }
}