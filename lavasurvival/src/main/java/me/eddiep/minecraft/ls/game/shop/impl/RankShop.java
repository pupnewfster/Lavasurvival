package me.eddiep.minecraft.ls.game.shop.impl;

import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.shop.ShopFactory;
import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.annotation.PreProcessor;
import net.njay.player.MenuPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@MenuInventory(slots = 9, name = "Rank Shop")
public class RankShop extends Menu {
    public RankShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.WOOD, name = "")
    )
    public void BasicRank(MenuPlayer player) {
        buyRank(player, "Basic", 7000);
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.IRON_BLOCK, name = "")
    )
    public void AdvanceRank(MenuPlayer player) {
        buyRank(player, "Advanced", 30000);
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.LAVA_BUCKET, name = "")
    )
    public void SurvivorRank(MenuPlayer player) {
        buyRank(player, "Survivor", 90000);
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.APPLE, name = "")
    )
    public void TrustedRank(MenuPlayer player) {
        buyRank(player, "Trusted", 180000);
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.GOLDEN_APPLE, name = "")
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

    @PreProcessor
    public void process(Inventory inv) {
        ItemStack basic = new ItemStack(Material.WOOD, 1);
        ItemMeta bmeta = basic.getItemMeta();
        bmeta.setDisplayName("Basic");
        bmeta.setLore(Arrays.asList(ChatColor.GOLD + "" + ChatColor.BOLD + "Start buying blocks!", "7000 ggs"));
        basic.setItemMeta(bmeta);
        basic = ShopFactory.addGlow(basic);
        inv.setItem(2, basic);
        ItemStack advanced = new ItemStack(Material.IRON_BLOCK, 1);
        ItemMeta ameta = advanced.getItemMeta();
        ameta.setDisplayName("Advanced");
        ameta.setLore(Arrays.asList(ChatColor.GOLD + "" + ChatColor.BOLD + "Buy more durable blocks!", "30,000 ggs"));
        advanced.setItemMeta(ameta);
        advanced = ShopFactory.addGlow(advanced);
        inv.setItem(3, advanced);
        ItemStack survivor = new ItemStack(Material.LAVA_BUCKET, 1);
        ItemMeta smeta = survivor.getItemMeta();
        smeta.setDisplayName("Survivor");
        smeta.setLore(Arrays.asList(ChatColor.GOLD + "" + ChatColor.BOLD + "Are you a survivor?", ChatColor.GOLD + "" + ChatColor.BOLD + "Prove yourself!", "90,000 ggs"));
        survivor.setItemMeta(smeta);
        survivor = ShopFactory.addGlow(survivor);
        inv.setItem(4, survivor);
        ItemStack trusted = new ItemStack(Material.APPLE, 1);
        ItemMeta tmeta = trusted.getItemMeta();
        tmeta.setDisplayName("Trusted");
        tmeta.setLore(Arrays.asList(ChatColor.GOLD + "" + ChatColor.BOLD + "Decorate that nice", ChatColor.GOLD + "" + ChatColor.BOLD + "house with some",
                ChatColor.GOLD + "" + ChatColor.BOLD + "furniture!", "180,000 ggs"));
        trusted.setItemMeta(tmeta);
        trusted = ShopFactory.addGlow(trusted);
        inv.setItem(5, trusted);
        ItemStack elder = new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1);
        ItemMeta emeta = elder.getItemMeta();
        emeta.setDisplayName("Elder");
        emeta.setLore(Arrays.asList(ChatColor.GOLD + "" + ChatColor.BOLD + "Only true elders", ChatColor.GOLD + "" + ChatColor.BOLD + "can achieve this",
                ChatColor.GOLD + "" + ChatColor.BOLD + "rank.. are you", ChatColor.GOLD + "" + ChatColor.BOLD + "one of them?", "300,000 ggs"));
        elder.setItemMeta(emeta);
        inv.setItem(6, elder);
    }
}