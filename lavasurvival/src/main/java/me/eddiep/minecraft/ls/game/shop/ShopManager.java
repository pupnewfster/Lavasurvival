package me.eddiep.minecraft.ls.game.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ShopManager {
    /**
     * This function is called when the player clicks the shop icon
     *
     * @param owner The player who clicked
     * @param shop  The shop that was opened
     */
    @SuppressWarnings("unused")
    void shopClicked(Player owner, Shop shop);

    /**
     * This method should return true if the inventory provided is a shop inventory
     *
     * @param inventory The inventory to check
     * @param owner     The owner of this inventory
     * @return True if this inventory is a shop inventory, otherwise false
     */
    boolean isShopInventory(Inventory inventory, Player owner);

    /**
     * This function is called when the player closes a shop
     *
     * @param owner     The player who closed
     * @param inventory The inventory that was closed that caused this event
     * @param shop      The shop that was closed
     */
    void shopClosed(Player owner, Inventory inventory, Shop shop);
}