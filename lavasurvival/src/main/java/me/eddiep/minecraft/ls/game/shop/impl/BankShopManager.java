package me.eddiep.minecraft.ls.game.shop.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.shop.Shop;
import me.eddiep.minecraft.ls.game.shop.ShopManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BankShopManager implements ShopManager {
    @Override
    public void shopClicked(Player owner, Shop shop) {
        Lavasurvival.INSTANCE.getUserManager().getUser(owner.getUniqueId()).createBankInventory(owner);
    }

    @Override
    public boolean isShopInventory(Inventory inventory, Player owner) {
        return inventory.getTitle().contains("Bank");
    }

    @Override
    public void shopClosed(Player owner, Inventory inventory, Shop shop) {
        if (inventory.contains(shop.getOpener())) {
            inventory.remove(shop.getOpener());
            inventory.setItem(owner.getInventory().firstEmpty(), shop.getOpener());
        }
        Lavasurvival.INSTANCE.getUserManager().getUser(owner.getUniqueId()).saveBank();
    }
}
