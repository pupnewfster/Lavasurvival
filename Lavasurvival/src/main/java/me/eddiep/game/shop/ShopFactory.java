package me.eddiep.game.shop;

import net.njay.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopFactory {
    private static ArrayList<Shop> shops = new ArrayList<Shop>();

    public static Shop createShop(Plugin plugin, String ShopName, Class<? extends Menu> shopMenu, Material material, List<String> description) {
        Shop shop = new Shop(shopMenu);

        shop.createOpenItem(material, ShopName, description);

        shop.register(plugin);

        shops.add(shop);

        return shop;
    }

    public static List<Shop> getShopList() {
        return Collections.unmodifiableList(shops);
    }

    public static Shop findShop(String shopName) {
        for (Shop shop : shops) {
            if (shop.getShopName().equals(shopName))
                return shop;
        }
        return null;
    }

    public static boolean doesShopExist(String shopName) {
        return findShop(shopName) != null;
    }

    public static Shop unregisterShop(String shopName) {
        Shop shop = findShop(shopName);

        if (shop != null)
            shop.unregister();
        return shop;
    }

    public static Inventory validateInventory(Inventory inventory) {
        for (Shop shop : shops) {
            if (!inventory.contains(shop.getOpener()))
                inventory.setItem(inventory.firstEmpty(), shop.getOpener());
        }

        return inventory;
    }

    public static void cleanup() {
        for (Shop shop : shops) {
            shop.unregister();
        }
        shops.clear();
    }
}
