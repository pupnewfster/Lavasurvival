package me.eddiep.minecraft.ls.game.shop;

import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopFactory {
    private static ArrayList<Shop> shops = new ArrayList<>();

    public static Shop createShop(Plugin plugin, String ShopName, ShopManager shopManager, Material material, List<String> description, boolean haveGlow) {
        Shop shop = new Shop(shopManager);
        shop.createOpenItem(material, ShopName, description, haveGlow);
        shop.register(plugin);
        shops.add(shop);
        return shop;
    }

    public static List<Shop> getShopList() {
        return Collections.unmodifiableList(shops);
    }

    private static Shop findShop(String shopName) {
        for (Shop shop : shops)
            if (shop.getShopName().equals(shopName))
                return shop;
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
        shops.stream().filter(shop -> !inventory.contains(shop.getOpener())).forEach(shop -> inventory.setItem(inventory.firstEmpty(), shop.getOpener()));
        return inventory;
    }

    public static void cleanup() {
        shops.forEach(Shop::unregister);
        shops.clear();
    }

    public static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null)
            tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
}