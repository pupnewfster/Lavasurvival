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

@MenuInventory(slots = 18, name = "Advanced Block Shop")
public class AdvancedBlockShop extends Menu {
    public AdvancedBlockShop(MenuManager manager, Inventory inv) {
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
            item = @ItemStackAnnotation(material = Material.STEP, durability = 0, name = "Stone slab")
    )
    public void buyStoneSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 0);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.STEP, durability = 1, name = "Sandstone slab")
    )
    public void buySandstoneSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 1);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.STEP, durability = 3, name = "Cobblestone slab")
    )
    public void buyCobbleSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 3);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.STEP, durability = 4, name = "Brick slab")
    )
    public void buyBrickSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 4);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.STEP, durability = 5, name = "Stone brick slab")
    )
    public void buyStonebrickSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 5);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.STEP, durability = 7, name = "Quartz slab")
    )
    public void buyQuartzSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STEP, price(Material.STEP), (byte) 7);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 0, name = "Oak slab")
    )
    public void buyOakSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 0);
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 1, name = "Spruce slab")
    )
    public void buySpruceSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 1);
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 2, name = "Birch slab")
    )
    public void buyBirchSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 2);
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 3, name = "Jungle slab")
    )
    public void buyJungleSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 3);
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 4, name = "Acacia slab")
    )
    public void buyAcaciaSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 4);
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = Material.WOOD_STEP, durability = 5, name = "Dark oak slab")
    )
    public void buyDarkOakSlab(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STEP, price(Material.WOOD_STEP), (byte) 5);
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = Material.MOSSY_COBBLESTONE, name = "Mossy cobblestone")
    )
    public void buyMossyCobble(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.MOSSY_COBBLESTONE, price(Material.MOSSY_COBBLESTONE));
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = Material.SMOOTH_BRICK, durability = 2, name = "Cracked stone brick")
    )
    public void buyStoneBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SMOOTH_BRICK, price(Material.SMOOTH_BRICK), (byte) 2);
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = Material.GLASS, name = "Glass")
    )
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
            case STEP:
                return 70;
            case WOOD_STEP:
                return 70;
            case MOSSY_COBBLESTONE:
                return 130;
            case SMOOTH_BRICK:
                return 230;
            case GLASS:
                return 400;
            default:
                return 0;
        }
    }
}