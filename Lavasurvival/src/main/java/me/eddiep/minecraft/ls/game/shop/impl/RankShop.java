package me.eddiep.minecraft.ls.game.shop.impl;

import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import me.eddiep.minecraft.ls.Lavasurvival;
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
            item = @ItemStackAnnotation(material = Material.WOOD, name = "Basic", lore = {"§l§6Start buying blocks!", "7000 ggs"})
    )
    public void BasicRank(MenuPlayer player) {
        buyRank(player, "Basic", 7000);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.IRON_BLOCK, name = "Advanced", lore = {"§6§lBuy more durable blocks!", "30,000 ggs"})
    )
    public void AdvanceRank(MenuPlayer player) {
        buyRank(player, "Advanced", 30000);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.LAVA_BUCKET, name = "Survivor", lore = {"§6§lAre you a survivor?", "§6§lProve yourself!", "90,000 ggs"})
    )
    public void SurvivorRank(MenuPlayer player) {
        buyRank(player, "Survivor", 90000);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.APPLE, name = "Trusted", lore = {"§6§lDecorate that nice", "§6§lhouse with some", "§6§lfurniture!", "180,000 ggs"})
    )
    public void TrustedRank(MenuPlayer player) {
        buyRank(player, "Trusted", 180000);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.GOLDEN_APPLE, name = "Elder", lore = {"§6§lOnly true elders", "§6§lcan achieve this", "§6§lrank..are you", "§6§lone of them?", "300,000 ggs"})
    )
    public void ElderRank(MenuPlayer player) {
        buyRank(player, "Elder", 300000);
    }

    public void buyRank(MenuPlayer player, String RANK, double price) {
        RankManager manager = Lavasurvival.INSTANCE.getRankManager();
        User user = Lavasurvival.INSTANCE.getNecessitiesUserManager().getUser(player.getBukkit().getUniqueId());
        int curNum = manager.getOrder().indexOf(user.getRank());
        int thisRank = manager.getOrder().indexOf(manager.getRank(RANK));
        if (curNum == thisRank)
            player.getBukkit().sendMessage(ChatColor.RED + "You are already the " + RANK + " rank..");
        else if (curNum > thisRank)
            player.getBukkit().sendMessage(ChatColor.RED + "You cannot buy a lower rank..");
        else if (thisRank - 1 != curNum)
            player.getBukkit().sendMessage(ChatColor.RED + "You must first buy the " + manager.getRank(RANK).getPrevious().getName() + " rank!");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(player.getBukkit()) || Lavasurvival.INSTANCE.getEconomy().getBalance(player.getBukkit()) < price)
            player.getBukkit().sendMessage(ChatColor.RED + "You do not have enough money to buy the " + manager.getRank(RANK).getName() + " rank!");
        else {
            user.setRank(user.getRank().getNext());
            Lavasurvival.INSTANCE.withdrawAndUpdate(player.getBukkit(), price);
            Lavasurvival.globalMessage(user.getPlayer().getDisplayName() + ChatColor.GREEN + " just bought the " + user.getRank().getName() + " rank!");
            player.getBukkit().getOpenInventory().close();
        }
    }
}