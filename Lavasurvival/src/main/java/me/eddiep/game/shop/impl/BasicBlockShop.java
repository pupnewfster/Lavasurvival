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
    public BasicBlockShop(MenuManager manager, Inventory inv) {
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
        item = @ItemStackAnnotation(material = (Material.STONE), name = "Stone", lore = {"150 ggs"})
    )
    public void buyStone(MenuPlayer player) {
        buyBlock(player, Material.STONE, 150);
    }

    private void buyBlock(MenuPlayer player, Material mat, double price) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(u.ownsBlock(mat))
            player.getBukkit().sendMessage(ChatColor.RED + "You already own that block..");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(player.getBukkit()) || Lavasurvival.INSTANCE.getEconomy().getBalance(player.getBukkit()) < price) {
            player.getBukkit().sendMessage(ChatColor.RED + "You do not have enough money to buy the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + "..");
        } else {
            u.addBlock(mat);
            player.getBukkit().sendMessage(ChatColor.GREEN + "You bought the block type " + mat.toString().replaceAll("_", " ").toLowerCase() + "!");
        }
    }
}