package me.eddiep.game.shop;

import me.eddiep.Lavasurvival;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Shop implements Listener {
    private String bossShopName;
    private ItemStack opener;
    private String shopName;
    private Plugin plugin;

    public Shop(String bossShopName) {
        this.bossShopName = bossShopName;
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
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem() != null && event.getItem().equals(opener)) {
                event.getPlayer().sendMessage("We got no shop yet son.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().equals(opener)) {
            event.setCancelled(true);
        }
    }

    public String getShopName() {
        return shopName;
    }
}
