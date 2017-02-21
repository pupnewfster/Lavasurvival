package me.eddiep.minecraft.ls.game.shop;

import me.eddiep.minecraft.ls.game.shop.impl.*;
import me.eddiep.minecraft.ls.ranks.RankType;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.njay.Menu;
import net.njay.annotation.MenuItem;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class ShopFactory {
    private static final ArrayList<Shop> shops = new ArrayList<>();

    @SuppressWarnings("UnusedReturnValue")
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
        return shops.stream().filter(shop -> shop.getShopName().equals(shopName)).findFirst().orElse(null);
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

    private static HashMap<RankType, List<MaterialData>> cachedBlocks = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static List<MaterialData> getBlocksFor(RankType rank) {
        if (cachedBlocks.containsKey(rank))
            return cachedBlocks.get(rank);

        Class<? extends Menu> _class;
        switch (rank) {
            case BASIC:
                _class = BasicBlockShop.class;
                break;
            case ADVANCED:
                _class = AdvancedBlockShop.class;
                break;
            case SURVIVOR:
                _class = SurvivorBlockShop.class;
                break;
            case TRUSTED:
                _class = TrustedBlockShop.class;
                break;
            case ELDER:
                _class = ElderBlockShop.class;
                break;
            default:
                return new ArrayList<>();
        }

        List<MaterialData> list = new ArrayList<>();
        Method[] methods = _class.getDeclaredMethods();
        for (Method m : methods) {
            MenuItem item = m.getAnnotation(MenuItem.class);

            if (item != null) {
                Material material = item.item().material();
                if (material.equals(Material.EMERALD)) //Ignore the return to previous page item
                    continue;
                list.add(new MaterialData(material, (byte) item.item().durability())); //Get the durability to make sure type is same white and black wool
            }
        }

        cachedBlocks.put(rank, list);
        return list;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Inventory validateInventory(Inventory inventory) {
        shops.stream().filter(shop -> !inventory.contains(shop.getOpener())).forEach(shop -> inventory.setItem(inventory.firstEmpty(), shop.getOpener()));
        return inventory;
    }

    public static void cleanup() {
        shops.forEach(Shop::unregister);
        shops.clear();
    }

    @SuppressWarnings("ConstantConditions")
    public static ItemStack addGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null)
            tag = nmsStack.getTag();
        tag.setInt("HideFlags", 63);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
}