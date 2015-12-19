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
        player.setActiveMenu(new BlockShopCategory(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = Material.GRAVEL, name = "")
    )
    public void buyGravel(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.GRAVEL, price(Material.GRAVEL));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.STONE, name = "")
    )
    public void buyStone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STONE, price(Material.STONE));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.LOG, durability = 0, name = "")
    )
    public void buyOakLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 0);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.LOG, durability = 1, name = "")
    )
    public void buySpruceLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 1);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.LOG, durability = 2, name = "")
    )
    public void buyBirchLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 2);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.LOG, durability = 3, name = "")
    )
    public void buyJungleLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG, price(Material.LOG), (byte) 3);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = Material.LOG_2, durability = 0, name = "")
    )
    public void buyAcaciaLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG_2, price(Material.LOG_2), (byte) 0);
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = Material.LOG_2, durability = 1, name = "")
    )
    public void buyDarkOakLog(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LOG_2, price(Material.LOG_2), (byte) 1);
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = Material.SANDSTONE, name = "")
    )
    public void buySandstone(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SANDSTONE, price(Material.SANDSTONE));
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = Material.HARD_CLAY, name = "")
    )
    public void buyHardClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.HARD_CLAY, price(Material.HARD_CLAY));
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 0, name = "")
    )
    public void buyWhiteClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 0);
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 1, name = "")
    )
    public void buyOrangeClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 1);
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 2, name = "")
    )
    public void buyMagentaClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 2);
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 3, name = "")
    )
    public void buyLightBlueClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 3);
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 4, name = "")
    )
    public void buyYellowClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 4);
    }

    @MenuItem(
            slot = 16,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 5, name = "")
    )
    public void buyLimeClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 5);
    }

    @MenuItem(
            slot = 17,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 6, name = "")
    )
    public void buyPinkClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 6);
    }

    @MenuItem(
            slot = 18,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 7, name = "")
    )
    public void buyGrayClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 7);
    }

    @MenuItem(
            slot = 19,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 8, name = "")
    )
    public void buyLightGrayClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 8);
    }

    @MenuItem(
            slot = 20,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 9, name = "")
    )
    public void buyCyanClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 9);
    }

    @MenuItem(
            slot = 21,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 10, name = "")
    )
    public void buyPurpleClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 10);
    }

    @MenuItem(
            slot = 22,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 11, name = "")
    )
    public void buyBlueClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 11);
    }

    @MenuItem(
            slot = 23,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 12, name = "")
    )
    public void buyBrownClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 12);
    }

    @MenuItem(
            slot = 24,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 13, name = "")
    )
    public void buyGreenClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 13);
    }

    @MenuItem(
            slot = 25,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 14, name = "")
    )
    public void buyRedClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 14);
    }

    @MenuItem(
            slot = 26,
            item = @ItemStackAnnotation(material = Material.STAINED_CLAY, durability = 15, name = "")
    )
    public void buyBlackClay(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_CLAY, price(Material.STAINED_CLAY), (byte) 15);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Lavasurvival.INSTANCE.getRankManager();
        if (rm.hasRank(Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Basic")))
            return true;
        else {
            player.getBukkit().sendMessage(ChatColor.RED + "You must be Basic or higher to purchase from this shop.");
            return false;
        }
    }

    @PreProcessor
    public void process(Inventory inv) {
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
            case GRAVEL:
                return 1000;
            case STONE:
                return 1000;
            case LOG:
                return 600;
            case LOG_2:
                return 600;
            case SANDSTONE:
                return 1000;
            case HARD_CLAY:
                return 1000;
            case STAINED_CLAY:
                return 1000;
            default:
                return 0;
        }
    }
}