package me.eddiep.minecraft.ls.ranks;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.system.BukkitUtils;
import me.eddiep.minecraft.ls.system.PhysicsListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class UserInfo {
    private ArrayList<MaterialData> ownedBlocks = new ArrayList<>();
    private long lastBreak = System.currentTimeMillis(), blockChangeCount;
    private boolean inWater = false;
    private Player bukkitPlayer;
    private int taskID = 0;
    private UUID userUUID;

    public UserInfo(Player p) {
        this.bukkitPlayer = p;
        this.userUUID = p.getUniqueId();
    }

    public UserInfo(UUID uuid) {
        this.userUUID = uuid;
        load();
    }

    public long getBlockChangeCount() {
        return blockChangeCount;
    }

    public void resetBlockChangeCount() {
        blockChangeCount = 0;
    }

    public void load() {
        //TODO Load all persistent data
    }

    public void save() {
        //TODO Save all persistent data
    }

    public void logOut() {
        this.bukkitPlayer = null;
        save();
    }

    public long getLastBreak() {
        return this.lastBreak;
    }

    public void setLastBreak(long lastBreak) {
        this.lastBreak = lastBreak;
    }

    public UUID getUUID() {
        return getPlayer() == null ? this.userUUID: getPlayer().getUniqueId();
    }

    public String getName() {
        return getPlayer() == null ? Bukkit.getOfflinePlayer(getUUID()).getName() : getPlayer().getName();
    }

    public boolean isInWater() {
        return this.inWater;
    }

    public void setInWater(boolean value) {
        this.inWater = value;
        if(!isInWater())
            Bukkit.getScheduler().cancelTask(this.taskID);
        if(value && getPlayer() != null && Gamemode.getCurrentGame() != null && Gamemode.WATER_DAMAGE != 0 && Gamemode.getCurrentGame().isAlive(getPlayer()))
            this.taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    if (isInWater() && getPlayer() != null) {
                        getPlayer().damage(Gamemode.WATER_DAMAGE);
                        Block b = getPlayer().getLocation().getBlock();
                        setInWater(((b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER)) && b.hasMetadata("classic_block")) ||
                                ((b.getRelative(BlockFace.UP).getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER)) &&
                                        b.getRelative(BlockFace.UP).hasMetadata("classic_block")));
                    }
                }
            }, (int) (20 * Gamemode.DAMAGE_FREQUENCY));
    }

    public Player getPlayer() {
        return this.bukkitPlayer;
    }

    public void setPlayer(Player p) {
        this.bukkitPlayer = p;
    }

    public void giveBoughtBlocks() {
        Player p = getPlayer();
        if(p != null)
            for (MaterialData dat : this.ownedBlocks) {
                if (BukkitUtils.hasItem(p.getInventory(), dat))
                    continue;
                p.getInventory().addItem(getItem(dat));
            }
    }

    private void addBlock(MaterialData dat) {
        if (!this.ownedBlocks.contains(dat)) {
            this.ownedBlocks.add(dat);
            if(getPlayer() != null)
                getPlayer().getInventory().addItem(getItem(dat));
        }
    }

    private ItemStack getItem(MaterialData dat) {
        ItemStack i = dat.toItemStack(1);
        ItemMeta im = i.getItemMeta();
        im.setLore(Arrays.asList("Melt time: " + PhysicsListener.getMeltTimeAsString(dat)));
        i.setItemMeta(im);
        return i;
    }

    public void clearBlocks() {
        if (getPlayer() != null)
            for (MaterialData dat : this.ownedBlocks)
                getPlayer().getInventory().remove(dat.toItemStack());
        this.ownedBlocks.clear();
    }

    public boolean ownsBlock(MaterialData dat) {
        return this.ownedBlocks.contains(dat);
    }

    public void buyBlock(Material mat, double price, byte data) {
        buyBlock(new MaterialData(mat, data), price, true);
    }

    public void buyBlock(Material mat, double price) {
        buyBlock(new MaterialData(mat), price, false);
    }

    private void buyBlock(MaterialData dat, double price, boolean hasData) {
        if (getPlayer() == null)
            return;
        if (ownsBlock(dat))
            getPlayer().sendMessage(ChatColor.RED + "You already own that block..");
        else if (!Lavasurvival.INSTANCE.getEconomy().hasAccount(getPlayer()) || Lavasurvival.INSTANCE.getEconomy().getBalance(getPlayer()) < price)
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to buy the block type " + dat.getItemType().toString().replaceAll("_", " ").toLowerCase() +
                    (hasData ? " with datavalue " + dat.getData() : "") + "..");
        else if (BukkitUtils.isInventoryFull(getPlayer().getInventory()))//TODO: Make an item with an extended inventory so they can buy more blocks
            getPlayer().sendMessage(ChatColor.RED + "You do not have enough inventory space to buy any more blocks..");
        else {
            addBlock(dat);
            Lavasurvival.INSTANCE.withdrawAndUpdate(getPlayer(), price);
            getPlayer().sendMessage(ChatColor.GREEN + "You bought the block type " + dat.getItemType().toString().replaceAll("_", " ").toLowerCase() + (hasData ? " with datavalue " + dat.getData() : "") + "!");
        }
    }

    public void incrimentBlockCount() {
        blockChangeCount++;
    }
}