package me.eddiep.minecraft.ls.game.shop.impl;

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
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@MenuInventory(slots = 9, name = "Survivor Block Shop")
public class SurvivorBlockShop extends Menu {
    public SurvivorBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = Material.EMERALD, name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenuAndReplace(new BlockShopCatagory(player.getMenuManager(), null, player.getBukkit()), true);
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = Material.PACKED_ICE, name = "Packed ice")
    )
    public void buyPackedIce(MenuPlayer player) {
        getUser(player).buyBlock(Material.PACKED_ICE, price(Material.PACKED_ICE));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.BRICK, name = "Brick")
    )
    public void buyBrick(MenuPlayer player) {
        getUser(player).buyBlock(Material.BRICK, price(Material.BRICK));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.SMOOTH_BRICK, durability = 0, name = "Stone brick")
    )
    public void buyStoneBrick(MenuPlayer player) {
        getUser(player).buyBlock(Material.SMOOTH_BRICK, price(Material.SMOOTH_BRICK), (byte) 0);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.THIN_GLASS, name = "Glass pane")
    )
    public void buyGlassPane(MenuPlayer player) {
        getUser(player).buyBlock(Material.THIN_GLASS, price(Material.THIN_GLASS));
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.IRON_FENCE, name = "Iron bars")
    )
    public void buyIronBars(MenuPlayer player) {
        getUser(player).buyBlock(Material.IRON_FENCE, price(Material.IRON_FENCE));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.IRON_BLOCK, name = "Block of iron")
    )
    public void buyIron(MenuPlayer player) {
        getUser(player).buyBlock(Material.IRON_BLOCK, price(Material.IRON_BLOCK));
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
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