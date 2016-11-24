package me.eddiep.minecraft.ls.game.shop.impl;

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
@MenuInventory(slots = 18, name = "Advanced Block Shop")
public class AdvancedBlockShop extends Menu implements BlockShop {
    AdvancedBlockShop(MenuManager manager, Inventory inv) {
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

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Lavasurvival.INSTANCE.getRankManager();
        if (rm.hasRank(Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Advanced")))
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