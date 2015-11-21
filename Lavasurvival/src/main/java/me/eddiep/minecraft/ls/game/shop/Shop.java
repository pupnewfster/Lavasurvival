package me.eddiep.minecraft.ls.game.shop;

import net.njay.Menu;
import net.njay.MenuFramework;
import net.njay.MenuManager;
import net.njay.player.MenuPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.List;

public class Shop implements Listener {
    private ItemStack opener;
    private String shopName;
    private Plugin plugin;
    private Constructor<? extends Menu> menu;

    public Shop(Class<? extends Menu> class_) {
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

    public ItemStack createOpenItem(Material material, String ShopName, List<String> descriptions) {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + ShopName + " " + ChatColor.GRAY + "(Right Click)");
        meta.setLore(descriptions);

        item.setItemMeta(meta);

        this.opener = item;
        this.shopName = ShopName;

        return opener;
    }

    public ItemStack getOpener() {
        return opener;
    }

    public Plugin getPluginOwner() {
        return plugin;
    }

    public void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        opener = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null && event.getItem().equals(opener)) {
            MenuPlayer player = MenuFramework.getPlayerManager().getPlayer(event.getPlayer());
            try {
                Menu menuObj;
                if (menu.getParameterTypes().length == 3)
                    menuObj = menu.newInstance(player.getMenuManager(), null, event.getPlayer());
                else
                    menuObj = menu.newInstance(player.getMenuManager(), null);

                player.setActiveMenuAndReplace(menuObj, true);
                event.setCancelled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().equals(opener))
            event.setCancelled(true);
    }

    public String getShopName() {
        return shopName;
    }
}