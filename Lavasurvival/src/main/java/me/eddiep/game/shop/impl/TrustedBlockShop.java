package me.eddiep.game.shop.impl;

import me.eddiep.Lavasurvival;
import me.eddiep.ranks.Rank;
import me.eddiep.ranks.UserInfo;
import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.player.MenuPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@MenuInventory(slots = 54, name = "Trusted Block Shop")
public class TrustedBlockShop extends Menu {
    Rank trusted = Lavasurvival.INSTANCE.getRankManager().getRank("Trusted");
    public TrustedBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = (Material.EMERALD), name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCatagory(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = (Material.FLOWER_POT_ITEM), name = "Flowerpot", lore = {"20 ggs"})
    )
    public void buyFlowerpot(MenuPlayer player) {
        getUser(player).buyBlock(Material.FLOWER_POT_ITEM, 20, trusted);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.YELLOW_FLOWER), name = "Dandelion", lore = {"30 ggs"})
    )
    public void buyDandelion(MenuPlayer player) {
        getUser(player).buyBlock(Material.YELLOW_FLOWER, 30, trusted);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.RED_ROSE), name = "Poppy", lore = {"40 ggs"})
    )
    public void buyPoppy(MenuPlayer player) {
        getUser(player).buyBlock(Material.RED_ROSE, 40, trusted);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.WOOD_DOOR), name = "Wooden door", lore = {"40 ggs"})
    )
    public void buyWoodDoor(MenuPlayer player) {
        getUser(player).buyBlock(Material.WOOD_DOOR, 40, trusted);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = (Material.BOOKSHELF), name = "Bookshelf", lore = {"80 ggs"})
    )
    public void buyBookshelf(MenuPlayer player) {
        getUser(player).buyBlock(Material.BOOKSHELF, 80, trusted);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = (Material.FENCE), name = "Fence", lore = {"130 ggs"})
    )
    public void buyFence(MenuPlayer player) {
        getUser(player).buyBlock(Material.FENCE, 130, trusted);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = (Material.WOOD_STAIRS), name = "Oak stairs", lore = {"170 ggs"})
    )
    public void buyOakStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.WOOD_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = (Material.COBBLESTONE_STAIRS), name = "Cobblestone stairs", lore = {"170 ggs"})
    )
    public void buyCobbleStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.COBBLESTONE_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = (Material.BRICK_STAIRS), name = "Brick stairs", lore = {"170 ggs"})
    )
    public void buyBrickStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.BRICK_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = (Material.SMOOTH_STAIRS), name = "Stone brick stairs", lore = {"170 ggs"})
    )
    public void buyStoneBrickStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.SMOOTH_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = (Material.SANDSTONE_STAIRS), name = "Sandstone stairs", lore = {"170 ggs"})
    )
    public void buySandstoneStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.SANDSTONE_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = (Material.SPRUCE_WOOD_STAIRS), name = "Spruce stairs", lore = {"170 ggs"})
    )
    public void buySpruceStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.SPRUCE_WOOD_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = (Material.BIRCH_WOOD_STAIRS), name = "Birch stairs", lore = {"170 ggs"})
    )
    public void buyBirchStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.BIRCH_WOOD_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = (Material.JUNGLE_WOOD_STAIRS), name = "Jungle stairs", lore = {"170 ggs"})
    )
    public void buyJungleStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.JUNGLE_WOOD_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = (Material.QUARTZ_STAIRS), name = "Quartz stairs", lore = {"170 ggs"})
    )
    public void buyQuartzStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.QUARTZ_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 16,
            item = @ItemStackAnnotation(material = (Material.ACACIA_STAIRS), name = "Acacia stairs", lore = {"170 ggs"})
    )
    public void buyAcaciaStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.ACACIA_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 17,
            item = @ItemStackAnnotation(material = (Material.DARK_OAK_STAIRS), name = "Dark oak stairs", lore = {"170 ggs"})
    )
    public void buyDarkOakStairs(MenuPlayer player) {
        getUser(player).buyBlock(Material.DARK_OAK_STAIRS, 170, trusted);
    }

    @MenuItem(
            slot = 18,
            item = @ItemStackAnnotation(material = (Material.COBBLE_WALL), durability = 0, name = "Cobblestone wall", lore = {"190 ggs"})
    )
    public void buyCobbleWall(MenuPlayer player) {
        getUser(player).buyBlock(Material.COBBLE_WALL, 190, (byte) 0, trusted);
    }

    @MenuItem(
            slot = 19,
            item = @ItemStackAnnotation(material = (Material.COBBLE_WALL), durability = 1, name = "Mossy cobblestone wall", lore = {"190 ggs"})
    )
    public void buyMossyCobbleWall(MenuPlayer player) {
        getUser(player).buyBlock(Material.COBBLE_WALL, 190, (byte) 1, trusted);
    }

    @MenuItem(
            slot = 20,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 0, name = "White stained glass", lore = {"400 ggs"})
    )
    public void buyWhiteGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 0, trusted);
    }

    @MenuItem(
            slot = 21,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 1, name = "Orange stained glass", lore = {"400 ggs"})
    )
    public void buyOrangeGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 1, trusted);
    }

    @MenuItem(
            slot = 22,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 2, name = "Magenta stained glass", lore = {"400 ggs"})
    )
    public void buyMagentaGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 2, trusted);
    }

    @MenuItem(
            slot = 23,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 3, name = "Light blue stained glass", lore = {"400 ggs"})
    )
    public void buyLightBlueGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 3, trusted);
    }

    @MenuItem(
            slot = 24,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 4, name = "Yellow stained glass", lore = {"400 ggs"})
    )
    public void buyYellowGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 4, trusted);
    }

    @MenuItem(
            slot = 25,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 5, name = "Lime stained glass", lore = {"400 ggs"})
    )
    public void buyLimeGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 5, trusted);
    }

    @MenuItem(
            slot = 26,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 6, name = "Pink stained glass", lore = {"400 ggs"})
    )
    public void buyPinkGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 6, trusted);
    }

    @MenuItem(
            slot = 27,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 7, name = "Gray stained glass", lore = {"400 ggs"})
    )
    public void buyGrayGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 7, trusted);
    }

    @MenuItem(
            slot = 28,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 8, name = "Light gray stained glass", lore = {"400 ggs"})
    )
    public void buyLightGrayGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 8, trusted);
    }

    @MenuItem(
            slot = 29,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 9, name = "Cyan stained glass", lore = {"400 ggs"})
    )
    public void buyCyanGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 9, trusted);
    }

    @MenuItem(
            slot = 30,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 10, name = "Purple stained glass", lore = {"400 ggs"})
    )
    public void buyPurpleGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 10, trusted);
    }

    @MenuItem(
            slot = 31,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 11, name = "Blue stained glass", lore = {"400 ggs"})
    )
    public void buyBlueGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 11, trusted);
    }

    @MenuItem(
            slot = 32,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 12, name = "Brown staied glass", lore = {"400 ggs"})
    )
    public void buyBrownGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 12, trusted);
    }

    @MenuItem(
            slot = 33,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 13, name = "Green stained glass", lore = {"400 ggs"})
    )
    public void buyGreenGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 13, trusted);
    }

    @MenuItem(
            slot = 34,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 14, name = "Red stained glass", lore = {"400 ggs"})
    )
    public void buyRedGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 14, trusted);
    }

    @MenuItem(
            slot = 35,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS), durability = 15, name = "Black stained glass", lore = {"400 ggs"})
    )
    public void buyBlackGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS, 400, (byte) 15, trusted);
    }

    @MenuItem(
            slot = 36,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 0, name = "White stained glass pane", lore = {"400 ggs"})
    )
    public void buyWhiteGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 0, trusted);
    }

    @MenuItem(
            slot = 37,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 1, name = "Orange stained glass pane", lore = {"400 ggs"})
    )
    public void buyOrangeGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 1, trusted);
    }

    @MenuItem(
            slot = 38,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 2, name = "Magenta stained glass pane", lore = {"400 ggs"})
    )
    public void buyMagentaGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 2, trusted);
    }

    @MenuItem(
            slot = 39,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 3, name = "Light blue stained glass pane", lore = {"400 ggs"})
    )
    public void buyLightBlueGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 3, trusted);
    }

    @MenuItem(
            slot = 40,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 4, name = "Yellow stained glass pane", lore = {"400 ggs"})
    )
    public void buyYellowGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 4, trusted);
    }

    @MenuItem(
            slot = 41,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 5, name = "Lime stained glass pane", lore = {"400 ggs"})
    )
    public void buyLimeGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 5, trusted);
    }

    @MenuItem(
            slot = 42,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 6, name = "Pink stained glass pane", lore = {"400 ggs"})
    )
    public void buyPinkGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 6, trusted);
    }

    @MenuItem(
            slot = 43,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 7, name = "Gray stained glass pane", lore = {"400 ggs"})
    )
    public void buyGrayGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 7, trusted);
    }

    @MenuItem(
            slot = 44,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 8, name = "Light gray stained glass pane", lore = {"400 ggs"})
    )
    public void buyLightGrayGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 8, trusted);
    }

    @MenuItem(
            slot = 45,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 9, name = "Cyan stained glass pane", lore = {"400 ggs"})
    )
    public void buyCyanGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 9, trusted);
    }

    @MenuItem(
            slot = 46,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 10, name = "Purple stained glass pane", lore = {"400 ggs"})
    )
    public void buyPurpleGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 10, trusted);
    }

    @MenuItem(
            slot = 47,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 11, name = "Blue stained glass pane", lore = {"400 ggs"})
    )
    public void buyBlueGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 11, trusted);
    }

    @MenuItem(
            slot = 48,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 12, name = "Brown staied glass pane", lore = {"400 ggs"})
    )
    public void buyBrownGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 12, trusted);
    }

    @MenuItem(
            slot = 49,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 13, name = "Green stained glass pane", lore = {"400 ggs"})
    )
    public void buyGreenGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 13, trusted);
    }

    @MenuItem(
            slot = 50,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 14, name = "Red stained glass pane", lore = {"400 ggs"})
    )
    public void buyRedGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 14, trusted);
    }

    @MenuItem(
            slot = 51,
            item = @ItemStackAnnotation(material = (Material.STAINED_GLASS_PANE), durability = 15, name = "Black stained glass pane", lore = {"400 ggs"})
    )
    public void buyBlackGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.STAINED_GLASS_PANE, 400, (byte) 15, trusted);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }
}