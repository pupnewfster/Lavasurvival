package me.eddiep.minecraft.ls.game.shop.impl;

import me.eddiep.minecraft.ls.game.shop.Shop;
import me.eddiep.minecraft.ls.game.shop.ShopManager;
import net.njay.Menu;
import net.njay.MenuFramework;
import net.njay.MenuManager;
import net.njay.player.MenuPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Constructor;

public class MenuShopManager implements ShopManager {
    private Constructor<? extends Menu> menu;

    public MenuShopManager(Class<? extends Menu> class_) {
        try {
            menu = class_.getConstructor(MenuManager.class, Inventory.class, Player.class);
        } catch (NoSuchMethodException e) {
            try {
                menu = class_.getConstructor(MenuManager.class, Inventory.class);
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void shopClicked(Player owner, Shop shop) {
        MenuPlayer player = MenuFramework.getPlayerManager().getPlayer(owner);
        try {
            player.setActiveMenuAndReplace(menu.getParameterTypes().length == 3 ? menu.newInstance(player.getMenuManager(), null, owner) : menu.newInstance(player.getMenuManager(), null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isShopInventory(Inventory inventory, Player owner) {
        MenuPlayer player = MenuFramework.getPlayerManager().getPlayer(owner);
        return player.getActiveMenu() != null && player.getActiveMenu().getInventory().equals(inventory);

    }

    @Override
    public void shopClosed(Player owner, Inventory inventory, Shop shop) {
    } //Do nothing
}
