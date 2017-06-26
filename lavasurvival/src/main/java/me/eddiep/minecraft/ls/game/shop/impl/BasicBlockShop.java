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
@MenuInventory(slots = 45, name = "Basic Block Shop")
public class BasicBlockShop extends Menu implements BlockShop {
    public BasicBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(slot = 0, item = @ItemStackAnnotation(material = Material.EMERALD, name = ""))
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCategory(player.getMenuManager(), null));
    }

    @MenuItem(slot = 1, item = @ItemStackAnnotation(material = Material.GRAVEL, name = ""))
    public void buyGravel(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.GRAVEL, price(Material.GRAVEL));
    }

    @MenuItem(slot = 2, item = @ItemStackAnnotation(material = Material.STONE, name = ""))
    public void buyStone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STONE, price(Material.STONE));
    }

    @MenuItem(slot = 3, item = @ItemStackAnnotation(material = Material.LOG, name = ""))
    public void buyOakLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 0);
    }

    @MenuItem(slot = 4, item = @ItemStackAnnotation(material = Material.LOG, durability = 1, name = ""))
    public void buySpruceLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 1);
    }

    @MenuItem(slot = 5, item = @ItemStackAnnotation(material = Material.LOG, durability = 2, name = ""))
    public void buyBirchLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 2);
    }

    @MenuItem(slot = 6, item = @ItemStackAnnotation(material = Material.LOG, durability = 3, name = ""))
    public void buyJungleLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 3);
    }

    @MenuItem(slot = 7, item = @ItemStackAnnotation(material = Material.LOG_2, name = ""))
    public void buyAcaciaLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG_2, price(Material.LOG_2), (byte) 0);
    }

    @MenuItem(slot = 8, item = @ItemStackAnnotation(material = Material.LOG_2, durability = 1, name = ""))
    public void buyDarkOakLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG_2, price(Material.LOG_2), (byte) 1);
    }

    @MenuItem(slot = 9, item = @ItemStackAnnotation(material = Material.SANDSTONE, name = ""))
    public void buySandstone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SANDSTONE, price(Material.SANDSTONE));
    }

    @MenuItem(slot = 10, item = @ItemStackAnnotation(material = Material.RED_SANDSTONE, name = ""))
    public void buyRedSandstone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_SANDSTONE, price(Material.RED_SANDSTONE));
    }

    @MenuItem(slot = 11, item = @ItemStackAnnotation(material = Material.HARD_CLAY, name = ""))
    public void buyHardClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.HARD_CLAY, price(Material.HARD_CLAY));
    }

    @MenuItem(slot = 12, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, name = ""))
    public void buyWhiteTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 0);
    }

    @MenuItem(slot = 13, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 1, name = ""))
    public void buyOrangeTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 1);
    }

    @MenuItem(slot = 14, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 2, name = ""))
    public void buyMagentaTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 2);
    }

    @MenuItem(slot = 15, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 3, name = ""))
    public void buyLightBlueTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 3);
    }

    @MenuItem(slot = 16, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 4, name = ""))
    public void buyYellowTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 4);
    }

    @MenuItem(slot = 17, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 5, name = ""))
    public void buyLimeTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 5);
    }

    @MenuItem(slot = 18, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 6, name = ""))
    public void buyPinkTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 6);
    }

    @MenuItem(slot = 19, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 7, name = ""))
    public void buyGrayTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 7);
    }

    @MenuItem(slot = 20, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 8, name = ""))
    public void buyLightGrayTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 8);
    }

    @MenuItem(slot = 21, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 9, name = ""))
    public void buyCyanTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 9);
    }

    @MenuItem(slot = 22, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 10, name = ""))
    public void buyPurpleTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 10);
    }

    @MenuItem(slot = 23, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 11, name = ""))
    public void buyBlueTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 11);
    }

    @MenuItem(slot = 24, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 12, name = ""))
    public void buyBrownTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 12);
    }

    @MenuItem(slot = 25, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 13, name = ""))
    public void buyGreenTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 13);
    }

    @MenuItem(slot = 26, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 14, name = ""))
    public void buyRedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 14);
    }

    @MenuItem(slot = 27, item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 15, name = ""))
    public void buyBlackTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 15);
    }

    @MenuItem(slot = 28, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, name = ""))
    public void buyWhiteConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 0);
    }

    @MenuItem(slot = 29, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 1, name = ""))
    public void buyOrangeConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 1);
    }

    @MenuItem(slot = 30, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 2, name = ""))
    public void buyMagentaConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 2);
    }

    @MenuItem(slot = 31, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 3, name = ""))
    public void buyLightBlueConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 3);
    }

    @MenuItem(slot = 32, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 4, name = ""))
    public void buyYellowConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 4);
    }

    @MenuItem(slot = 33, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 5, name = ""))
    public void buyLimeConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 5);
    }

    @MenuItem(slot = 34, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 6, name = ""))
    public void buyPinkConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 6);
    }

    @MenuItem(slot = 35, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 7, name = ""))
    public void buyGrayConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 7);
    }

    @MenuItem(slot = 36, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 8, name = ""))
    public void buyLightGrayConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 8);
    }

    @MenuItem(slot = 37, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 9, name = ""))
    public void buyCyanConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 9);
    }

    @MenuItem(slot = 38, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 10, name = ""))
    public void buyPurpleConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 10);
    }

    @MenuItem(slot = 39, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 11, name = ""))
    public void buyBlueConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 11);
    }

    @MenuItem(slot = 40, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 12, name = ""))
    public void buyBrownConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 12);
    }

    @MenuItem(slot = 41, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 13, name = ""))
    public void buyGreenConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 13);
    }

    @MenuItem(slot = 42, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 14, name = ""))
    public void buyRedConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 14);
    }

    @MenuItem(slot = 43, item = @ItemStackAnnotation(material = Material.CONCRETE_POWDER, durability = 15, name = ""))
    public void buyBlackConcretePowder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE_POWDER, price(Material.CONCRETE_POWDER), (byte) 15);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Necessities.getRM();
        if (rm.hasRank(Necessities.getUM().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Basic")))
            return true;
        else {
            player.getBukkit().sendMessage(ChatColor.RED + "You must be Basic or higher to purchase from this shop.");
            return false;
        }
    }

    @PreProcessor
    public void process(Inventory inv) {
        setupInventory(inv);
    }

    public int price(Material type) {
        return 1000;
    }
}