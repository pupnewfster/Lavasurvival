package me.eddiep.minecraft.ls.game.shop.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.items.LavaItem;
import me.eddiep.minecraft.ls.system.BukkitUtils;
import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.annotation.PreProcessor;
import net.njay.player.MenuPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
@MenuInventory(slots = 9, name = "Item Shop")
public class ItemShop extends Menu {
    public ItemShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(slot = 0, item = @ItemStackAnnotation(material = Material.WOOD, name = ""))
    public void generosity(MenuPlayer player) {
        buyItem(player, LavaItem.GENEROSITY);
    }

    @MenuItem(slot = 1, item = @ItemStackAnnotation(material = Material.WOOD, name = ""))
    public void minorHeal(MenuPlayer player) {
        buyItem(player, LavaItem.MINOR_HEAL);
    }

    @MenuItem(slot = 2, item = @ItemStackAnnotation(material = Material.WOOD, name = ""))
    public void majorHeal(MenuPlayer player) {
        buyItem(player, LavaItem.MAJOR_HEAL);
    }

    @MenuItem(slot = 3, item = @ItemStackAnnotation(material = Material.WOOD, name = ""))
    public void minorInvincibility(MenuPlayer player) {
        buyItem(player, LavaItem.MINOR_INVINCIBILITY);
    }

    @MenuItem(slot = 4, item = @ItemStackAnnotation(material = Material.WOOD, name = ""))
    public void majorInvincibility(MenuPlayer player) {
        buyItem(player, LavaItem.MAJOR_INVINCIBILITY);
    }

    private void buyItem(MenuPlayer player, LavaItem item) {
        ItemStack is = item.createItem();
        Player p = player.getBukkit();
        if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(p) || Lavasurvival.INSTANCE.getEconomy().getBalance(p) < item.getPrice())
            p.sendMessage(ChatColor.RED + "You do not have enough money to buy the item " + item.name() + "..");
        else if (BukkitUtils.isInventoryFull(p.getInventory()))
            p.sendMessage(ChatColor.RED + "You do not have enough inventory space to buy any more items..");
        else {
            p.getInventory().addItem(is);
            Lavasurvival.INSTANCE.withdrawAndUpdate(p, item.getPrice());
            p.sendMessage(ChatColor.GREEN + "You bought the item " + item.name() + "!");
        }
    }

    @PreProcessor
    public void process(Inventory inv) {
        for (int i = 0; i < LavaItem.ITEMS.length; i++) {
            if (i >= inv.getSize())
                break;
            inv.setItem(i, LavaItem.ITEMS[i].createItem());
        }
    }
}