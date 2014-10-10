package me.eddiep.game.shop.impl;

import net.njay.Menu;
import net.njay.MenuManager;
import net.njay.annotation.ItemStackAnnotation;
import net.njay.annotation.MenuInventory;
import net.njay.annotation.MenuItem;
import net.njay.player.MenuPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

@MenuInventory(slots = 5, name = "Block Shop")
public class BlockShopCatagory extends Menu {
    public BlockShopCatagory(MenuManager manager, Inventory inv) {
        super(manager, inv);
    }

    @MenuItem(
            slot = 0,
            item = @ItemStackAnnotation(material = (Material.WOOD), name = "Basic Shop")
    )
    public void BasicRank(MenuPlayer player) {

    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = (Material.IRON_BLOCK), name = "Advanced Shop")
    )
    public void AdvanceRank(MenuPlayer player) {

    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.FENCE_GATE), name = "Survivor Shop")
    )
    public void SurvivorRank(MenuPlayer player) {

    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.IRON_FENCE), name = "Trusted Shop")
    )
    public void TrustedRank(MenuPlayer player) {

    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.NETHER_BRICK), name = "Elder Shop")
    )
    public void ElderRank(MenuPlayer player) {

    }
}
