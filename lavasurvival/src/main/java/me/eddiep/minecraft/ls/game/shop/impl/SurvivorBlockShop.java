package me.eddiep.minecraft.ls.game.shop.impl;

import com.crossge.necessities.RankManager.RankManager;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
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

@MenuInventory(slots = 36, name = "Survivor Block Shop")
public class SurvivorBlockShop extends Menu {
    public SurvivorBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = Material.EMERALD, name = "")
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCategory(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = Material.ICE, name = "")
    )
    public void buyIce(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ICE, price(Material.ICE));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.PACKED_ICE, name = "")
    )
    public void buyPackedIce(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PACKED_ICE, price(Material.PACKED_ICE));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.BRICK, name = "")
    )
    public void buyBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BRICK, price(Material.BRICK));
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.SMOOTH_BRICK, durability = 0, name = "")
    )
    public void buyStoneBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SMOOTH_BRICK, price(Material.SMOOTH_BRICK), (byte) 0);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.THIN_GLASS, name = "")
    )
    public void buyGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.THIN_GLASS, price(Material.THIN_GLASS));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.IRON_FENCE, name = "")
    )
    public void buyIronBars(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.IRON_FENCE, price(Material.IRON_FENCE));
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = Material.IRON_BLOCK, name = "")
    )
    public void buyIron(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.IRON_BLOCK, price(Material.IRON_BLOCK));
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = Material.LADDER, name = "")
    )
    public void buyLadder(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.LADDER, price(Material.LADDER));
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = Material.QUARTZ_BLOCK, name = "")
    )
    public void buyQuartzBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.QUARTZ_BLOCK, price(Material.QUARTZ_BLOCK));
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = Material.QUARTZ_BLOCK, durability = 1, name = "")
    )
    public void buyChiseledQuartzBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.QUARTZ_BLOCK, price(Material.QUARTZ_BLOCK), (byte) 1);
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = Material.QUARTZ_BLOCK, durability = 2, name = "")
    )
    public void buyPillarQuartzBlock(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.QUARTZ_BLOCK, price(Material.QUARTZ_BLOCK), (byte) 2);
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 0, name = "")
    )
    public void buyWhiteWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 0);
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 1, name = "")
    )
    public void buyOrangeWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 1);
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 2, name = "")
    )
    public void buyMagentaWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 2);
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 3, name = "")
    )
    public void buyLightBlueWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 3);
    }

    @MenuItem(
            slot = 16,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 4, name = "")
    )
    public void buyYellowWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 4);
    }

    @MenuItem(
            slot = 17,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 5, name = "")
    )
    public void buyLimeWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 5);
    }

    @MenuItem(
            slot = 18,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 6, name = "")
    )
    public void buyPinkWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 6);
    }

    @MenuItem(
            slot = 19,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 7, name = "")
    )
    public void buyGrayWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 7);
    }

    @MenuItem(
            slot = 20,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 8, name = "")
    )
    public void buyLightGrayWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 8);
    }

    @MenuItem(
            slot = 21,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 9, name = "")
    )
    public void buyCyanWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 9);
    }

    @MenuItem(
            slot = 22,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 10, name = "")
    )
    public void buyPurpleWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 10);
    }

    @MenuItem(
            slot = 23,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 11, name = "")
    )
    public void buyBlueWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 11);
    }

    @MenuItem(
            slot = 24,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 12, name = "")
    )
    public void buyBrownWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 12);
    }

    @MenuItem(
            slot = 25,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 13, name = "")
    )
    public void buyGreenWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 13);
    }

    @MenuItem(
            slot = 26,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 14, name = "")
    )
    public void buyRedWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 14);
    }

    @MenuItem(
            slot = 27,
            item = @ItemStackAnnotation(material = Material.WOOL, durability = 15, name = "")
    )
    public void buyBlackWool(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOL, price(Material.WOOL), (byte) 15);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Lavasurvival.INSTANCE.getRankManager();
        if (rm.hasRank(Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Survivor")))
            return true;
        else {
            player.getBukkit().sendMessage(ChatColor.RED + "You must be Survivor or higher to purchase from this shop.");
            return false;
        }
    }

    @PreProcessor
    public void process(Inventory inv) {
        ItemStack stack = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Back to block shop");
        meta.setLore(Arrays.asList(ChatColor.GOLD + "" + ChatColor.ITALIC + "Buy more blocks!"));
        stack.setItemMeta(meta);
        stack = ShopFactory.addGlow(stack);
        inv.setItem(0, stack);
        for (int i = 1; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is == null)
                continue;
            ItemMeta m = is.getItemMeta();
            m.setLore(Arrays.asList(price(is.getType()) + " ggs", "Lava MeltTime: " + PhysicsListener.getLavaMeltTimeAsString(is.getData()), "Water MeltTime: " + PhysicsListener.getWaterMeltTimeAsString(is.getData())));
            is.setItemMeta(m);
            inv.setItem(i, is);
        }
    }

    protected int price(Material type) {
        switch (type) {
            case ICE:
                return 600;
            case PACKED_ICE:
                return 600;
            case BRICK:
                return 2000;
            case SMOOTH_BRICK:
                return 2000;
            case THIN_GLASS:
                return 1750;
            case IRON_FENCE:
                return 1750;
            case IRON_BLOCK:
                return 2000;
            case LADDER:
                return 600;
            case QUARTZ_BLOCK:
                return 1750;
            case WOOL:
                return 1500;
            default:
                return 0;
        }
    }
}