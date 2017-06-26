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
@MenuInventory(slots = 36, name = "Advanced Block Shop")
public class AdvancedBlockShop extends Menu implements BlockShop {
    public AdvancedBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(slot = 0, item = @ItemStackAnnotation(material = Material.EMERALD, name = ""))
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCategory(player.getMenuManager(), null));
    }

    @MenuItem(slot = 1, item = @ItemStackAnnotation(material = Material.STEP, name = ""))
    public void buyStoneSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 0);
    }

    @MenuItem(slot = 2, item = @ItemStackAnnotation(material = Material.STEP, durability = 1, name = ""))
    public void buySandstoneSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 1);
    }

    @MenuItem(slot = 3, item = @ItemStackAnnotation(material = Material.STONE_SLAB2, name = ""))
    public void buyRedSandstoneSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STONE_SLAB2, price(Material.STONE_SLAB2));
    }

    @MenuItem(slot = 4, item = @ItemStackAnnotation(material = Material.STEP, durability = 3, name = ""))
    public void buyCobbleSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 3);
    }

    @MenuItem(slot = 5, item = @ItemStackAnnotation(material = Material.STEP, durability = 4, name = ""))
    public void buyBrickSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 4);
    }

    @MenuItem(slot = 6, item = @ItemStackAnnotation(material = Material.STEP, durability = 5, name = ""))
    public void buyStoneBrickSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 5);
    }

    @MenuItem(slot = 7, item = @ItemStackAnnotation(material = Material.STEP, durability = 7, name = ""))
    public void buyQuartzSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 7);
    }

    @MenuItem(slot = 8, item = @ItemStackAnnotation(material = Material.WOOD_STEP, name = ""))
    public void buyOakSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 0);
    }

    @MenuItem(slot = 9, item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 1, name = ""))
    public void buySpruceSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 1);
    }

    @MenuItem(slot = 10, item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 2, name = ""))
    public void buyBirchSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 2);
    }

    @MenuItem(slot = 11, item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 3, name = ""))
    public void buyJungleSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 3);
    }

    @MenuItem(slot = 12, item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 4, name = ""))
    public void buyAcaciaSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 4);
    }

    @MenuItem(slot = 13, item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 5, name = ""))
    public void buyDarkOakSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 5);
    }

    @MenuItem(slot = 14, item = @ItemStackAnnotation(material = Material.MOSSY_COBBLESTONE, name = ""))
    public void buyMossyCobble(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.MOSSY_COBBLESTONE, price(Material.MOSSY_COBBLESTONE));
    }

    @MenuItem(slot = 15, item = @ItemStackAnnotation(material = Material.SMOOTH_BRICK, durability = 2, name = ""))
    public void buyStoneBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SMOOTH_BRICK, price(Material.SMOOTH_BRICK), (byte) 2);
    }

    @MenuItem(slot = 16, item = @ItemStackAnnotation(material = Material.GLASS, name = "Glass"))
    public void buyGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.GLASS, price(Material.GLASS));
    }

    @MenuItem(slot = 17, item = @ItemStackAnnotation(material = Material.CONCRETE, name = ""))
    public void buyWhiteConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 0);
    }

    @MenuItem(slot = 18, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 1, name = ""))
    public void buyOrangeConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 1);
    }

    @MenuItem(slot = 19, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 2, name = ""))
    public void buyMagentaConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 2);
    }

    @MenuItem(slot = 20, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 3, name = ""))
    public void buyLightBlueConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 3);
    }

    @MenuItem(slot = 21, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 4, name = ""))
    public void buyYellowConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 4);
    }

    @MenuItem(slot = 22, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 5, name = ""))
    public void buyLimeConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 5);
    }

    @MenuItem(slot = 23, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 6, name = ""))
    public void buyPinkConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 6);
    }

    @MenuItem(slot = 24, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 7, name = ""))
    public void buyGrayConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 7);
    }

    @MenuItem(slot = 25, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 8, name = ""))
    public void buyLightGrayConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 8);
    }

    @MenuItem(slot = 26, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 9, name = ""))
    public void buyCyanConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 9);
    }

    @MenuItem(slot = 27, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 10, name = ""))
    public void buyPurpleConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 10);
    }

    @MenuItem(slot = 28, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 11, name = ""))
    public void buyBlueConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 11);
    }

    @MenuItem(slot = 29, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 12, name = ""))
    public void buyBrownConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 12);
    }

    @MenuItem(slot = 30, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 13, name = ""))
    public void buyGreenConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 13);
    }

    @MenuItem(slot = 31, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 14, name = ""))
    public void buyRedConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 14);
    }

    @MenuItem(slot = 32, item = @ItemStackAnnotation(material = Material.CONCRETE, durability = 15, name = ""))
    public void buyBlackConcrete(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.CONCRETE, price(Material.CONCRETE), (byte) 15);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Necessities.getRM();
        if (rm.hasRank(Necessities.getUM().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Advanced")))
            return true;
        else {
            player.getBukkit().sendMessage(ChatColor.RED + "You must be Advanced or higher to purchase from this shop.");
            return false;
        }
    }

    @PreProcessor
    public void process(Inventory inv) {
        setupInventory(inv);
    }

    public int price(Material type) {
        return 1500;
    }
}