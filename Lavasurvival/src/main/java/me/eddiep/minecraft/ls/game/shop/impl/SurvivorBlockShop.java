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

@MenuInventory(slots = 9, name = "Survivor Block Shop")
public class SurvivorBlockShop extends BlockShop {
    public SurvivorBlockShop(MenuManager manager, Inventory inv) {
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
            item = @ItemStackAnnotation(material = Material.PACKED_ICE, name = "Packed ice")
    )
    public void buyPackedIce(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PACKED_ICE, price(Material.PACKED_ICE));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.BRICK, name = "Brick")
    )
    public void buyBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BRICK, price(Material.BRICK));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.SMOOTH_BRICK, durability = 0, name = "Stone brick")
    )
    public void buyStoneBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SMOOTH_BRICK, price(Material.SMOOTH_BRICK), (byte) 0);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.THIN_GLASS, name = "Glass pane")
    )
    public void buyGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.THIN_GLASS, price(Material.THIN_GLASS));
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.IRON_FENCE, name = "Iron bars")
    )
    public void buyIronBars(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.IRON_FENCE, price(Material.IRON_FENCE));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.IRON_BLOCK, name = "Block of iron")
    )
    public void buyIron(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.IRON_BLOCK, price(Material.IRON_BLOCK));
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

    @Override
    protected int price(Material type) {
        switch (type) {
            case PACKED_ICE:
                return 20;
            case BRICK:
                return 220;
            case SMOOTH_BRICK:
                return 300;
            case THIN_GLASS:
                return 380;
            case IRON_FENCE:
                return 450;
            case IRON_BLOCK:
                return 480;
            default:
                return 0;
        }
    }
}