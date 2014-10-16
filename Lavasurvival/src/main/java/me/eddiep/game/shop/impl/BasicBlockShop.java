package me.eddiep.game.shop.impl;

import me.eddiep.Lavasurvival;
import me.eddiep.ranks.UserInfo;
import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.player.MenuPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@MenuInventory(slots = 9, name = "Basic Block Shop")
public class BasicBlockShop extends Menu {
    private static final String RANK = "Basic";
    private boolean isCorrectRank = true;

    public BasicBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    public BasicBlockShop(MenuManager manager, Inventory inv, boolean isRanked) {
        super(manager, inv);

        this.isCorrectRank = isRanked;
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
            item = @ItemStackAnnotation(material = (Material.GRAVEL), name = "Gravel", lore = {"75 ggs"})
    )
    public void buyGravel(MenuPlayer player) {
        if (!isCorrectRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "Sorry! You must be " + ChatColor.BOLD + ChatColor.ITALIC + RANK + ChatColor.RESET + ChatColor.RED + " to buy that block..");
        }

        getUser(player).buyBlock(Material.GRAVEL, 75);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.STONE), name = "Stone", lore = {"100 ggs"})
    )
    public void buyStone(MenuPlayer player) {
        if (!isCorrectRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "Sorry! You must be " + ChatColor.BOLD + ChatColor.ITALIC + RANK + ChatColor.RESET + ChatColor.RED + " to buy that block..");
        }

        getUser(player).buyBlock(Material.STONE, 100);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.SANDSTONE), name = "Sandstone", lore = {"180 ggs"})
    )
    public void buySandstone(MenuPlayer player) {
        if (!isCorrectRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "Sorry! You must be " + ChatColor.BOLD + ChatColor.ITALIC + RANK + ChatColor.RESET + ChatColor.RED + " to buy that block..");
        }

        getUser(player).buyBlock(Material.SANDSTONE, 180);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.BRICK), name = "Brick", lore = {"200 ggs"})
    )
    public void buyBrick(MenuPlayer player) {
        if (!isCorrectRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "Sorry! You must be " + ChatColor.BOLD + ChatColor.ITALIC + RANK + ChatColor.RESET + ChatColor.RED + " to buy that block..");
        }

        getUser(player).buyBlock(Material.BRICK, 200);
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }
}