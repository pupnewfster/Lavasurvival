package me.eddiep.minecraft.ls.game.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Shop implements Listener {
    private ItemStack opener;
    private String shopName;
    private Plugin plugin;
    private ShopManager manager;

    public Shop(ShopManager manager) {
        this.manager = manager;
    }

    public ItemStack createOpenItem(Material material, String ShopName, List<String> descriptions, boolean haveGlow) {
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + ShopName + " " + ChatColor.GRAY + "(Right Click)");
        meta.setLore(descriptions);

        item.setItemMeta(meta);

        if (haveGlow)
            item = ShopFactory.addGlow(item);

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
            manager.shopClicked(event.getPlayer(), this);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClosed(InventoryCloseEvent event) {
        Player p = (Player)event.getPlayer();
        if (manager.isShopInventory(event.getInventory(), p)) {
            manager.shopClosed(p, event.getInventory(), this);
        }
    }

    public String getShopName() {
        return shopName;
    }
}