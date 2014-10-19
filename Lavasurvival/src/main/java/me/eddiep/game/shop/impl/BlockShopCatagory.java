package me.eddiep.game.shop.impl;

import me.eddiep.Lavasurvival;
import me.eddiep.ranks.RankManager;
import me.eddiep.ranks.UserInfo;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@MenuInventory(slots = 9, name = "Block Shop")
public class BlockShopCatagory extends Menu {
    RankManager rm = Lavasurvival.INSTANCE.getRankManager();
    private Player player;
    public BlockShopCatagory(MenuManager manager, Inventory inv, Player player) {
        super(manager, inv);
        this.player = player;
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.WOOD), name = "Basic Shop!")
    )
    public void BasicRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(rm.hasRank(u.getRank(), rm.getRank("Basic")))
            player.setActiveMenu(new BasicBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.IRON_BLOCK), name = "Advanced Shop")
    )
    public void AdvanceRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(rm.hasRank(u.getRank(), rm.getRank("Advanced")))
            player.setActiveMenu(new AdvancedBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.FENCE_GATE), name = "Survivor Shop")
    )
    public void SurvivorRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(rm.hasRank(u.getRank(), rm.getRank("Survivor")))
            player.setActiveMenu(new SurvivorBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = (Material.IRON_FENCE), name = "Trusted Shop")
    )
    public void TrustedRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(rm.hasRank(u.getRank(), rm.getRank("Trusted")))
            player.setActiveMenu(new TrustedBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = (Material.NETHER_BRICK), name = "Elder Shop")
    )
    public void ElderRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        if(rm.hasRank(u.getRank(), rm.getRank("Elder")))
            player.setActiveMenu(new ElderBlockShop(player.getMenuManager(), null));
    }

    @PreProcessor
    public void process(Inventory inv){
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(this.player.getUniqueId());
        if (!rm.hasRank(u.getRank(), rm.getRank("Basic"))) {
            ItemStack i = inv.getItem(2);
            ItemMeta m = i.getItemMeta();
            m.setLore(Arrays.asList(ChatColor.RED + "" + ChatColor.BOLD + "You are not skilled enough to enter!",
                    ChatColor.RED + "" + ChatColor.ITALIC + "Requires: Basic"));
            i.setItemMeta(m);
            inv.setItem(2, i);
        }
        if (!rm.hasRank(u.getRank(), rm.getRank("Advanced"))) {
            ItemStack i = inv.getItem(3);
            ItemMeta m = i.getItemMeta();
            m.setLore(Arrays.asList(ChatColor.RED + "" + ChatColor.BOLD + "You are not skilled enough to enter!",
                    ChatColor.RED + "" + ChatColor.ITALIC + "Requires: Advanced"));
            i.setItemMeta(m);
            inv.setItem(3, i);
        }
        if (!rm.hasRank(u.getRank(), rm.getRank("Survivor"))) {
            ItemStack i = inv.getItem(4);
            ItemMeta m = i.getItemMeta();
            m.setLore(Arrays.asList(ChatColor.RED + "" + ChatColor.BOLD + "You are not skilled enough to enter!",
                    ChatColor.RED + "" + ChatColor.ITALIC + "Requires: Survivor"));
            i.setItemMeta(m);
            inv.setItem(4, i);
        }
        if (!rm.hasRank(u.getRank(), rm.getRank("Trusted"))) {
            ItemStack i = inv.getItem(5);
            ItemMeta m = i.getItemMeta();
            m.setLore(Arrays.asList(ChatColor.RED + "" + ChatColor.BOLD + "You are not skilled enough to enter!",
                    ChatColor.RED + "" + ChatColor.ITALIC + "Requires: Trusted"));
            i.setItemMeta(m);
            inv.setItem(5, i);
        }
        if (!rm.hasRank(u.getRank(), rm.getRank("Elder"))) {
            ItemStack i = inv.getItem(6);
            ItemMeta m = i.getItemMeta();
            m.setLore(Arrays.asList(ChatColor.RED + "" + ChatColor.BOLD + "You are not skilled enough to enter!",
                    ChatColor.RED + "" + ChatColor.ITALIC + "Requires: Elder"));
            i.setItemMeta(m);
            inv.setItem(6, i);
        }
    }
}