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

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.BOLD;

@MenuInventory(slots = 5, name = "Rank Shop")
public class RankShop extends Menu {
    public RankShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = (Material.WOOD), name = "Basic", lore = { "§l§6Start buying blocks from the block shop!", "500 ggs" })
    )
    public void BasicRank(MenuPlayer player) {
        buyRank(player, "Basic");
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = (Material.IRON_BLOCK), name = "Advanced", lore = { "§l§6Buy more durable blocks!", "30,000 ggs"})
    )
    public void AdvanceRank(MenuPlayer player) {
        buyRank(player, "Advanced");
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.LAVA_BUCKET), name = "Survivor", lore = { "§l§6Are you a survivor? Prove yourself!", "120,000 ggs"})
    )
    public void SurvivorRank(MenuPlayer player) {
        buyRank(player, "Survivor");
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.APPLE), name = "Trusted", lore = { "§l§6Decorate that nice house with some furniture!", "230,000 ggs"})
    )
    public void TrustedRank(MenuPlayer player) {
        buyRank(player, "Trusted");
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.GOLDEN_APPLE), name = "Elder", lore = { "§l§6Only true elders can achieve this rank..are you one of them?", "610,000 ggs"})
    )
    public void ElderRank(MenuPlayer player) {
        buyRank(player, "Elder");
    }

    public void buyRank(MenuPlayer player, String RANK) {
        RankManager manager = Lavasurvival.INSTANCE.getRankManager();
        UserInfo user = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        int curNum = manager.getOrder().indexOf(user.getRank());
        int thisRank = manager.getOrder().indexOf(manager.getRank(RANK));
        if (curNum == thisRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "You are already the " + RANK + " rank..");
        } else if (curNum > thisRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "You cannot buy a lower rank..");
        } else if (curNum - 1 < thisRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "You must first buy the " + manager.getRank(RANK).getPrevious().getName() + " rank!");
        } else {
            user.setRank(user.getRank().getNext());
            Lavasurvival.globalMessage(user.getPlayer().getDisplayName() + ChatColor.GREEN + " just bought the " + user.getRank().getName() + " rank!");
            player.getBukkit().getOpenInventory().close();
        }
    }
}
