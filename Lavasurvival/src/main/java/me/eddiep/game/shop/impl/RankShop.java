package me.eddiep.game.shop.impl;

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

    }

    @MenuItem(
            slot = 1,
            item = @ItemStackAnnotation(material = (Material.IRON_BLOCK), name = "Advanced", lore = { "§l§6Buy more durable blocks!", "30,000 ggs"})
    )
    public void AdvanceRank(MenuPlayer player) {

    }

    @MenuItem(
            slot = 2,
            item = @ItemStackAnnotation(material = (Material.LAVA_BUCKET), name = "Survivor", lore = { "§l§6Are you a survivor? Prove yourself!", "120,000 ggs"})
    )
    public void SurvivorRank(MenuPlayer player) {

    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.APPLE), name = "Trusted", lore = { "§l§6Decorate that nice house with some furniture!", "230,000 ggs"})
    )
    public void TrustedRank(MenuPlayer player) {

    }

    @MenuItem(
            slot = 3,
            item = @ItemStackAnnotation(material = (Material.GOLDEN_APPLE), name = "Elder", lore = { "§l§6Only true elders can achieve this rank..are you one of them?", "610,000 ggs"})
    )
    public void ElderRank(MenuPlayer player) {

    }
}
