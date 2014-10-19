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

@MenuInventory(slots = 9, name = "Block Shop")
public class BlockShopCatagory extends Menu {
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
        player.setActiveMenu(new BasicBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.IRON_BLOCK), name = "Advanced Shop")
    )
    public void AdvanceRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        player.setActiveMenu(new AdvancedBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = (Material.FENCE_GATE), name = "Survivor Shop")
    )
    public void SurvivorRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        player.setActiveMenu(new SurvivorBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = (Material.IRON_FENCE), name = "Trusted Shop")
    )
    public void TrustedRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        player.setActiveMenu(new TrustedBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = (Material.NETHER_BRICK), name = "Elder Shop")
    )
    public void ElderRank(MenuPlayer player) {
        UserInfo u = Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
        player.setActiveMenu(new ElderBlockShop(player.getMenuManager(), null));
    }

    @PreProcessor
    public void process(Inventory inv){
        Lavasurvival.globalMessage(player.getDisplayName() + " opened the inventory o.o");
    }
}