package me.eddiep.minecraft.ls.game.shop.impl;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.shop.Shop;
import me.eddiep.minecraft.ls.game.shop.ShopManager;
import me.eddiep.minecraft.ls.ranks.UserInfo;
import me.eddiep.minecraft.ls.ranks.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BankShopManager implements ShopManager {
    @Override
    public void shopClicked(Player owner, Shop shop) {
        UserManager um = Lavasurvival.INSTANCE.getUserManager();

        UserInfo user = um.getUser(owner.getUniqueId());

        Inventory bankInventory = user.createBankInventory();

        owner.openInventory(bankInventory);
    }

    @Override
    public boolean isShopInventory(Inventory inventory, Player owner) {
        return inventory.getTitle().equals("Bank");
    }

    @Override
    public void shopClosed(Player owner, Inventory inventory, Shop shop) {
        UserManager um = Lavasurvival.INSTANCE.getUserManager();

        UserInfo user = um.getUser(owner.getUniqueId());

        user.saveBank(inventory);
    }
}
