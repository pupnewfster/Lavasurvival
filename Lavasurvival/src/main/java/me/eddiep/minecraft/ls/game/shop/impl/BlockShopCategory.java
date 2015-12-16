package me.eddiep.minecraft.ls.game.shop.impl;

import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.player.MenuPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@MenuInventory(slots = 9, name = "Block Shop")
public class BlockShopCategory extends Menu {
    public BlockShopCategory(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = Material.WOOD, name = "Basic Shop!")
    )
    public void BasicRank(MenuPlayer player) {
        player.setActiveMenu(new BasicBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = Material.IRON_BLOCK, name = "Advanced Shop")
    )
    public void AdvanceRank(MenuPlayer player) {
        player.setActiveMenu(new AdvancedBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = Material.FENCE_GATE, name = "Survivor Shop")
    )
    public void SurvivorRank(MenuPlayer player) {
        player.setActiveMenu(new SurvivorBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 4,
            item = @ItemStackAnnotation(material = Material.IRON_FENCE, name = "Trusted Shop")
    )
    public void TrustedRank(MenuPlayer player) {
        player.setActiveMenu(new TrustedBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 5,
            item = @ItemStackAnnotation(material = Material.NETHER_BRICK, name = "Elder Shop")
    )
    public void ElderRank(MenuPlayer player) {
        player.setActiveMenu(new ElderBlockShop(player.getMenuManager(), null));
    }

    @MenuItem(
            slot = 6,
            item = @ItemStackAnnotation(material = Material.RED_ROSE, name = "Donator Shop", lore = {"Decorative blocks"})
    )
    public void Dontator(MenuPlayer player) {
        player.setActiveMenu(new DonatorBlockShop(player.getMenuManager(), null));
    }
}