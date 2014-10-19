package me.eddiep.game.shop.impl;

import me.eddiep.Lavasurvival;
import me.eddiep.ranks.UserInfo;
import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.player.MenuPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@MenuInventory(slots = 9, name = "Survivor Block Shop")
public class SurvivorBlockShop extends Menu {
    public SurvivorBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = (Material.EMERALD), name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCatagory(player.getMenuManager(), null, player.getBukkit()));
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = (Material.PACKED_ICE), name = "Packed ice", lore = {"20 ggs"})
    )
    public void buyPackedIce(MenuPlayer player) {
        getUser(player).buyBlock(Material.PACKED_ICE, 20);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.BRICK), name = "Brick", lore = {"220 ggs"})
    )
    public void buyBrick(MenuPlayer player) {
        getUser(player).buyBlock(Material.BRICK, 220);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.SMOOTH_BRICK), durability = 0, name = "Stone brick", lore = {"300 ggs"})
    )
    public void buyStoneBrick(MenuPlayer player) {
        getUser(player).buyBlock(Material.SMOOTH_BRICK, 300, (byte) 0);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.THIN_GLASS), name = "Glass pane", lore = {"380 ggs"})
    )
    public void buyGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.THIN_GLASS, 380);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = (Material.IRON_FENCE), name = "Iron bars", lore = {"450 ggs"})
    )
    public void buyIronBars(MenuPlayer player) {
        getUser(player).buyBlock(Material.IRON_FENCE, 450);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = (Material.IRON_BLOCK), name = "Block of iron", lore = {"480 ggs"})
    )
    public void buyIron(MenuPlayer player) {
        getUser(player).buyBlock(Material.IRON_BLOCK, 480);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }
}