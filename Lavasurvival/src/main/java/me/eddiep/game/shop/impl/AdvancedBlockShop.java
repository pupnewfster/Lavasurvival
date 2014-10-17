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

@MenuInventory(slots = 18, name = "Advanced Block Shop")
public class AdvancedBlockShop extends Menu {
    Rank advanced = Lavasurvival.INSTANCE.getRankManager().getRank("Advanced");
    public AdvancedBlockShop(MenuManager manager, Inventory inv) {
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
            item = @ItemStackAnnotation(material = (Material.STEP), durability = 0, name = "Stone slab", lore = {"70 ggs"})
    )
    public void buyStoneSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.STEP, 70, (byte) 0, advanced);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.STEP), durability = 1, name = "Sandstone slab", lore = {"70 ggs"})
    )
    public void buySandstoneSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.STEP, 70, (byte) 1, advanced);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.STEP), durability = 3, name = "Cobblestone slab", lore = {"70 ggs"})
    )
    public void buyCobbleSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.STEP, 70, (byte) 3, advanced);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.STEP), durability = 4, name = "Brick slab", lore = {"70 ggs"})
    )
    public void buyBrickSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.STEP, 70, (byte) 4, advanced);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = (Material.STEP), durability = 5, name = "Stone brick slab", lore = {"70 ggs"})
    )
    public void buyStonebrickSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.STEP, 70, (byte) 5, advanced);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = (Material.STEP), durability = 7, name = "Quartz slab", lore = {"70 ggs"})
    )
    public void buyQuartzSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.STEP, 70, (byte) 7, advanced);
    }

    @MenuItem(
            slot = 7,
            item = @ItemStackAnnotation(material = (Material.WOOD_STEP), durability = 0, name = "Oak slab", lore = {"70 ggs"})
    )
    public void buyOakSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.WOOD_STEP, 70, (byte) 0, advanced);
    }

    @MenuItem(
            slot = 8,
            item = @ItemStackAnnotation(material = (Material.WOOD_STEP), durability = 1, name = "Spruce slab", lore = {"70 ggs"})
    )
    public void buySpruceSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.WOOD_STEP, 70, (byte) 1, advanced);
    }

    @MenuItem(
            slot = 9,
            item = @ItemStackAnnotation(material = (Material.WOOD_STEP), durability = 2, name = "Birch slab", lore = {"70 ggs"})
    )
    public void buyBirchSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.WOOD_STEP, 70, (byte) 2, advanced);
    }

    @MenuItem(
            slot = 10,
            item = @ItemStackAnnotation(material = (Material.WOOD_STEP), durability = 3, name = "Jungle slab", lore = {"70 ggs"})
    )
    public void buyJungleSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.WOOD_STEP, 70, (byte) 3, advanced);
    }

    @MenuItem(
            slot = 11,
            item = @ItemStackAnnotation(material = (Material.WOOD_STEP), durability = 4, name = "Acacia slab", lore = {"70 ggs"})
    )
    public void buyAcaciaSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.WOOD_STEP, 70, (byte) 4, advanced);
    }

    @MenuItem(
            slot = 12,
            item = @ItemStackAnnotation(material = (Material.WOOD_STEP), durability = 5, name = "Dark oak slab", lore = {"70 ggs"})
    )
    public void buyDarkOakSlab(MenuPlayer player) {
        getUser(player).buyBlock(Material.WOOD_STEP, 70, (byte) 5, advanced);
    }

    @MenuItem(
            slot = 13,
            item = @ItemStackAnnotation(material = (Material.MOSSY_COBBLESTONE), name = "Mossy cobblestone", lore = {"130 ggs"})
    )
    public void buyMossyCobble(MenuPlayer player) {
        getUser(player).buyBlock(Material.MOSSY_COBBLESTONE, 130, advanced);
    }

    @MenuItem(
            slot = 14,
            item = @ItemStackAnnotation(material = (Material.SMOOTH_BRICK), durability = 2, name = "Cracked stone brick", lore = {"230 ggs"})
    )
    public void buyStoneBrick(MenuPlayer player) {
        getUser(player).buyBlock(Material.SMOOTH_BRICK, 230, (byte) 2, advanced);
    }

    @MenuItem(
            slot = 15,
            item = @ItemStackAnnotation(material = (Material.GLASS), name = "Glass", lore = {"400 ggs"})
    )
    public void buyGlass(MenuPlayer player) {
        getUser(player).buyBlock(Material.GLASS, 400, advanced);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }
}