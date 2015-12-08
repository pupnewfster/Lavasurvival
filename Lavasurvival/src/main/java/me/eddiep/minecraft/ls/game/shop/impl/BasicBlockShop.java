package me.eddiep.minecraft.ls.game.shop.impl;

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
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@MenuInventory(slots = 27, name = "Basic Block Shop")
public class BasicBlockShop extends Menu {
    public BasicBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = Material.EMERALD, name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenuAndReplace(new BlockShopCatagory(player.getMenuManager(), null, player.getBukkit()), true);
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = Material.GRAVEL, name = "Gravel")
    )
    public void buyGravel(MenuPlayer player) {
        getUser(player).buyBlock(Material.GRAVEL, price(Material.GRAVEL));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.STONE, name = "Stone")
    )
    public void buyStone(MenuPlayer player) {
        getUser(player).buyBlock(Material.STONE, price(Material.STONE));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.LOG, durability = 0, name = "Oak log")
    )
    public void buyOakLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 0);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.LOG, durability = 1, name = "Spruce log")
    )
    public void buySpruceLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 1);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.LOG, durability = 2, name = "Birch log")
    )
    public void buyBirchLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 2);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.LOG, durability = 3, name = "Jungle log")
    )
    public void buyJungleLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 3);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = Material.LOG_2, durability = 0, name = "Acacia log")
    )
    public void buyAcaciaLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG_2, price(Material.LOG_2), (byte) 0);
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = Material.LOG_2, durability = 1, name = "Dark oak log")
    )
    public void buyDarkOakLog(MenuPlayer player) {
        getUser(player).buyBlock(Material.LOG_2, price(Material.LOG_2), (byte) 1);
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = Material.SANDSTONE, name = "Sandstone")
    )
    public void buySandstone(MenuPlayer player) {
        getUser(player).buyBlock(Material.SANDSTONE, price(Material.SANDSTONE));
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = Material.HARD_CLAY, name = "Hardened clay")
    )
    public void buyHardClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.HARD_CLAY, price(Material.HARD_CLAY));
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 0, name = "White stained clay")
    )
    public void buyWhiteClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 0);
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 1, name = "Orange stained clay")
    )
    public void buyOrangeClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 1);
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 2, name = "Magenta stained clay")
    )
    public void buyMagentaClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 2);
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 3, name = "Light blue stained clay")
    )
    public void buyLightBlueClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 3);
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 4, name = "Yellow stained clay")
    )
    public void buyYellowClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 4);
    }

    @MenuItem(
            slot = 16,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 5, name = "Lime stained clay")
    )
    public void buyLimeClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 5);
    }

    @MenuItem(
            slot = 17,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 6, name = "Pink stained clay")
    )
    public void buyPinkClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 6);
    }

    @MenuItem(
            slot = 18,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 7, name = "Gray stained clay")
    )
    public void buyGrayClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 7);
    }

    @MenuItem(
            slot = 19,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 8, name = "Light gray stained clay")
    )
    public void buyLightGrayClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 8);
    }

    @MenuItem(
            slot = 20,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 9, name = "Cyan stained clay")
    )
    public void buyCyanClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 9);
    }

    @MenuItem(
            slot = 21,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 10, name = "Purple stained clay")
    )
    public void buyPurpleClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 10);
    }

    @MenuItem(
            slot = 22,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 11, name = "Blue stained clay")
    )
    public void buyBlueClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 11);
    }

    @MenuItem(
            slot = 23,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 12, name = "Brown staied clay")
    )
    public void buyBrownClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 12);
    }

    @MenuItem(
            slot = 24,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 13, name = "Green stained clay")
    )
    public void buyGreenClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 13);
    }

    @MenuItem(
            slot = 25,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 14, name = "Red stained clay")
    )
    public void buyRedClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 14);
    }

    @MenuItem(
            slot = 26,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 15, name = "Black stained clay")
    )
    public void buyBlackClay(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 15);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    @PreProcessor
    public void process(Inventory inv){
        for (int i = 1; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is == null)
                continue;
            ItemMeta m = is.getItemMeta();
            m.setLore(Arrays.asList(price(is.getType()) + " ggs", "Melt time: " + PhysicsListener.getMeltTime(is.getData()) + " seconds"));
            is.setItemMeta(m);
            inv.setItem(i, is);
        }
    }

    protected int price(Material type) {
        switch (type) {
            case GRAVEL:
                return 75;
            case STONE:
                return 100;
            case LOG:
                return 135;
            case LOG_2:
                return 135;
            case SANDSTONE:
                return 180;
            case HARD_CLAY:
                return 185;
            case STAINED_CLAY:
                return 185;
            default:
                return 0;
        }
    }
}