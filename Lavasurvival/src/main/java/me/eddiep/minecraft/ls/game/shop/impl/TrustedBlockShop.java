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

@MenuInventory(slots = 54, name = "Trusted Block Shop")
public class TrustedBlockShop extends Menu {
    public TrustedBlockShop(MenuManager manager, Inventory inv) {
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
            item = @ItemStackAnnotation(material = Material.WOOD_DOOR, name = "")
    )
    public void buyWoodDoor(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_DOOR, price(Material.WOOD_DOOR));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.FENCE, name = "")
    )
    public void buyFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.FENCE, price(Material.FENCE));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.WOOD_STAIRS, name = "")
    )
    public void buyOakStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STAIRS, price(Material.WOOD_STAIRS));
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.COBBLESTONE_STAIRS, name = "")
    )
    public void buyCobbleStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.COBBLESTONE_STAIRS, price(Material.COBBLESTONE_STAIRS));
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.BRICK_STAIRS, name = "")
    )
    public void buyBrickStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BRICK_STAIRS, price(Material.BRICK_STAIRS));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.SMOOTH_STAIRS, name = "")
    )
    public void buyStoneBrickStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SMOOTH_STAIRS, price(Material.SMOOTH_STAIRS));
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = Material.SANDSTONE_STAIRS, name = "")
    )
    public void buySandstoneStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SANDSTONE_STAIRS, price(Material.SANDSTONE_STAIRS));
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = Material.RED_SANDSTONE_STAIRS, name = "")
    )
    public void buyRedSandstoneStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_SANDSTONE_STAIRS, price(Material.RED_SANDSTONE_STAIRS));
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = Material.SPRUCE_WOOD_STAIRS, name = "")
    )
    public void buySpruceStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SPRUCE_WOOD_STAIRS, price(Material.SPRUCE_WOOD_STAIRS));
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = Material.BIRCH_WOOD_STAIRS, name = "")
    )
    public void buyBirchStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BIRCH_WOOD_STAIRS, price(Material.BIRCH_WOOD_STAIRS));
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = Material.JUNGLE_WOOD_STAIRS, name = "")
    )
    public void buyJungleStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.JUNGLE_WOOD_STAIRS, price(Material.JUNGLE_WOOD_STAIRS));
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = Material.QUARTZ_STAIRS, name = "")
    )
    public void buyQuartzStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.QUARTZ_STAIRS, price(Material.QUARTZ_STAIRS));
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = Material.ACACIA_STAIRS, name = "")
    )
    public void buyAcaciaStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ACACIA_STAIRS, price(Material.ACACIA_STAIRS));
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = Material.DARK_OAK_STAIRS, name = "")
    )
    public void buyDarkOakStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.DARK_OAK_STAIRS, price(Material.DARK_OAK_STAIRS));
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = Material.COBBLE_WALL, durability = 0, name = "")
    )
    public void buyCobbleWall(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.COBBLE_WALL, price(Material.COBBLE_WALL), (byte) 0);
    }

    @MenuItem(
            slot = 16,
            item = @ItemStackAnnotation(material = Material.COBBLE_WALL, durability = 1, name = "")
    )
    public void buyMossyCobbleWall(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.COBBLE_WALL, price(Material.COBBLE_WALL), (byte) 1);
    }

    @MenuItem(
            slot = 17,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 0, name = "")
    )
    public void buyWhiteGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 0);
    }

    @MenuItem(
            slot = 18,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 1, name = "")
    )
    public void buyOrangeGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 1);
    }

    @MenuItem(
            slot = 19,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 2, name = "")
    )
    public void buyMagentaGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 2);
    }

    @MenuItem(
            slot = 20,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 3, name = "")
    )
    public void buyLightBlueGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 3);
    }

    @MenuItem(
            slot = 21,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 4, name = "")
    )
    public void buyYellowGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 4);
    }

    @MenuItem(
            slot = 22,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 5, name = "")
    )
    public void buyLimeGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 5);
    }

    @MenuItem(
            slot = 23,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 6, name = "")
    )
    public void buyPinkGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 6);
    }

    @MenuItem(
            slot = 24,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 7, name = "")
    )
    public void buyGrayGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 7);
    }

    @MenuItem(
            slot = 25,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 8, name = "")
    )
    public void buyLightGrayGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 8);
    }

    @MenuItem(
            slot = 26,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 9, name = "")
    )
    public void buyCyanGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 9);
    }

    @MenuItem(
            slot = 27,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 10, name = "")
    )
    public void buyPurpleGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 10);
    }

    @MenuItem(
            slot = 28,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 11, name = "")
    )
    public void buyBlueGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 11);
    }

    @MenuItem(
            slot = 29,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 12, name = "")
    )
    public void buyBrownGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 12);
    }

    @MenuItem(
            slot = 30,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 13, name = "")
    )
    public void buyGreenGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 13);
    }

    @MenuItem(
            slot = 31,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 14, name = "")
    )
    public void buyRedGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 14);
    }

    @MenuItem(
            slot = 32,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 15, name = "")
    )
    public void buyBlackGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 15);
    }

    @MenuItem(
            slot = 33,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 0, name = "")
    )
    public void buyWhiteGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 0);
    }

    @MenuItem(
            slot = 34,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 1, name = "")
    )
    public void buyOrangeGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 1);
    }

    @MenuItem(
            slot = 35,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 2, name = "")
    )
    public void buyMagentaGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 2);
    }

    @MenuItem(
            slot = 36,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 3, name = "")
    )
    public void buyLightBlueGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 3);
    }

    @MenuItem(
            slot = 37,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 4, name = "")
    )
    public void buyYellowGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 4);
    }

    @MenuItem(
            slot = 38,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 5, name = "")
    )
    public void buyLimeGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 5);
    }

    @MenuItem(
            slot = 39,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 6, name = "")
    )
    public void buyPinkGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 6);
    }

    @MenuItem(
            slot = 40,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 7, name = "")
    )
    public void buyGrayGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 7);
    }

    @MenuItem(
            slot = 41,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 8, name = "")
    )
    public void buyLightGrayGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 8);
    }

    @MenuItem(
            slot = 42,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 9, name = "")
    )
    public void buyCyanGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 9);
    }

    @MenuItem(
            slot = 43,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 10, name = "")
    )
    public void buyPurpleGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 10);
    }

    @MenuItem(
            slot = 44,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 11, name = "")
    )
    public void buyBlueGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 11);
    }

    @MenuItem(
            slot = 45,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 12, name = "")
    )
    public void buyBrownGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 12);
    }

    @MenuItem(
            slot = 46,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 13, name = "")
    )
    public void buyGreenGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 13);
    }

    @MenuItem(
            slot = 47,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 14, name = "")
    )
    public void buyRedGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 14);
    }

    @MenuItem(
            slot = 48,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 15, name = "")
    )
    public void buyBlackGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 15);
    }

    @MenuItem(
            slot = 49,
            item = @ItemStackAnnotation(material = Material.PRISMARINE, durability = 0, name = "")
    )
    public void buyPrismarine(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PRISMARINE, price(Material.PRISMARINE), (byte) 0);
    }

    @MenuItem(
            slot = 50,
            item = @ItemStackAnnotation(material = Material.PRISMARINE, durability = 1, name = "")
    )
    public void buyPrismarineBrick(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PRISMARINE, price(Material.PRISMARINE), (byte) 1);
    }

    @MenuItem(
            slot = 51,
            item = @ItemStackAnnotation(material = Material.PRISMARINE, durability = 2, name = "")
    )
    public void buyDarkPrismarine(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.PRISMARINE, price(Material.PRISMARINE), (byte) 2);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }

    private boolean canBuy(MenuPlayer player) {
        if (player == null || player.getBukkit() == null)
            return false;
        RankManager rm = Lavasurvival.INSTANCE.getRankManager();
        if (rm.hasRank(Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getBukkit().getUniqueId()).getRank(), rm.getRank("Trusted")))
            return true;
        else {
            player.getBukkit().sendMessage(ChatColor.RED + "You must be Trusted or higher to purchase from this shop.");
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
            case WOOD_DOOR:
                return 2100;
            case FENCE:
                return 2100;
            case WOOD_STAIRS:
                return 2100;
            case COBBLESTONE_STAIRS:
                return 2500;
            case BRICK_STAIRS:
                return 2500;
            case SMOOTH_STAIRS:
                return 2500;
            case SANDSTONE_STAIRS:
                return 2500;
            case RED_SANDSTONE_STAIRS:
                return 2500;
            case SPRUCE_WOOD_STAIRS:
                return 2100;
            case BIRCH_WOOD_STAIRS:
                return 2100;
            case JUNGLE_WOOD_STAIRS:
                return 2100;
            case QUARTZ_STAIRS:
                return 2500;
            case ACACIA_STAIRS:
                return 2100;
            case DARK_OAK_STAIRS:
                return 2100;
            case COBBLE_WALL:
                return 2500;
            case STAINED_GLASS:
                return 2100;
            case STAINED_GLASS_PANE:
                return 2100;
            case PRISMARINE:
                return 2500;
            default:
                return 0;
        }
    }
}