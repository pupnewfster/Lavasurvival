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

@MenuInventory(slots = 27, name = "Basic Block Shop")
public class BasicBlockShop extends Menu {
    public BasicBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = (Material.EMERALD), name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenuAndReplace(new BlockShopCatagory(player.getMenuManager(), null, player.getBukkit()), true);
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
            item = @ItemStackAnnotation(material = (Material.LOG), durability = 0, name = "Oak log", lore = {"135 ggs"})
    )
    public void buyOakLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG, 135, (byte) 0);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.LOG), durability = 1, name = "Spruce log", lore = {"135 ggs"})
    )
    public void buySpruceLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG, 135, (byte) 1);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = (Material.LOG), durability = 2, name = "Birch log", lore = {"135 ggs"})
    )
    public void buyBirchLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG, 135, (byte) 2);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = (Material.LOG), durability = 3, name = "Jungle log", lore = {"135 ggs"})
    )
    public void buyJungleLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG, 135, (byte) 3);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = (Material.LOG_2), durability = 0, name = "Acacia log", lore = {"135 ggs"})
    )
    public void buyAcaciaLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG_2, 135, (byte) 0);
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = (Material.LOG_2), durability = 1, name = "Dark oak log", lore = {"135 ggs"})
    )
    public void buyDarkOakLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG_2, 135, (byte) 1);
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = (Material.SANDSTONE), name = "Sandstone", lore = {"180 ggs"})
    )
    public void buySandstone(MenuPlayer player) {
        getUser(player).buyBlock(Material.SANDSTONE, 180);
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = (Material.HARD_CLAY), name = "Hardened clay", lore = {"185 ggs"})
    )
    public void buyHardClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.HARD_CLAY, 185);
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 0, name = "White stained clay", lore = {"185 ggs"})
    )
    public void buyWhiteClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 0);
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 1, name = "Orange stained clay", lore = {"185 ggs"})
    )
    public void buyOrangeClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 1);
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 2, name = "Magenta stained clay", lore = {"185 ggs"})
    )
    public void buyMagentaClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 2);
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 3, name = "Light blue stained clay", lore = {"185 ggs"})
    )
    public void buyLightBlueClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 3);
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 4, name = "Yellow stained clay", lore = {"185 ggs"})
    )
    public void buyYellowClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 4);
    }

    @MenuItem(
            slot = 16,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 5, name = "Lime stained clay", lore = {"185 ggs"})
    )
    public void buyLimeClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 5);
    }

    @MenuItem(
            slot = 17,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 6, name = "Pink stained clay", lore = {"185 ggs"})
    )
    public void buyPinkClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 6);
    }

    @MenuItem(
            slot = 18,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 7, name = "Gray stained clay", lore = {"185 ggs"})
    )
    public void buyGrayClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 7);
    }

    @MenuItem(
            slot = 19,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 8, name = "Light gray stained clay", lore = {"185 ggs"})
    )
    public void buyLightGrayClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 8);
    }

    @MenuItem(
            slot = 20,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 9, name = "Cyan stained clay", lore = {"185 ggs"})
    )
    public void buyCyanClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 9);
    }

    @MenuItem(
            slot = 21,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 10, name = "Purple stained clay", lore = {"185 ggs"})
    )
    public void buyPurpleClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 10);
    }

    @MenuItem(
            slot = 22,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 11, name = "Blue stained clay", lore = {"185 ggs"})
    )
    public void buyBlueClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 11);
    }

    @MenuItem(
            slot = 23,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 12, name = "Brown staied clay", lore = {"185 ggs"})
    )
    public void buyBrownClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 12);
    }

    @MenuItem(
            slot = 24,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 13, name = "Green stained clay", lore = {"185 ggs"})
    )
    public void buyGreenClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 13);
    }

    @MenuItem(
            slot = 25,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 14, name = "Red stained clay", lore = {"185 ggs"})
    )
    public void buyRedClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 14);
    }

    @MenuItem(
            slot = 26,
            item = @ItemStackAnnotation(material = (Material.STAINED_CLAY), durability = 15, name = "Black stained clay", lore = {"185 ggs"})
    )
    public void buyBlackClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, 185, (byte) 15);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }
}