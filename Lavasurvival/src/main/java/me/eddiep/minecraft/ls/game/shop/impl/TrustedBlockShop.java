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
        player.setActiveMenu(new BlockShopCatagory(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = Material.FLOWER_POT_ITEM, name = "Flowerpot")
    )
    public void buyFlowerpot(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.FLOWER_POT_ITEM, price(Material.FLOWER_POT_ITEM));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.YELLOW_FLOWER, name = "Dandelion")
    )
    public void buyDandelion(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.YELLOW_FLOWER, price(Material.YELLOW_FLOWER));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, name = "Poppy")
    )
    public void buyPoppy(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.RED_ROSE, price(Material.RED_ROSE));
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.WOOD_DOOR, name = "Wooden door")
    )
    public void buyWoodDoor(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_DOOR, price(Material.WOOD_DOOR));
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.BOOKSHELF, name = "Bookshelf")
    )
    public void buyBookshelf(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BOOKSHELF, price(Material.BOOKSHELF));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.FENCE, name = "Fence")
    )
    public void buyFence(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.FENCE, price(Material.FENCE));
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = Material.WOOD_STAIRS, name = "Oak stairs")
    )
    public void buyOakStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.WOOD_STAIRS, price(Material.WOOD_STAIRS));
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = Material.COBBLESTONE_STAIRS, name = "Cobblestone stairs")
    )
    public void buyCobbleStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.COBBLESTONE_STAIRS, price(Material.COBBLESTONE_STAIRS));
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = Material.BRICK_STAIRS, name = "Brick stairs")
    )
    public void buyBrickStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BRICK_STAIRS, price(Material.BRICK_STAIRS));
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = Material.SMOOTH_STAIRS, name = "Stone brick stairs")
    )
    public void buyStoneBrickStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SMOOTH_STAIRS, price(Material.SMOOTH_STAIRS));
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = Material.SANDSTONE_STAIRS, name = "Sandstone stairs")
    )
    public void buySandstoneStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SANDSTONE_STAIRS, price(Material.SANDSTONE_STAIRS));
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = Material.SPRUCE_WOOD_STAIRS, name = "Spruce stairs")
    )
    public void buySpruceStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.SPRUCE_WOOD_STAIRS, price(Material.SPRUCE_WOOD_STAIRS));
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = Material.BIRCH_WOOD_STAIRS, name = "Birch stairs")
    )
    public void buyBirchStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.BIRCH_WOOD_STAIRS, price(Material.BIRCH_WOOD_STAIRS));
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = Material.JUNGLE_WOOD_STAIRS, name = "Jungle stairs")
    )
    public void buyJungleStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.JUNGLE_WOOD_STAIRS, price(Material.JUNGLE_WOOD_STAIRS));
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = Material.QUARTZ_STAIRS, name = "Quartz stairs")
    )
    public void buyQuartzStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.QUARTZ_STAIRS, price(Material.QUARTZ_STAIRS));
    }

    @MenuItem(
            slot = 16,
            item = @ItemStackAnnotation(material = Material.ACACIA_STAIRS, name = "Acacia stairs")
    )
    public void buyAcaciaStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.ACACIA_STAIRS, price(Material.ACACIA_STAIRS));
    }

    @MenuItem(
            slot = 17,
            item = @ItemStackAnnotation(material = Material.DARK_OAK_STAIRS, name = "Dark oak stairs")
    )
    public void buyDarkOakStairs(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.DARK_OAK_STAIRS, price(Material.DARK_OAK_STAIRS));
    }

    @MenuItem(
            slot = 18,
            item = @ItemStackAnnotation(material = Material.COBBLE_WALL, durability = 0, name = "Cobblestone wall")
    )
    public void buyCobbleWall(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.COBBLE_WALL, price(Material.COBBLE_WALL), (byte) 0);
    }

    @MenuItem(
            slot = 19,
            item = @ItemStackAnnotation(material = Material.COBBLE_WALL, durability = 1, name = "Mossy cobblestone wall")
    )
    public void buyMossyCobbleWall(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.COBBLE_WALL, price(Material.COBBLE_WALL), (byte) 1);
    }

    @MenuItem(
            slot = 20,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 0, name = "White stained glass")
    )
    public void buyWhiteGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 0);
    }

    @MenuItem(
            slot = 21,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 1, name = "Orange stained glass")
    )
    public void buyOrangeGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 1);
    }

    @MenuItem(
            slot = 22,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 2, name = "Magenta stained glass")
    )
    public void buyMagentaGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 2);
    }

    @MenuItem(
            slot = 23,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 3, name = "Light blue stained glass")
    )
    public void buyLightBlueGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 3);
    }

    @MenuItem(
            slot = 24,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 4, name = "Yellow stained glass")
    )
    public void buyYellowGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 4);
    }

    @MenuItem(
            slot = 25,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 5, name = "Lime stained glass")
    )
    public void buyLimeGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 5);
    }

    @MenuItem(
            slot = 26,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 6, name = "Pink stained glass")
    )
    public void buyPinkGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 6);
    }

    @MenuItem(
            slot = 27,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 7, name = "Gray stained glass")
    )
    public void buyGrayGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 7);
    }

    @MenuItem(
            slot = 28,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 8, name = "Light gray stained glass")
    )
    public void buyLightGrayGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 8);
    }

    @MenuItem(
            slot = 29,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 9, name = "Cyan stained glass")
    )
    public void buyCyanGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 9);
    }

    @MenuItem(
            slot = 30,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 10, name = "Purple stained glass")
    )
    public void buyPurpleGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 10);
    }

    @MenuItem(
            slot = 31,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 11, name = "Blue stained glass")
    )
    public void buyBlueGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 11);
    }

    @MenuItem(
            slot = 32,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 12, name = "Brown staied glass")
    )
    public void buyBrownGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 12);
    }

    @MenuItem(
            slot = 33,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 13, name = "Green stained glass")
    )
    public void buyGreenGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 13);
    }

    @MenuItem(
            slot = 34,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 14, name = "Red stained glass")
    )
    public void buyRedGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 14);
    }

    @MenuItem(
            slot = 35,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS, durability = 15, name = "Black stained glass")
    )
    public void buyBlackGlass(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS, price(Material.STAINED_GLASS), (byte) 15);
    }

    @MenuItem(
            slot = 36,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 0, name = "White stained glass pane")
    )
    public void buyWhiteGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 0);
    }

    @MenuItem(
            slot = 37,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 1, name = "Orange stained glass pane")
    )
    public void buyOrangeGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 1);
    }

    @MenuItem(
            slot = 38,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 2, name = "Magenta stained glass pane")
    )
    public void buyMagentaGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 2);
    }

    @MenuItem(
            slot = 39,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 3, name = "Light blue stained glass pane")
    )
    public void buyLightBlueGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 3);
    }

    @MenuItem(
            slot = 40,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 4, name = "Yellow stained glass pane")
    )
    public void buyYellowGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 4);
    }

    @MenuItem(
            slot = 41,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 5, name = "Lime stained glass pane")
    )
    public void buyLimeGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 5);
    }

    @MenuItem(
            slot = 42,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 6, name = "Pink stained glass pane")
    )
    public void buyPinkGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 6);
    }

    @MenuItem(
            slot = 43,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 7, name = "Gray stained glass pane")
    )
    public void buyGrayGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 7);
    }

    @MenuItem(
            slot = 44,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 8, name = "Light gray stained glass pane")
    )
    public void buyLightGrayGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 8);
    }

    @MenuItem(
            slot = 45,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 9, name = "Cyan stained glass pane")
    )
    public void buyCyanGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 9);
    }

    @MenuItem(
            slot = 46,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 10, name = "Purple stained glass pane")
    )
    public void buyPurpleGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 10);
    }

    @MenuItem(
            slot = 47,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 11, name = "Blue stained glass pane")
    )
    public void buyBlueGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 11);
    }

    @MenuItem(
            slot = 48,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 12, name = "Brown staied glass pane")
    )
    public void buyBrownGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 12);
    }

    @MenuItem(
            slot = 49,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 13, name = "Green stained glass pane")
    )
    public void buyGreenGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 13);
    }

    @MenuItem(
            slot = 50,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 14, name = "Red stained glass pane")
    )
    public void buyRedGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 14);
    }

    @MenuItem(
            slot = 51,
            item = @ItemStackAnnotation(material = Material.STAINED_GLASS_PANE, durability = 15, name = "Black stained glass pane")
    )
    public void buyBlackGlassPane(MenuPlayer player) {
        if (canBuy(player))
            getUser(player).buyBlock(Material.STAINED_GLASS_PANE, price(Material.STAINED_GLASS_PANE), (byte) 15);
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
            case FLOWER_POT_ITEM:
                return 20;
            case YELLOW_FLOWER:
                return 30;
            case RED_ROSE:
                return 40;
            case WOOD_DOOR:
                return 40;
            case BOOKSHELF:
                return 80;
            case FENCE:
                return 130;
            case WOOD_STAIRS:
                return 170;
            case COBBLESTONE_STAIRS:
                return 170;
            case BRICK_STAIRS:
                return 170;
            case SMOOTH_STAIRS:
                return 170;
            case SANDSTONE_STAIRS:
                return 170;
            case SPRUCE_WOOD_STAIRS:
                return 170;
            case BIRCH_WOOD_STAIRS:
                return 170;
            case JUNGLE_WOOD_STAIRS:
                return 170;
            case QUARTZ_STAIRS:
                return 170;
            case ACACIA_STAIRS:
                return 170;
            case DARK_OAK_STAIRS:
                return 170;
            case COBBLE_WALL:
                return 190;
            case STAINED_GLASS:
                return 400;
            case STAINED_GLASS_PANE:
                return 400;
            default:
                return 0;
        }
    }
}