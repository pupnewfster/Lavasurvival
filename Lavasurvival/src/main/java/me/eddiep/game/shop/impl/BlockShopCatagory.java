package me.eddiep.game.shop.impl;

import me.eddiep.Lavasurvival;
import me.eddiep.ranks.RankManager;
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

@MenuInventory(slots = 9, name = "Block Shop")
public class BlockShopCatagory extends Menu {
    RankManager rm = Lavasurvival.INSTANCE.getRankManager();

    public BlockShopCatagory(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
        slot = 0,
        item = @ItemStackAnnotation(material = (Material.WOOD), name = "Buy more blocks!")
    )
    public void BasicRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(!rm.hasRank(u.getRank(), rm.getRank("Basic")))
            player.getBukkit().sendMessage(ChatColor.RED + "You do not have access to the shop for the Basic rank..");
        else
            player.setActiveMenu(new BasicBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
        slot = 1,
        item = @ItemStackAnnotation(material = (Material.IRON_BLOCK), name = "Advanced Shop")
    )
    public void AdvanceRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(!rm.hasRank(u.getRank(), rm.getRank("Advanced")))
            player.getBukkit().sendMessage(ChatColor.RED + "You do not have access to the shop for the Advanced rank..");
        else
            player.setActiveMenu(new AdvancedBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
        slot = 2,
        item = @ItemStackAnnotation(material = (Material.FENCE_GATE), name = "Survivor Shop")
    )
    public void SurvivorRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(!rm.hasRank(u.getRank(), rm.getRank("Survivor")))
            player.getBukkit().sendMessage(ChatColor.RED + "You do not have access to the shop for the Survivor rank..");
        else
            player.setActiveMenu(new SurvivorBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
        slot = 3,
        item = @ItemStackAnnotation(material = (Material.IRON_FENCE), name = "Trusted Shop")
    )
    public void TrustedRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(!rm.hasRank(u.getRank(), rm.getRank("Trusted")))
            player.getBukkit().sendMessage(ChatColor.RED + "You do not have access to the shop for the Trusted rank..");
        else
            player.setActiveMenu(new TrustedBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
        slot = 4,
        item = @ItemStackAnnotation(material = (Material.NETHER_BRICK), name = "Elder Shop")
    )
    public void ElderRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(!rm.hasRank(u.getRank(), rm.getRank("Elder")))
            player.getBukkit().sendMessage(ChatColor.RED + "You do not have access to the shop for the Elder rank..");
        else
            player.setActiveMenu(new ElderBlockShop(player.getMenuManager(), null));
    }
}