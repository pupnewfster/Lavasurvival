package me.eddiep.game.shop.impl;

import me.eddiep.Lavasurvival;
import me.eddiep.ranks.UserInfo;
import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.player.MenuPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@MenuInventory(slots = 9, name = "Survivor Block Shop")
public class SurvivorBlockShop extends Menu {
    public SurvivorBlockShop(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = (Material.EMERALD), name = "Back to block shop", lore = {"§6§oBuy more blocks!"})
    )
    public void backToMenu(MenuPlayer player) {
        player.setActiveMenu(new BlockShopCatagory(player.getMenuManager(), null));
    }

    private UserInfo getUser(MenuPlayer player) {
        return Lavasurvival.INSTANCE.getUserManager().getUser(player.getBukkit().getUniqueId());
    }
}