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
@MenuInventory(slots = 54, name = "Survivor Block Shop")
public class SurvivorBlockShop extends Menu implements BlockShop {
    public SurvivorBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(slot = 0, item = @ItemStackAnnotation(material = Material.EMERALD, name = ""))
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCategory(player.getMenuManager(), null));
    }

    @MenuItem(slot = 1, item = @ItemStackAnnotation(material = Material.ICE, name = ""))
    public void buyIce(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ICE, price(Material.ICE));
    }

    @MenuItem(slot = 2, item = @ItemStackAnnotation(material = Material.PACKED_ICE, name = ""))
    public void buyPackedIce(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PACKED_ICE, price(Material.PACKED_ICE));
    }

    @MenuItem(slot = 3, item = @ItemStackAnnotation(material = Material.BRICK, name = ""))
    public void buyBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BRICK, price(Material.BRICK));
    }

    @MenuItem(slot = 4, item = @ItemStackAnnotation(material = Material.SMOOTH_BRICK, name = ""))
    public void buyStoneBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SMOOTH_BRICK, price(Material.SMOOTH_BRICK), (byte) 0);
    }

    @MenuItem(slot = 5, item = @ItemStackAnnotation(material = Material.THIN_GLASS, name = ""))
    public void buyGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.THIN_GLASS, price(Material.THIN_GLASS));
    }

    @MenuItem(slot = 6, item = @ItemStackAnnotation(material = Material.IRON_FENCE, name = ""))
    public void buyIronBars(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.IRON_FENCE, price(Material.IRON_FENCE));
    }

    @MenuItem(slot = 7, item = @ItemStackAnnotation(material = Material.IRON_BLOCK, name = ""))
    public void buyIron(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.IRON_BLOCK, price(Material.IRON_BLOCK));
    }

    @MenuItem(slot = 8, item = @ItemStackAnnotation(material = Material.LADDER, name = ""))
    public void buyLadder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LADDER, price(Material.LADDER));
    }

    @MenuItem(slot = 9, item = @ItemStackAnnotation(material = Material.QUARTZ_BLOCK, name = ""))
    public void buyQuartzBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.QUARTZ_BLOCK, price(Material.QUARTZ_BLOCK));
    }

    @MenuItem(slot = 10, item = @ItemStackAnnotation(material = Material.QUARTZ_BLOCK, durability = 1, name = ""))
    public void buyChiseledQuartzBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.QUARTZ_BLOCK, price(Material.QUARTZ_BLOCK), (byte) 1);
    }

    @MenuItem(slot = 11, item = @ItemStackAnnotation(material = Material.QUARTZ_BLOCK, durability = 2, name = ""))
    public void buyPillarQuartzBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.QUARTZ_BLOCK, price(Material.QUARTZ_BLOCK), (byte) 2);
    }

    @MenuItem(slot = 12, item = @ItemStackAnnotation(material = Material.WOOL, name = ""))
    public void buyWhiteWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 0);
    }

    @MenuItem(slot = 13, item = @ItemStackAnnotation(material = Material.WOOL, durability = 1, name = ""))
    public void buyOrangeWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 1);
    }

    @MenuItem(slot = 14, item = @ItemStackAnnotation(material = Material.WOOL, durability = 2, name = ""))
    public void buyMagentaWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 2);
    }

    @MenuItem(slot = 15, item = @ItemStackAnnotation(material = Material.WOOL, durability = 3, name = ""))
    public void buyLightBlueWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 3);
    }

    @MenuItem(slot = 16, item = @ItemStackAnnotation(material = Material.WOOL, durability = 4, name = ""))
    public void buyYellowWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 4);
    }

    @MenuItem(slot = 17, item = @ItemStackAnnotation(material = Material.WOOL, durability = 5, name = ""))
    public void buyLimeWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 5);
    }

    @MenuItem(slot = 18, item = @ItemStackAnnotation(material = Material.WOOL, durability = 6, name = ""))
    public void buyPinkWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 6);
    }

    @MenuItem(slot = 19, item = @ItemStackAnnotation(material = Material.WOOL, durability = 7, name = ""))
    public void buyGrayWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 7);
    }

    @MenuItem(slot = 20, item = @ItemStackAnnotation(material = Material.WOOL, durability = 8, name = ""))
    public void buyLightGrayWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 8);
    }

    @MenuItem(slot = 21, item = @ItemStackAnnotation(material = Material.WOOL, durability = 9, name = ""))
    public void buyCyanWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 9);
    }

    @MenuItem(slot = 22, item = @ItemStackAnnotation(material = Material.WOOL, durability = 10, name = ""))
    public void buyPurpleWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 10);
    }

    @MenuItem(slot = 23, item = @ItemStackAnnotation(material = Material.WOOL, durability = 11, name = ""))
    public void buyBlueWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 11);
    }

    @MenuItem(slot = 24, item = @ItemStackAnnotation(material = Material.WOOL, durability = 12, name = ""))
    public void buyBrownWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 12);
    }

    @MenuItem(slot = 25, item = @ItemStackAnnotation(material = Material.WOOL, durability = 13, name = ""))
    public void buyGreenWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 13);
    }

    @MenuItem(slot = 26, item = @ItemStackAnnotation(material = Material.WOOL, durability = 14, name = ""))
    public void buyRedWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 14);
    }

    @MenuItem(slot = 27, item = @ItemStackAnnotation(material = Material.WOOL, durability = 15, name = ""))
    public void buyBlackWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 15);
    }

    @MenuItem(slot = 28, item = @ItemStackAnnotation(material = Material.WHITE_GLAZED_TERRACOTTA, name = ""))
    public void buyWhiteGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WHITE_GLAZED_TERRACOTTA, price(Material.WHITE_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 29, item = @ItemStackAnnotation(material = Material.ORANGE_GLAZED_TERRACOTTA, name = ""))
    public void buyOrangeGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ORANGE_GLAZED_TERRACOTTA, price(Material.ORANGE_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 30, item = @ItemStackAnnotation(material = Material.MAGENTA_GLAZED_TERRACOTTA, name = ""))
    public void buyMagentaGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.MAGENTA_GLAZED_TERRACOTTA, price(Material.MAGENTA_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 31, item = @ItemStackAnnotation(material = Material.LIGHT_BLUE_GLAZED_TERRACOTTA, name = ""))
    public void buyLightBlueGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, price(Material.LIGHT_BLUE_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 32, item = @ItemStackAnnotation(material = Material.YELLOW_GLAZED_TERRACOTTA, name = ""))
    public void buyYellowGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.YELLOW_GLAZED_TERRACOTTA, price(Material.YELLOW_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 33, item = @ItemStackAnnotation(material = Material.LIME_GLAZED_TERRACOTTA, name = ""))
    public void buyLimeGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LIME_GLAZED_TERRACOTTA, price(Material.LIME_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 34, item = @ItemStackAnnotation(material = Material.PINK_GLAZED_TERRACOTTA, name = ""))
    public void buyPinkGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PINK_GLAZED_TERRACOTTA, price(Material.PINK_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 35, item = @ItemStackAnnotation(material = Material.GRAY_GLAZED_TERRACOTTA, name = ""))
    public void buyGrayGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.GRAY_GLAZED_TERRACOTTA, price(Material.GRAY_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 36, item = @ItemStackAnnotation(material = Material.SILVER_GLAZED_TERRACOTTA, name = ""))
    public void buyLightGrayGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SILVER_GLAZED_TERRACOTTA, price(Material.SILVER_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 37, item = @ItemStackAnnotation(material = Material.CYAN_GLAZED_TERRACOTTA, name = ""))
    public void buyCyanGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CYAN_GLAZED_TERRACOTTA, price(Material.CYAN_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 38, item = @ItemStackAnnotation(material = Material.PURPLE_GLAZED_TERRACOTTA, name = ""))
    public void buyPurpleGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PURPLE_GLAZED_TERRACOTTA, price(Material.PURPLE_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 39, item = @ItemStackAnnotation(material = Material.BLUE_GLAZED_TERRACOTTA, name = ""))
    public void buyBlueGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BLUE_GLAZED_TERRACOTTA, price(Material.BLUE_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 40, item = @ItemStackAnnotation(material = Material.BROWN_GLAZED_TERRACOTTA, name = ""))
    public void buyBrownGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BROWN_GLAZED_TERRACOTTA, price(Material.BROWN_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 41, item = @ItemStackAnnotation(material = Material.GREEN_GLAZED_TERRACOTTA, name = ""))
    public void buyGreenGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.GREEN_GLAZED_TERRACOTTA, price(Material.GREEN_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 42, item = @ItemStackAnnotation(material = Material.RED_GLAZED_TERRACOTTA, name = ""))
    public void buyRedGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_GLAZED_TERRACOTTA, price(Material.RED_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 43, item = @ItemStackAnnotation(material = Material.BLACK_GLAZED_TERRACOTTA, name = ""))
    public void buyBlackGlazedTerracotta(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BLACK_GLAZED_TERRACOTTA, price(Material.BLACK_GLAZED_TERRACOTTA));
    }

    @MenuItem(slot = 44, item = @ItemStackAnnotation(material = Material.NETHER_WART_BLOCK, name = ""))
    public void buyNetherWartBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.NETHER_WART_BLOCK, price(Material.NETHER_WART_BLOCK));
    }

    @MenuItem(slot = 45, item = @ItemStackAnnotation(material = Material.TRAP_DOOR, name = ""))
    public void buyTrapdoor(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.TRAP_DOOR, price(Material.TRAP_DOOR));
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Necessities.getRM();
        if (rm.hasRank(Necessities.getUM().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Survivor")))
            return true;
        else {
            player.getBukkit().sendMessage(ChatColor.RED + "You must be Survivor or higher to purchase from this shop.");
            return false;
        }
    }

    @PreProcessor
    public void process(Inventory inv) {
        setupInventory(inv);
    }

    public int price(Material type) {
        return 2000;
    }
}