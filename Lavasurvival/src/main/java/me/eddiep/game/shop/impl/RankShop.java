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

@MenuInventory(slots = 9, name = "Rank Shop")
public class RankShop extends Menu {
    public RankShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.WOOD), name = "Basic", lore = { "§l§6Start buying blocks!", "500 ggs" })
    )
    public void BasicRank(MenuPlayer player) {
        buyRank(player, "Basic", 500);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.IRON_BLOCK), name = "Advanced", lore = { "§l§6Buy more durable blocks!", "30,000 ggs"})
    )
    public void AdvanceRank(MenuPlayer player) {
        buyRank(player, "Advanced", 30000);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.LAVA_BUCKET), name = "Survivor", lore = { "§l§6Are you a survivor?", "§l§6Prove yourself!", "120,000 ggs"})
    )
    public void SurvivorRank(MenuPlayer player) {
        buyRank(player, "Survivor", 120000);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = (Material.APPLE), name = "Trusted", lore = { "§l§6Decorate that nice", "§l§6house with some", "§l§6furniture!", "230,000 ggs"})
    )
    public void TrustedRank(MenuPlayer player) {
        buyRank(player, "Trusted", 230000);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = (Material.GOLDEN_APPLE), name = "Elder", lore = { "§l§6Only true elders", "§l§6can achieve this", "§l§6rank..are you", "§l§6one of them?", "610,000 ggs"})
    )
    public void ElderRank(MenuPlayer player) {
        buyRank(player, "Elder", 610000);
    }

    public void buyRank(MenuPlayer player, String RANK, double price) {
        RankManager manager = Lavasurvival.INSTANCE.getRankManager();
        UserInfo user = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        int curNum = manager.getOrder().indexOf(user.getRank());
        int thisRank = manager.getOrder().indexOf(manager.getRank(RANK));
        if (curNum == thisRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "You are already the " + RANK + " rank..");
        } else if (curNum > thisRank) {
            player.getBukkit().sendMessage(ChatColor.RED + "You cannot buy a lower rank..");
        } else if (thisRank - 1 != curNum) {
            player.getBukkit().sendMessage(ChatColor.RED + "You must first buy the " + manager.getRank(RANK).getPrevious().getName() + " rank!");
        } else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(player.getBukkit()) || Lavasurvival.INSTANCE.getEconomy().getBalance(player.getBukkit()) < price) {
            player.getBukkit().sendMessage(ChatColor.RED + "You do not have enough money to buy the " + manager.getRank(RANK).getPrevious().getName() + " rank!");
        } else {
            user.setRank(user.getRank().getNext());
            Lavasurvival.INSTANCE.getEconomy().withdrawPlayer(player.getBukkit(), price);
            Lavasurvival.globalMessage(user.getPlayer().getDisplayName() + ChatColor.GREEN + " just bought the " + user.getRank().getName() + " rank!");
            player.getBukkit().getOpenInventory().close();
        }
    }
}
